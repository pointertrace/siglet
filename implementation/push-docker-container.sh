#!/bin/bash

JAR_FILE=$(find target -maxdepth 1 -type f -name "*.jar" \
                 ! -name "*-sources.jar" \
                 ! -name "*-javadoc.jar" \
                 | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "Siglet implementation jar not found in target"
    exit 1
fi
FILE_NAME=$(basename "$JAR_FILE")

if [[ "$FILE_NAME" =~ [sS][nN][aA][pP][sS][hH][oO][tT] ]]; then
    VERSION="nightly"
else
   VERSION=$(echo "$FILE_NAME" | \
       sed -E 's/^.*-([0-9]+(\.[0-9]+)*(-[A-Za-z0-9]+)?)\.jar$/\1/')
fi
docker push pointertrace/siglet:${VERSION}