#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 <new-verison>"
  exit 1
fi

NEW_VERSION="$1"

echo "Changing version to: $NEW_VERSION"

# Altera versão do projeto raiz e seus filhos
echo "Changing root and children..."
mvn versions:set -DnewVersion="$NEW_VERSION" -DgenerateBackupPoms=false

echo "Getting springboot modules..."

SPRINGBOOT_MODULES=$(find . -name "pom.xml" -exec grep -l "<artifactId>spring-boot-starter-parent</artifactId>" {} \;)

for POM in $SPRINGBOOT_MODULES; do
  MODULO_DIR=$(dirname "$POM")
  echo "Changing $MODULO_DIR version"
  mvn versions:set -DnewVersion="$NEW_VERSION" -DgenerateBackupPoms=false -f "$POM"
done

echo "All versions changed to $NEW_VERSION ."
