#!/usr/bin/env bash

set -eo pipefail

./init.sh

# disable color if no terminal is available
if [ ! -t 1 ]; then
    no_color_arg="-no-color"
fi

terraform destroy -force
