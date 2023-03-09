FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/*.jar myapp.jar

RUN wget https://github.com/pinpoint-apm/pinpoint/releases/download/v2.5.0/pinpoint-agent-2.5.0.tar.gz
RUN tar -zxvf pinpoint-agent-2.5.0.tar.gz
RUN ls pinpoint-agent-2.5.0
RUN chmod 755 pinpoint-agent-2.5.0/pinpoint-bootstrap-2.5.0.jar
EXPOSE 8000
ENV JAVA_OPTS="-javaagent:pinpoint-agent-2.5.0/pinpoint-bootstrap-2.5.0.jar -Dpinpoint.agentId=myapp-1 -Dpinpoint.applicationName=myapp -Dprofiler.collector.ip=172.24.0.30 -Dprofiler.transport.grpc.collector.ip=172.24.0.30"
ENTRYPOINT exec java ${JAVA_OPTS} -jar myapp.jar