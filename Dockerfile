# Use a base image that has Maven and Java pre-installed
FROM maven:3.8.4-openjdk-11

# Set the working directory inside the container
WORKDIR /app

# Copy the contents of your Maven project to the container
COPY . .


# Run the 'mvn clean install' command when the container starts
RUN mvn clean install

RUN ls
RUN pwd
RUN ls ./target

#COPY ./target/test-classes Question-Bank.jar
ENTRYPOINT ["java", "-jar","./target/Question-Bank.jar"]
EXPOSE 8080
