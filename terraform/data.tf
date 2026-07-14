data "aws_vpc" "default" {
  id = "vpc-0e19eb97a4956d135"
}

# us-east-1d — where the ECS Fargate task's network config currently lives
data "aws_subnet" "az_d" {
  id = "subnet-07cc8678a7fdee274"
}

# us-east-1a — second AZ for the ALB and RDS subnet group
data "aws_subnet" "az_a" {
  id = "subnet-06c3e57311eb3c856"
}
