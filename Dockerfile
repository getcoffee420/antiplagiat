FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app

COPY settings.gradle* build.gradle* gradle.properties* ./
COPY gradle ./gradle
COPY gradlew ./gradlew

COPY src ./src
COPY analysis ./analysis
COPY storage ./storage

RUN chmod +x gradlew
RUN gradle --no-daemon clean :bootJar :analysis:bootJar :storage:bootJar


FROM eclipse-temurin:21-jre AS gateway
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]


FROM eclipse-temurin:21-jre AS analysis
WORKDIR /app
COPY --from=build /app/analysis/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]


FROM eclipse-temurin:21-jre AS storage
WORKDIR /app
COPY --from=build /app/storage/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
