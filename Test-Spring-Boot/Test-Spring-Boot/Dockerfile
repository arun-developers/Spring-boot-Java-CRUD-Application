# Base image
FROM maven:3.9.9-eclipse-temurin-23 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the `pom.xml` and `src` directory into the container
COPY Test-Spring-Boot/Test-Spring-Boot/pom.xml .
COPY Test-Spring-Boot/Test-Spring-Boot/src ./src


# Package the application
RUN mvn clean package -e

# Second stage: Run the application
FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
