variable "name" {}
variable "filename" {}
variable "memory_size" {}
variable "role_arn" {}
variable "aws_resource_prefix" {}

resource "aws_lambda_function" "lambda" {
    function_name = "${var.aws_resource_prefix}_${var.name}"
    handler = "Lambda::handle"
    filename = "${var.filename}"
    source_code_hash = "${base64sha256(file("${var.filename}"))}"
    role = "${var.role_arn}"
    memory_size = "${var.memory_size}"
    runtime = "java8"
    timeout = "300"
}
 
output "aws_lambda_function_arn" {
    value = "${aws_lambda_function.lambda.arn}"
}
