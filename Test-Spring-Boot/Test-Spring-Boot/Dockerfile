# Base image
FROM maven:3.9.9-eclipse-temurin-23 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the `pom.xml` and `src` directory into the container
COPY Test-Spring-Boot/Test-Spring-Boot/pom.xml .
COPY Test-Spring-Boot/Test-Spring-Boot/src ./src

# Use the settings.xml if you have specific repository configurations
# COPY settings.xml /root/.m2/settings.xml 

# Package the application
RUN mvn clean package -DskipTests -X

# Second stage: Run the application
FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
