# Step 1: Use a builder image to compile the application
FROM maven:3.9.4-eclipse-temurin-17-alpine AS build

# Set the working directory for the build stage
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# Step 2: Use a lightweight runtime image for the application
FROM openjdk:17-jdk-slim AS runtime

# Set the working directory for the runtime stage
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
