# Use the official Maven image to build the application
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy the project files into the container
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use a lightweight Java image to run the application
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
