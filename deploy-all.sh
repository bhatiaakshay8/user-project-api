#!/usr/bin/env bash

yes | docker container prune
./gradlew bootBuildImage
yes | docker image prune
docker compose up -d