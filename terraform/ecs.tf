import {
  to = aws_ecs_cluster.main
  id = "comic-store-cluster"
}

resource "aws_ecs_cluster" "main" {
  name = "comic-store-cluster"

  configuration {
    execute_command_configuration {
      logging = "DEFAULT"
    }
  }
}

# Bootstraps a new, secrets-based task definition revision (replacing the
# plaintext JWT_SECRET/DB_PASSWORD environment entries). `container_definitions`
# is ignored after creation because .github/workflows/deploy.yml fetches the
# LIVE task definition and re-renders it with a new image tag on every push —
# Terraform owns the initial shape (env/secrets/log config/cpu/mem), CI owns
# image revisions from here on. Since secrets resolve at task launch (not
# baked into the definition), a future RDS endpoint change only needs an SSM
# parameter update, not a new task revision.
resource "aws_ecs_task_definition" "main" {
  family                   = "comic-store-api"
  requires_compatibilities = ["FARGATE"]
  network_mode              = "awsvpc"
  cpu                        = "512"
  memory                     = "1024"
  execution_role_arn         = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name  = "comic-store-api"
      # Known-good image currently running. CI takes over tagging from here.
      image     = "${aws_ecr_repository.main.repository_url}:0fa5049a7f6206c9f60073661fb08fccb640e832"
      essential = true

      portMappings = [{
        containerPort = 8081
        hostPort      = 8081
        protocol      = "tcp"
      }]

      environment = [
        { name = "JWT_EXPIRATION_MS", value = "86400000" },
        { name = "DB_USERNAME", value = "postgres" },
        { name = "ALLOWED_ORIGIN", value = "https://${aws_cloudfront_distribution.main.domain_name}" },
      ]

      # DB_URL is here too (not just secrets) — see ssm.tf for why: it must
      # resolve fresh at task launch, not get baked into a frozen definition.
      secrets = [
        { name = "JWT_SECRET", valueFrom = aws_ssm_parameter.jwt_secret.arn },
        { name = "DB_PASSWORD", valueFrom = aws_ssm_parameter.db_password.arn },
        { name = "DB_URL", valueFrom = aws_ssm_parameter.db_url.arn },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.ecs.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])

  lifecycle {
    ignore_changes = [container_definitions]
  }
}

# No import block: the previous service was destroyed (see terraform/README.md
# incident note) and must be created fresh. Its `load_balancer` block below
# only references the permanent target group, never the ALB/listener
# directly — that's what keeps this service from being swept into a future
# hibernate cycle the way it was this time.
resource "aws_ecs_service" "main" {
  name            = "comic-store-api-svc"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.main.arn
  desired_count   = var.demo_enabled ? 1 : 0
  launch_type     = "FARGATE"

  health_check_grace_period_seconds = 240
  availability_zone_rebalancing     = "ENABLED"
  enable_ecs_managed_tags           = true

  network_configuration {
    subnets          = [data.aws_subnet.az_d.id]
    security_groups  = [aws_security_group.ecs.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.main.arn
    container_name    = "comic-store-api"
    container_port    = 8081
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  # See note on aws_ecs_task_definition above — CI deploys new revisions
  # directly via the AWS API, outside Terraform. Without this, any unrelated
  # `terraform apply` (e.g. toggling demo_enabled) would silently roll the
  # service back to whatever revision Terraform created at bootstrap.
  lifecycle {
    ignore_changes = [task_definition]
  }
}
