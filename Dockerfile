FROM openjdk:11

ADD target/question.bank-0.0.1-SNAPSHOT.jar question-bank.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "question.bank.jar"]
