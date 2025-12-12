FROM ubuntu:latest
LABEL authors="get-coffee"

FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app

COPY storage/build.gradle* storage/settings.gradle* storage/gradle.properties* ./storage/
COPY storage/gradle ./storage/gradle
COPY storage/gradlew ./storage/gradlew
COPY storage/src ./storage/src

WORKDIR /app/storage
RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/storage/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]