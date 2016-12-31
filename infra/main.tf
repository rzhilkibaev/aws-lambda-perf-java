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

resource "aws_api_gateway_rest_api" "rest_api" {
  name = "${var.aws_resource_prefix}_api"
}

module "rest_api_endpoint_dummy_256" {
  source = "./modules/rest_api_endpoint"
  rest_api_id = "${aws_api_gateway_rest_api.rest_api.id}"
  parent_id = "${aws_api_gateway_rest_api.rest_api.root_resource_id}"
  path_part = "dummy_256"
  lambda_arn = "${module.lambda_dummy_256.aws_lambda_function_arn}"
  aws_region = "${var.aws_region}"
  aws_account = "${var.aws_account}"
}

module "rest_api_endpoint_dummy_512" {
  source = "./modules/rest_api_endpoint"
  rest_api_id = "${aws_api_gateway_rest_api.rest_api.id}"
  parent_id = "${aws_api_gateway_rest_api.rest_api.root_resource_id}"
  path_part = "dummy_512"
  lambda_arn = "${module.lambda_dummy_512.aws_lambda_function_arn}"
  aws_region = "${var.aws_region}"
  aws_account = "${var.aws_account}"
}

module "rest_api_endpoint_dummy_1024" {
  source = "./modules/rest_api_endpoint"
  rest_api_id = "${aws_api_gateway_rest_api.rest_api.id}"
  parent_id = "${aws_api_gateway_rest_api.rest_api.root_resource_id}"
  path_part = "dummy_1024"
  lambda_arn = "${module.lambda_dummy_1024.aws_lambda_function_arn}"
  aws_region = "${var.aws_region}"
  aws_account = "${var.aws_account}"
}

module "rest_api_endpoint_dummy_1536" {
  source = "./modules/rest_api_endpoint"
  rest_api_id = "${aws_api_gateway_rest_api.rest_api.id}"
  parent_id = "${aws_api_gateway_rest_api.rest_api.root_resource_id}"
  path_part = "dummy_1536"
  lambda_arn = "${module.lambda_dummy_1536.aws_lambda_function_arn}"
  aws_region = "${var.aws_region}"
  aws_account = "${var.aws_account}"
}

resource "aws_api_gateway_deployment" "rest_api_deployment" {
  rest_api_id = "${aws_api_gateway_rest_api.rest_api.id}"
  stage_name = "test"
  depends_on = [
    "module.rest_api_endpoint_dummy_256",
    "module.rest_api_endpoint_dummy_512",
    "module.rest_api_endpoint_dummy_1024",
    "module.rest_api_endpoint_dummy_1536"
  ]
}

output "rest_api_url" {
  value = "https://${aws_api_gateway_rest_api.rest_api.id}.execute-api.${var.aws_region}.amazonaws.com/${aws_api_gateway_deployment.rest_api_deployment.stage_name}"
}
