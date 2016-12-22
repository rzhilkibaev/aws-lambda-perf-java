module "lambda_lifecycle" {
  source = "./modules/lambda"
  name = "lifecycle"
  filename = "/tmp/lambda/dummy.jar"
  memory_size = "256"
  role_arn = "${module.role_lambda.aws_iam_role_arn}"
  aws_resource_prefix = "${var.aws_resource_prefix}"
}

module "role_lambda" {
  source = "./modules/role_lambda"
  name = "lambda"
  aws_resource_prefix = "${var.aws_resource_prefix}"
}

