package com.example.numbletimedealserver.restdocs

import com.example.numbletimedealserver.DocsFieldType
import org.springframework.restdocs.payload.FieldDescriptor




import com.example.numbletimedealserver.*
import com.example.numbletimedealserver.domain.Customer
import com.example.numbletimedealserver.domain.ROLE
import com.example.numbletimedealserver.dto.CustomerDto
import com.example.numbletimedealserver.repository.customer.CustomerRepository
import com.example.numbletimedealserver.request.LoginRequest
import com.example.numbletimedealserver.request.SignUpRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@Import(RestDocsConfig::class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@Transactional
class 록CustomerControllerDocs @Autowired constructor(
    private val mapper:ObjectMapper,
    private val customerRepository: CustomerRepository,
) {
    @Autowired
    lateinit var mockMvc: MockMvc

    val signUpRequest = SignUpRequest("inu1", "test2", ROLE.ADMIN)
    lateinit var customer: Customer
    val loginRequest = LoginRequest(signUpRequest.name, signUpRequest.pw)

    @BeforeEach
    fun setup() {
        customer = customerRepository.save(Customer(signUpRequest.name, signUpRequest.pw, signUpRequest.role))

    }

    @AfterEach
    fun delete() {
        customerRepository.deleteAll()
    }

    @Test
    fun signup() {
        val mysignup = SignUpRequest("inu2", "12345", ROLE.USER)
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/signup").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mysignup)).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isString)
            .andExpect(jsonPath("$.name").value(mysignup.name))
            .andExpect(jsonPath("$.role").value(mysignup.role.toString()))
            .andDo(
                document(
                    myIdentifier("회원가입"),
                    requestFields(
                        fieldWithPath("name").TYPE(STRING).description("회원 이름"),
                        fieldWithPath("pw").TYPE(STRING).description("회원 비밀번호"),
                        fieldWithPath("role").TYPE(ENUM<ROLE>(ROLE::class)).description("회원 권한")
                    ),
                    responseFields(
                        fieldWithPath("id").TYPE(STRING).description("회원 id"),
                        fieldWithPath("name").TYPE(STRING).description("회원 이름"),
                        fieldWithPath("role").TYPE(STRING).description("회원 권한")
                    )
                )
            )
    }

    /*@PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequest,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<CustomerDto> {
        val loginResult = customerService.login(loginRequest)
        val session = httpServletRequest.getSession(true)
        session.setAttribute("user", loginResult)
        return ResponseEntity.ok(loginResult)
    }*/
    @Test
    fun login() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/login").content(mapper.writeValueAsString(loginRequest))
                .contentType(
                    MediaType.APPLICATION_JSON
                ).accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isString)
            .andExpect(jsonPath("$.name").value(signUpRequest.name))
            .andExpect(jsonPath("$.role").value(signUpRequest.role.toString()))
            .andDo(
                document(
                    myIdentifier("로그인"),
                    requestFields(
                        fieldWithPath("username").TYPE(STRING).description("회원 이름"),
                        fieldWithPath("pw").TYPE(STRING).description("회원 비밀번호")
                    ),
                    responseFields(
                        fieldWithPath("id").TYPE(STRING).description("회원 id"),
                        fieldWithPath("name").TYPE(STRING).description("회원 이름"),
                        fieldWithPath("role").TYPE(STRING).description("회원 권한")
                    )
                )
            )
    }

    /*@DeleteMapping("/user")
    fun resign(@SessionLogin loggedinUser: CustomerDto): ResponseEntity<String> {
        customerService.resign(loggedinUser.id)
        return ResponseEntity.ok().build()
    }*/
    @Test
    fun deleteUser() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/user").contentType(MediaType.APPLICATION_JSON).sessionAttrs(
                mapOf("user" to CustomerDto(customer))
            )
        ).andExpect(status().isOk)
            .andDo(
                document(
                    myIdentifier("회원 탈퇴")
                )
            )
    }

    /*@GetMapping("/users")
    fun listUsers(pageable: Pageable): ResponseEntity<Page<CustomerDto>> =
        customerService.getAll(pageable).let { ResponseEntity.ok(it) }
    */
    @Test
    fun userlist() {
        val param = LinkedMultiValueMap<String, String>()
        param["page"] = "0"
        param["size"] = "10"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/users").params(param).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].name").value(signUpRequest.name))
            .andExpect(jsonPath("$.content[0].id").isString)
            .andExpect(jsonPath("$.content[0].role").value(signUpRequest.role.toString()))
            .andDo(
                document(
                    myIdentifier("회원목록"),
                    queryParameters(
                        parameterWithName("page").optional().description("The page number to retrieve (default: 0)"),
                        parameterWithName("size").optional().description("The size of list for each page"),
                        parameterWithName("sort").optional().description("sort criteria(default:ASC)")
                    ),
                    relaxedResponseFields(
                        fieldWithPath("content[].id").TYPE(STRING).description("회원 id"),
                        fieldWithPath("content[].name").TYPE(STRING).description("회원 이름"),
                        fieldWithPath("content[].role").TYPE(STRING).description("회원 권한"),
                        fieldWithPath("last").TYPE(BOOLEAN).description("Whether this is the last page"),
                        fieldWithPath("totalPages").TYPE(NUMBER).description("The total number of pages"),
                        fieldWithPath("totalElements").TYPE(NUMBER).description("The total number of elements"),
                        fieldWithPath("size").TYPE(NUMBER).description("The size of the page"),
                        fieldWithPath("number").TYPE(NUMBER).description("The current page number"),
                        fieldWithPath("first").TYPE(BOOLEAN).description("Whether this is the first page"),
                        fieldWithPath("numberOfElements").TYPE(NUMBER).description("The number of elements on this page"),
                        fieldWithPath("empty").TYPE(BOOLEAN).description("Whether this page is empty")
                    ),
                )
            ).andReturn()

    }

    fun myIdentifier(methodName: String) = "{class-name}/$methodName"
}