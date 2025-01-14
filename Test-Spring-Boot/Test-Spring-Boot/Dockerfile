FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app

# Add debugging commands
RUN echo "Current files in build context:"
RUN ls -la

# Copy project files into the container
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests
