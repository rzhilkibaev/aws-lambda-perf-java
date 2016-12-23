module "lambda_dummy_256" {
  source = "./modules/lambda"
  name = "dummy_256"
  filename = "/tmp/lambda/dummy.jar"
  memory_size = "256"
  role_arn = "${module.role_lambda.aws_iam_role_arn}"
  aws_resource_prefix = "${var.aws_resource_prefix}"
}

module "lambda_dummy_512" {
  source = "./modules/lambda"
  name = "dummy_512"
  filename = "/tmp/lambda/dummy.jar"
  memory_size = "512"
  role_arn = "${module.role_lambda.aws_iam_role_arn}"
  aws_resource_prefix = "${var.aws_resource_prefix}"
}

module "lambda_dummy_1024" {
  source = "./modules/lambda"
  name = "dummy_1024"
  filename = "/tmp/lambda/dummy.jar"
  memory_size = "1024"
  role_arn = "${module.role_lambda.aws_iam_role_arn}"
  aws_resource_prefix = "${var.aws_resource_prefix}"
}

module "lambda_dummy_1536" {
  source = "./modules/lambda"
  name = "dummy_1536"
  filename = "/tmp/lambda/dummy.jar"
  memory_size = "1536"
  role_arn = "${module.role_lambda.aws_iam_role_arn}"
  aws_resource_prefix = "${var.aws_resource_prefix}"
}

module "role_lambda" {
  source = "./modules/role_lambda"
  name = "lambda"
  aws_resource_prefix = "${var.aws_resource_prefix}"
}

