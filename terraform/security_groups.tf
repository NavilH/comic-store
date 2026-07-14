import {
  to = aws_security_group.alb
  id = "sg-0820890be0833abe7"
}

resource "aws_security_group" "alb" {
  name        = "comic-store-alb-sg"
  description = "ALB security group for comic-store"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

import {
  to = aws_security_group.ecs
  id = "sg-0931f7020875aa5fc"
}

resource "aws_security_group" "ecs" {
  name        = "comic-store-ecs-sg"
  description = "ECS"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port       = 8081
    to_port         = 8081
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  # New: RDS also lives in this SG (matching the prior manual setup), so ECS
  # tasks and RDS need to reach each other on 5432 via self-referencing ingress.
  ingress {
    description = "Postgres, self-referencing, ECS tasks and RDS"
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    self        = true
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
