# ALB toggled by var.demo_enabled — count 0/1, not destroyed via `-target`.
# The target group is permanent (never gated): it's cheap to keep, the ECS
# service's load_balancer block references it directly, and keeping it
# stable avoids re-importing it on every wake cycle.

resource "aws_lb" "main" {
  count              = var.demo_enabled ? 1 : 0
  name               = "comic-store-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = [data.aws_subnet.az_d.id, data.aws_subnet.az_a.id]
}

import {
  to = aws_lb_target_group.main
  id = "arn:aws:elasticloadbalancing:us-east-1:010526279410:targetgroup/comic-store-tg/5e4ca4819ac727e7"
}

resource "aws_lb_target_group" "main" {
  name        = "comic-store-tg"
  port        = 8081
  protocol    = "HTTP"
  vpc_id      = data.aws_vpc.default.id
  target_type = "ip"

  health_check {
    path                = "/health"
    healthy_threshold   = 2
    unhealthy_threshold = 2
    interval            = 30
  }
}

resource "aws_lb_listener" "http" {
  count             = var.demo_enabled ? 1 : 0
  load_balancer_arn = aws_lb.main[0].arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.main.arn
  }
}
