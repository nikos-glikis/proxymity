#!/usr/bin/env bash
docker run -p 81:81 -d proxymity
#docker start $(docker ps -a -q --filter name=proxymity --format="{{.ID}}")
#docker run -it -v ~/.m2:/root/.m2  -p 81:81 --name proxymity proxymity
