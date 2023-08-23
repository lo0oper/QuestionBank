# Use a Java base image with Java 11
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the compiled Spring Boot JAR into the container
COPY target/quesion-bank.jar app.jar

# Expose the port your Spring Boot app listens on (assuming it's 8080)
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
