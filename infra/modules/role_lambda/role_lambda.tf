variable "aws_resource_prefix" {}
variable "name" {}

resource "aws_iam_role" "lambda" {
    name = "${var.aws_resource_prefix}_${var.name}"
    # this policy tells who can assume this role
    assume_role_policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": "sts:AssumeRole",
            "Principal": {
                "Service": "lambda.amazonaws.com"
            },
            "Effect": "Allow",
            "Sid": ""
        }
    ]
}
EOF
}

resource "aws_iam_role_policy" "lambda" {
    name = "allow_logs"
    role = "${aws_iam_role.lambda.id}"
    policy = <<EOF
{
        "Version": "2012-10-17",
        "Statement": [
            {
                "Action": [
                    "logs:*"
                ],
                "Effect": "Allow",
                "Resource": "*"
            }
        ]
}
EOF
}

output "aws_iam_role_arn" {
    value = "${aws_iam_role.lambda.arn}"
}
