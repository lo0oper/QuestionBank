FROM openjdk:11
ADD target/Question-Bank.jar Question-Bank.jar
ENTRYPOINT ["java", "-jar","Question-Bank.jar"]
EXPOSE 8080