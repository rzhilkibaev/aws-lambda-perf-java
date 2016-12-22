#!/usr/bin/env bash

set -eo pipefail

./init.sh

./gradlew build
