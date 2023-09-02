FROM openjdk:11
RUN mvn clean install
ADD target/Question-Bank.jar Question-Bank.jar
ENTRYPOINT ["java", "-jar","Question-Bank.jar"]
EXPOSE 8080