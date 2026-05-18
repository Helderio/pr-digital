#!/usr/bin/env sh
set -eu

MVNW_VERSION="${MVNW_VERSION:-3.9.9}"
BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_HOME="$BASE_DIR/.mvn/apache-maven-$MVNW_VERSION"
MAVEN_BIN="$MAVEN_HOME/bin/mvn"

if [ ! -x "$MAVEN_BIN" ]; then
  mkdir -p "$BASE_DIR/.mvn"
  ARCHIVE="$BASE_DIR/.mvn/apache-maven-$MVNW_VERSION-bin.tar.gz"
  URL="https://archive.apache.org/dist/maven/maven-3/$MVNW_VERSION/binaries/apache-maven-$MVNW_VERSION-bin.tar.gz"

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o "$ARCHIVE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$ARCHIVE"
  else
    echo "curl ou wget é necessário para descarregar o Maven $MVNW_VERSION." >&2
    exit 1
  fi

  tar -xzf "$ARCHIVE" -C "$BASE_DIR/.mvn"
fi

exec "$MAVEN_BIN" "$@"
