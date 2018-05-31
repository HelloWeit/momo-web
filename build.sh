#!/usr/bin/env bash

mvn clean package

docker build -t momoweb:latest .

#docker run -it momoweb:latest --ip=容器的IP地址 --port=容器的PORT地址