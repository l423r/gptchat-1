FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/gptchat.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
ENV JAVA_TOOL_OPTIONS "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:6089"
ENTRYPOINT ["java","-jar","app.jar"]