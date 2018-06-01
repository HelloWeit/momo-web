#!/usr/bin/env bash

mvn clean package

docker build -t momoweb:latest .

#docker run -it momoweb:latest --ip=192.168.0.1 --port=8080 -p 8080:8080

#docker compose up -d