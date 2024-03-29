package com.example.numbletimedealserver.repository.product

import com.example.numbletimedealserver.domain.Product
import com.example.numbletimedealserver.domain.QProduct
import com.example.numbletimedealserver.domain.QProduct.*
import com.example.numbletimedealserver.exception.CustomException
import com.querydsl.core.types.dsl.BooleanExpression

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.time.LocalTime
import java.util.*



class ProductRepositoryCustomImpl(private val jpaQueryFactory: JPAQueryFactory) : ProductRepositoryCustom {

    override fun findAllByAppointedTimeBetween(start: LocalTime, end: LocalTime): List<Product> {
        val exp: BooleanExpression =if(start.isBefore(end)) product._appointedTime.between(start,end) else product._appointedTime.after(end).not().or(product._appointedTime.before(start).not())
        return jpaQueryFactory.selectFrom(product).where(exp).fetch()
    }

    override fun findAllByAdminId(adminId: UUID): List<Product> =
        jpaQueryFactory.selectFrom(product).where(product.admin.id.eq(adminId)).fetch()


    override fun countAllByAdminId(adminId: UUID): JPAQuery<Long> =
        jpaQueryFactory.select(product.count()).from(product).where(product.admin.id.eq(adminId))

    override fun findByIdAndAdminId(productId: UUID, adminId: UUID): Product? =  jpaQueryFactory.selectFrom(product).where(product.id.eq(productId).and(product.admin.id.eq(adminId))).fetchOne()


    override fun findByIdLockOption(@Param("productId") productId: UUID, isLock: Boolean): Product? {
        val query = jpaQueryFactory.selectFrom(product).where(product.id.eq(productId))
        if(isLock)query.setLockMode(LockModeType.PESSIMISTIC_WRITE)
        return query.fetchOne()
    }
}