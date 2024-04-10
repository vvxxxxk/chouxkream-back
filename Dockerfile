# Dockerfile

# Base image for building the Spring Boot application
FROM gradle:7.4-jdk17-alpine as builder
WORKDIR /build

# Only copy the Gradle configuration files first to leverage Docker cache
COPY build.gradle settings.gradle /build/

# Download dependencies - this layer will be cached if the build.gradle files are not modified
RUN gradle --no-daemon dependencies

# Copy the rest of the application
COPY . /build

# Build the application without running tests
RUN gradle --no-daemon build -x test --parallel

# Base image for running the Spring Boot application
FROM openjdk:17.0-slim
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=builder /build/build/libs/*.jar ./app.jar

# Dockerfile
#FROM openjdk:17-jdk-slim
#ARG JAR_FILE=build/libs/gitaction-0.0.1.jar
#COPY ${JAR_FILE} app.jar

# Java Run
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar","app.jar"]