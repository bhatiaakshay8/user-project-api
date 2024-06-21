#!/usr/bin/env bash

yes | docker container prune
./gradlew clean bootBuildImage
yes | docker image prune
docker compose up -d