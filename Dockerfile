FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/*.jar myapp.jar
COPY pinpoint-agent-2.5.0.tar.gz pinpoint-agent.tar.gz
RUN tar -zxvf pinpoint-agent.tar.gz
RUN ls pinpoint-agent-2.5.0
RUN chmod 755 pinpoint-agent-2.5.0/pinpoint-bootstrap-2.5.0.jar

EXPOSE 8000
ENV JAVA_OPTS="-javaagent:pinpoint-agent-2.5.0/pinpoint-bootstrap-2.5.0.jar -Dpinpoint.agentId=myapp-1 -Dpinpoint.applicationName=myapp -Dpinpoint.config=pinpoint-agent-2.5.0/pinpoint-root.config"
ENTRYPOINT exec java ${JAVA_OPTS} -jar myapp.jar