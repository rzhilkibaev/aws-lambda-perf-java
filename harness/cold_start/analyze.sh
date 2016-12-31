#!/usr/bin/env bash

set -eo pipefail

sqlite3 < analyze.sql
