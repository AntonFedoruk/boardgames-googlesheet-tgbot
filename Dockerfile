FROM adoptopenjdk/openjdk11:ubi

ARG JAR_FILE=target/*.jar

ENV BOT_NAME=default_bot_name
ENV BOT_TOKEN=2222222222:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
ENV BOT_DB_USERNAME=dbusername
ENV BOT_DB_PASSWORD=dbuserpwd

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","-Dbot.username=${BOT_NAME}","-Dbot.token=${BOT_TOKEN}","-Dspring.datasource.username=${BOT_DB_USERNAME}","-Dspring.datasource.password=${BOT_DB_PASSWORD}","/app.jar"]