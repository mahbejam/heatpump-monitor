FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src

RUN sed -i 's/\r$//' gradlew
RUN sh gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]