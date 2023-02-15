FROM adoptopenjdk/openjdk11:ubi

ARG JAR_FILE=target/*.jar

ENV BOT_NAME=default_bot_name
ENV BOT_TOKEN=2222222222:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","-Dbot.username=${BOT_NAME}","-Dbot.token=${BOT_TOKEN}","/app.jar"]