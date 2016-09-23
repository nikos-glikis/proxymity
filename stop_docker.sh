#!/usr/bin/env bash
#docker stop proxymity
docker stop $(docker ps -a -q --filter name=proxymity --format="{{.ID}}")
