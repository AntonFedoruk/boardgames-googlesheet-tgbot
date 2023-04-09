#!/bin/bash

Xvfb :99 -screen 0 1024x768x16 &

until [ -f /tmp/.X99-lock ]; do
  echo "Waiting for Xvfb..."
  sleep 0.1
done

java -jar -Dbot.username=${BOT_NAME} -Dbot.token=${BOT_TOKEN} -Dspring.datasource.username=${BOT_DB_USERNAME} -Dspring.datasource.password=${BOT_DB_PASSWORD} -Dgoogle.spreadsheets.id=${SPREADSHEETS_ID} app.jar