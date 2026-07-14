# Toggled by var.demo_enabled — count 0/1, not destroyed via `-target`.
# Fresh master credentials are fine whenever this is (re)created: Flyway
# (V1__init_schema.sql etc.) recreates schema + seed data automatically on boot.

resource "aws_db_subnet_group" "main" {
  count      = var.demo_enabled ? 1 : 0
  name       = "comic-store-db-subnet-group"
  subnet_ids = [data.aws_subnet.az_d.id, data.aws_subnet.az_a.id]
}

resource "aws_db_instance" "main" {
  count          = var.demo_enabled ? 1 : 0
  identifier     = "comic-store-db"
  engine         = "postgres"
  engine_version = "16"
  instance_class = "db.t3.micro"

  allocated_storage = 20
  storage_type       = "gp2"

  db_name  = "comic_store"
  username = "postgres"
  password = random_password.db.result

  db_subnet_group_name   = aws_db_subnet_group.main[0].name
  vpc_security_group_ids = [aws_security_group.ecs.id]
  publicly_accessible    = false

  auto_minor_version_upgrade = true
  skip_final_snapshot        = true
}
