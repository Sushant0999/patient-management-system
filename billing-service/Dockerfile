FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy gradle wrapper and build config
COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY build.gradle settings.gradle ./

# Download dependencies first (faster rebuilds)
RUN ./gradlew build --no-daemon -x test || return 0

# Copy source files
COPY src ./src

# Build the project
RUN ./gradlew clean build --no-daemon -x test

# Run stage
FROM openjdk:17-alpine AS runner

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 4001

EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]
