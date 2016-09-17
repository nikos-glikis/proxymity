#!/usr/bin/env bash
docker run  -v ~/.m2:/root/.m2  -p 81:81 -d --name proxymity proxymity
