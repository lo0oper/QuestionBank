



# Use a base image that includes Java and build tools
FROM openjdk:11-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy your Java application files to the container
COPY . /app

# Compile your Java code and create the JAR file
RUN mvn package
RUN javac -d . ./src/main/java/com/example/question/bank/Application.java
RUN jar -cvf Application.jar *

EXPOSE 8080
# Command to run the application (optional)
CMD ["java", "-jar", "Application.jar"]

