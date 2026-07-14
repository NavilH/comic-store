import {
  to = aws_iam_role.ecs_task_execution
  id = "ecsTaskExecutionRole"
}

resource "aws_iam_role" "ecs_task_execution" {
  name = "ecsTaskExecutionRole"

  assume_role_policy = jsonencode({
    Version = "2008-10-17"
    Statement = [{
      Sid       = ""
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })
}

import {
  to = aws_iam_role_policy_attachment.ecs_task_execution_managed
  id = "ecsTaskExecutionRole/arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_managed" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# New: lets the execution role resolve the SSM SecureString secrets referenced
# in the task definition's `secrets` block at task launch time.
resource "aws_iam_role_policy" "ecs_task_execution_ssm" {
  name = "comic-store-ssm-secrets"
  role = aws_iam_role.ecs_task_execution.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = ["ssm:GetParameters"]
        Resource = [
          aws_ssm_parameter.db_password.arn,
          aws_ssm_parameter.jwt_secret.arn,
          aws_ssm_parameter.db_url.arn,
        ]
      },
      {
        Effect   = "Allow"
        Action   = ["kms:Decrypt"]
        Resource = ["arn:aws:kms:${var.aws_region}:${var.account_id}:alias/aws/ssm"]
      }
    ]
  })
}
