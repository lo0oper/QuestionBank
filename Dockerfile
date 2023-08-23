FROM openjdk:11

# Copy the JAR file into the image
COPY target/question.bank*.jar question.bank.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/question.bank.jar"]
