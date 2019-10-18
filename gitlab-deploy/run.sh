#!/usr/bin/env bash
green=$(docker ps --filter "name=green" --format "{{.ID}}")
blue=$(docker ps --filter "name=blue" --format "{{.ID}}")
stoppedGreen=$(docker ps -a --filter "name=green" --filter "status=exited" --format "{{.ID}}")
stoppedBlue=$(docker ps -a --filter "name=blue" --filter "status=exited" --format "{{.ID}}")
hash=$(git rev-parse HEAD)
name=${PWD##*/}
fullName=${name}:${hash}

echo "Alive container blue" "$blue"
echo "Alive container green" "$green"
echo "Stopped container blue" "$stoppedBlue"
echo "Stopped container green" "$stoppedGreen"

if [[ ! -z "$stoppedGreen" ]]
then
    docker rm ${stoppedGreen}
fi

if [[ ! -z "$stoppedBlue" ]]
then
    docker rm ${stoppedBlue}
fi

waiting () {
    i="0"
    while [[ ${i} -lt 20 ]]
    do
        i=$[$i+1]
        echo "Checking ${containerName} container is alive [${i}]"
        containerId=$(docker ps --filter "name=${containerName}"    --format "{{.ID}}")
        if [[ ! -z "$containerId" ]]
        then
            if [[ ! -z "$containerName" ]]
            then
                echo "Stoping ${containerName} container"
                docker stop ${containerName}
            fi
            exit 0
        fi
        echo "Waiting for " ${containerName} "to stop"
        sleep 2
    done
    exit 1
}

if [[ -z "$green" ]]
then
    echo "Starting green container"
    docker run -d -p 8080:8080 --name green rbarbiero/${fullName} java -jar app.jar
    if [[ -z "$blue" ]]
    then
        waiting "$blue"
    fi
else
    echo "Starting blue container"
    docker run -d -p 8081:8080 --name blue rbarbiero/${fullName} java -jar app.jar
    if [[ -z "$green" ]]
    then
        waiting "$green"
    fi
fi

