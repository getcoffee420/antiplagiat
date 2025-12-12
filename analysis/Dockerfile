FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app

COPY analysis/build.gradle* analysis/settings.gradle* analysis/gradle.properties* ./analysis/
COPY analysis/gradle ./analysis/gradle
COPY analysis/gradlew ./analysis/gradlew
COPY analysis/src ./analysis/src

WORKDIR /app/analysis
RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/analysis/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
