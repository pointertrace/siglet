#!/bin/bash

JAR_FILE=$(ls target/siglet-implementation*.jar | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "Siglet implementation jar not found in target"
    exit 1
fi
FILENAME=$(basename "$JAR_FILE")

if [[ "$FILENAME" =~ [sS][nN][aA][pP][sS][hH][oO][tT] ]]; then
    VERSION="nightly"
else
    if [[ "$FILENAME" =~ -([0-9]+\.[0-9]+\.[0-9]+)\.jar$ ]]; then
        VERSION="${BASH_REMATCH[1]}"
    else
        VERSION="$FILENAME"
    fi
fi

docker push pointertrace/siglet:${VERSION}