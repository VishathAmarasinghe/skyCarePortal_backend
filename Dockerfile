# Step 1: Build the Spring Boot application with Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml ./

RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src ./src

# Package the Spring Boot application (this will create a JAR file)
RUN mvn clean package -DskipTests

# Step 2: Run the Spring Boot app in a clean environment
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 5000

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
