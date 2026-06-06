FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]