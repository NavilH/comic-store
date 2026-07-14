variable "aws_region" {
  description = "AWS region for all resources"
  type        = string
  default     = "us-east-1"
}

variable "account_id" {
  description = "AWS account ID, used to build ARNs"
  type        = string
  default     = "010526279410"
}

variable "demo_enabled" {
  description = <<-EOT
    Single toggle for the whole billable backend. true = RDS + ALB + listener
    exist and the ECS service runs 1 task. false = hibernated (RDS/ALB
    destroyed, ECS scaled to 0). CloudFront, ECS service/task definition,
    the dashboard, ECR, S3, and IAM never get destroyed by this toggle —
    only updated in place — so restoring never changes the site's URL.
    Deliberately one variable, not two, so RDS/ALB and desired_count can
    never drift out of sync with each other.
  EOT
  type        = bool
  default     = false
}
