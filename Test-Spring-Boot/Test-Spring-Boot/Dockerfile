# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-alpine AS build

# Copy the application source code to the working directory
COPY . .

# Run Maven to build the application
RUN ./mvnw clean package

# Use a slim version of the JDK for the runtime image
FROM openjdk:17-jdk-slim AS runtime

# Copy the built application JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
