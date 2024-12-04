FROM openjdk:17-jdk-slim
LABEL authors="giuseppe.mercurio"
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]