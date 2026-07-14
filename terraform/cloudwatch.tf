import {
  to = aws_cloudwatch_log_group.ecs
  id = "/ecs/comic-store-api"
}

resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/comic-store-api"
  retention_in_days = 14 # was "Never expire" before this
}

locals {
  ecs_widgets = [
    {
      type   = "metric"
      x      = 0
      y      = 0
      width  = 12
      height = 6
      properties = {
        title  = "ECS CPU / Memory"
        region = var.aws_region
        view   = "timeSeries"
        stat   = "Average"
        period = 60
        metrics = [
          ["AWS/ECS", "CPUUtilization", "ClusterName", aws_ecs_cluster.main.name, "ServiceName", aws_ecs_service.main.name],
          ["AWS/ECS", "MemoryUtilization", "ClusterName", aws_ecs_cluster.main.name, "ServiceName", aws_ecs_service.main.name],
        ]
      }
    }
  ]

  # Only meaningful (and only valid to reference) while var.demo_enabled —
  # ALB/RDS don't exist otherwise. Kept out of the base widget list so the
  # dashboard itself never needs to be destroyed when hibernating.
  backend_widgets = var.demo_enabled ? [
    {
      type   = "metric"
      x      = 12
      y      = 0
      width  = 12
      height = 6
      properties = {
        title  = "ALB Requests / Latency / 5XX"
        region = var.aws_region
        view   = "timeSeries"
        stat   = "Sum"
        period = 60
        metrics = [
          ["AWS/ApplicationELB", "RequestCount", "LoadBalancer", aws_lb.main[0].arn_suffix],
          ["AWS/ApplicationELB", "TargetResponseTime", "LoadBalancer", aws_lb.main[0].arn_suffix],
          ["AWS/ApplicationELB", "HTTPCode_Target_5XX_Count", "LoadBalancer", aws_lb.main[0].arn_suffix],
        ]
      }
    },
    {
      type   = "metric"
      x      = 0
      y      = 6
      width  = 12
      height = 6
      properties = {
        title  = "ALB Healthy Hosts"
        region = var.aws_region
        view   = "timeSeries"
        stat   = "Average"
        period = 60
        metrics = [
          ["AWS/ApplicationELB", "HealthyHostCount", "TargetGroup", aws_lb_target_group.main.arn_suffix, "LoadBalancer", aws_lb.main[0].arn_suffix],
        ]
      }
    },
    {
      type   = "metric"
      x      = 12
      y      = 6
      width  = 12
      height = 6
      properties = {
        title  = "RDS CPU / Storage / Connections"
        region = var.aws_region
        view   = "timeSeries"
        stat   = "Average"
        period = 60
        metrics = [
          ["AWS/RDS", "CPUUtilization", "DBInstanceIdentifier", aws_db_instance.main[0].id],
          ["AWS/RDS", "FreeStorageSpace", "DBInstanceIdentifier", aws_db_instance.main[0].id],
          ["AWS/RDS", "DatabaseConnections", "DBInstanceIdentifier", aws_db_instance.main[0].id],
        ]
      }
    }
  ] : []
}

resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "comic-store"

  dashboard_body = jsonencode({
    widgets = concat(local.ecs_widgets, local.backend_widgets)
  })
}
