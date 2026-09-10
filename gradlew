#!/bin/sh
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION=8.10.2
DIST_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/dists/gradle-${GRADLE_VERSION}-bin"
GRADLE_HOME="$DIST_DIR/gradle-${GRADLE_VERSION}"
if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
  mkdir -p "$DIST_DIR"
  ARCHIVE="$DIST_DIR/gradle-${GRADLE_VERSION}-bin.zip"
  if [ ! -f "$ARCHIVE" ]; then curl -fL --retry 3 "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "$ARCHIVE"; fi
  rm -rf "$GRADLE_HOME"
  unzip -q "$ARCHIVE" -d "$DIST_DIR"
fi
exec "$GRADLE_HOME/bin/gradle" "$@"
