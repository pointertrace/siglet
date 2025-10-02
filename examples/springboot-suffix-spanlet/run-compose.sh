#!/bin/bash

JAR=$(ls target/suffix-springboot-spanlet-example*.jar | head -n 1)

if [ -z "$JAR" ]; then
  echo "Could not find spanlet jar (suffix-springboot-spanlet-example*.jar) in target directory"
  exit 1
fi

JAR_PATH=$(realpath "$JAR")
export JAR_PATH

docker-compose up
