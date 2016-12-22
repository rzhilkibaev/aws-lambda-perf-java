#!/usr/bin/env bash

set -eo pipefail

rm -f gradle.properties
cfgen gradle.properties
