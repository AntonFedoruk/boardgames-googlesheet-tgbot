#!/bin/bash

# Pull new changes
git pull

# Prepare Jar
mvn clean
mvn package

# Ensure, that docker-compose stopped
docker-compose stop

# Add environment variables for Docker
export BOT_NAME=$1
export BOT_TOKEN=$2
export BOT_DB_USERNAME='bg_db_user'
export BOT_DB_PASSWORD='bg_db_password'
export SPREADSHEETS_ID=$3

# Start new deployment
docker-compose up --build -d