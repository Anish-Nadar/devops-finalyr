#!/usr/bin/env sh
# Marathon Portal Maven Runner for Unix/Linux/CI
if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
else
    echo "ERROR: mvn not found on PATH." >&2
    exit 1
fi

