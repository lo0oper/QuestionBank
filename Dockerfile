# Builder stage
FROM maven:3.8.4-openjdk-11 AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml for dependency resolution
COPY pom.xml .

# Build the project and download dependencies
RUN mvn clean install

# Final stage
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the compiled JAR file from the builder stage
COPY --from=builder /app/target/question.bank-0.0.1-SNAPSHOT.jar .

# Run the Spring Boot application
CMD ["java", "-jar", "question.bank-0.0.1-SNAPSHOT.jar"]
