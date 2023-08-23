FROM openjdk:11-jre-slim
ARG JAR_FILE=target/*.jar
COPY ./target/question.bank-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
