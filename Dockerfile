# Step 1: Build the Spring Boot application with Maven
FROM maven:3.9.9-eclipse-temurin-17 as build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and install dependenciesz
COPY pom.xml .

# Download dependencies (this allows us to cache dependencies to speed up builds)
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src ./src

# Package the Spring Boot application (this will create a JAR file)
RUN mvn clean package -DskipTests

# Step 2: Run the Spring Boot app in a clean environment
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables for the Spring Boot app
ARG SPRING_PROFILES_ACTIVE
ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD
ARG SPRING_MAIL_USERNAME
ARG SPRING_MAIL_PASSWORD
ARG FRONTEND_URL
ARG SERVER_PORT

# Expose the application port
EXPOSE ${SERVER_PORT:-5000}

# Run the Spring Boot application with the injected environment variables
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=$SPRING_PROFILES_ACTIVE", \
    "--spring.datasource.url=$SPRING_DATASOURCE_URL", \
    "--spring.datasource.username=$SPRING_DATASOURCE_USERNAME", \
    "--spring.datasource.password=$SPRING_DATASOURCE_PASSWORD", \
    "--spring.mail.username=$SPRING_MAIL_USERNAME", \
    "--spring.mail.password=$SPRING_MAIL_PASSWORD", \
    "--app.external.service.frontendUrl=$FRONTEND_URL", \
    "--server.port=$SERVER_PORT"]
