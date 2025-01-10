# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-alpine AS build

# Set the working directory for the build stage
WORKDIR /app

# Copy the application source code to the working directory
COPY . .

# Run Maven to build the application file
RUN ./mvnw clean package

# Use a slim version of the JDK for the runtime image
FROM openjdk:17-jdk-slim AS runtime

# Set the working directory in the runtime image
WORKDIR /app

# Copy the built application JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
