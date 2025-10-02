#!/bin/bash

JAR=$(ls target/suffix-fatjar-metric-from-span-fatjar-spanlet-example-*.jar | head -n 1)

if [ -z "$JAR" ]; then
  echo "Could not find spanlet jar (metric-from-span-fatjar-spanlet-example-*.jar) in target directory"
  exit 1
fi

JAR_PATH=$(realpath "$JAR")
export JAR_PATH

docker-compose up