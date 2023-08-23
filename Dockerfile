FROM openjdk:11

ARG JAR_FILE=target/question.bank*.jar

ADD ${JAR_FILE} question.bank.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/question.bank.jar", "/app/question.bank.jar"]