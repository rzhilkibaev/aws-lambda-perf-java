#!/usr/bin/env bash

set -eo pipefail

rm -f terraform.tfvars
cfgen terraform.tfvars

#source terraform.tfvars

#terraform remote config \
#    -backend=s3 \
#    -backend-config="bucket=${s3_bucket}" \
#    -backend-config="key=${aws_resource_prefix}/sync_lambda_invocation/terraform.tfstate" \
#    -backend-config="region=${aws_region}"

terraform get
