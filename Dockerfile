FROM gradle:8.10-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon || return 0

COPY src ./src

RUN ./gradlew bootJar --no-daemon -x test


FROM gradle:8.10-jdk21 AS test
WORKDIR /app

RUN apt-get update && apt-get install -y docker-cli

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

COPY --from=build /app /app

CMD ["./gradlew", "test", "--no-daemon"]

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]