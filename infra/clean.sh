#!/usr/bin/env bash

set -eo pipefail

./init.sh

terraform destroy -force
