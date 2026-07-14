# Auto-generated secrets — replaces the plaintext JWT_SECRET/DB_PASSWORD that
# previously lived directly in the ECS task definition's environment block.
# No manual input needed: the old RDS instance (and its "postgres"/"postgres"
# credentials) is already gone, and the app is fully down, so a fresh password
# and a fresh JWT secret are safe to generate on first apply.

resource "random_password" "db" {
  length  = 32
  special = false # avoid characters needing URL-encoding in the JDBC connection string
}

resource "random_password" "jwt" {
  length  = 40
  special = false
}

resource "aws_ssm_parameter" "db_password" {
  name  = "/comic-store/db-password"
  type  = "SecureString"
  value = random_password.db.result
}

resource "aws_ssm_parameter" "jwt_secret" {
  name  = "/comic-store/jwt-secret"
  type  = "SecureString"
  value = random_password.jwt.result
}

# DB_URL also goes through SSM rather than the task definition's `environment`
# block: the RDS endpoint hostname changes every time the instance is
# recreated (e.g. a hibernate/wake cycle), but `container_definitions` is
# frozen via `ignore_changes` after the first apply (see ecs.tf). Routing it
# through `secrets` means it resolves fresh at every task launch instead of
# being baked into a definition revision that Terraform stops touching.
# Always exists (never gated by demo_enabled) so the IAM policy referencing
# its ARN doesn't need conditional indexing; value is just a harmless
# placeholder whenever RDS doesn't exist, since no task is running to read it.
resource "aws_ssm_parameter" "db_url" {
  name  = "/comic-store/db-url"
  type  = "SecureString"
  value = var.demo_enabled ? "jdbc:postgresql://${aws_db_instance.main[0].address}:5432/comic_store" : "backend-hibernated-no-rds"
}
