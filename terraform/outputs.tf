output "cloudfront_domain_name" {
  value = aws_cloudfront_distribution.main.domain_name
}

output "alb_dns_name" {
  description = "null while hibernated (var.demo_enabled = false)"
  value       = try(aws_lb.main[0].dns_name, null)
}

output "rds_endpoint" {
  description = "null while hibernated (var.demo_enabled = false)"
  value       = try(aws_db_instance.main[0].address, null)
}

output "ecs_cluster_name" {
  value = aws_ecs_cluster.main.name
}

output "ecs_service_name" {
  value = aws_ecs_service.main.name
}

output "ecs_task_definition_arn" {
  description = "ARN of the Terraform-managed task definition revision."
  value       = aws_ecs_task_definition.main.arn
}
