#FROM openjdk:17-oracle
#FROM adoptopenjdk/openjdk11:ubi
FROM openjdk:11-jdk

ARG JAR_FILE=target/*.jar

ENV BOT_NAME=default_bot_name
ENV BOT_TOKEN=2222222222:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
ENV BOT_DB_USERNAME=dbusername
ENV BOT_DB_PASSWORD=dbuserpwd
ENV SPREADSHEETS_ID=qweqweqweqweqweqweqweqweqweq
ENV DISPLAY=:99

RUN apt-get update \
    && apt-get install -y xvfb \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN apt-get update \
    && apt-get install -y libxrender1 libxtst6 libxi6 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY xvfb_and_app_run.sh /xvfb_and_app_run.sh
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["/bin/bash", "/xvfb_and_app_run.sh"]