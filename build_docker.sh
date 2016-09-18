#!/usr/bin/env bash
docker build -t proxymity .
docker run -d --name proxymity proxymity
sleep 10
./stop_docker.sh