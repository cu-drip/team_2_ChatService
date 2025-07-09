FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /app
COPY settings.gradle.kts build.gradle.kts gradlew ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x ./gradlew \
 && ./gradlew clean build

FROM azul/zulu-openjdk-alpine:21-jre-headless
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
