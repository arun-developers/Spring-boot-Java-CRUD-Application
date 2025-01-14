# First Stage: Build the application
# Use Maven with Java 23 as the base image for building the application
FROM maven:3.9.4-eclipse-temurin-23 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the `pom.xml` file first to leverage Docker layer caching for dependencies
COPY Test-Spring-Boot/Test-Spring-Boot/pom.xml .

# Download dependencies without building the project (improves build caching)
RUN mvn dependency:go-offline -B

# Copy the source code into the container
COPY Test-Spring-Boot/Test-Spring-Boot/src ./src

# Package the application
RUN mvn clean package -DskipTests

# Second Stage: Create a lightweight runtime image
# Use an Alpine-based Java 23 image for a smaller runtime footprint
FROM eclipse-temurin:23-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file built in the first stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
