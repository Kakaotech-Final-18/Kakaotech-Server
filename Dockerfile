# Build stage
FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /app

# 의존성 설치 및 빌드
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle build --no-daemon || true
COPY src ./src
RUN gradle build --no-daemon -x test

# Run stage
FROM openjdk:17-jdk-slim-buster
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]