terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}

# Uses the AWS CLI profile from the AWS_PROFILE environment variable.
# See README.md — expected to be "comic-store-terraform" (broad permissions,
# kept separate from the narrowly-scoped "comic-store-deploy" user used by CI).
provider "aws" {
  region = var.aws_region
}
