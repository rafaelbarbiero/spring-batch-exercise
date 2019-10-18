#!/usr/bin/env bash
export hash=$(git rev-parse HEAD) &&
export name=${PWD##*/}
export fullName=${name}:${hash} &&
docker build -f Dockerfile -t rbarbiero/${fullName} .