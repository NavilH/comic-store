import {
  to = aws_s3_bucket.ui
  id = "comic-store-ui-prod"
}

resource "aws_s3_bucket" "ui" {
  bucket = "comic-store-ui-prod"
}

import {
  to = aws_s3_bucket_versioning.ui
  id = "comic-store-ui-prod"
}

resource "aws_s3_bucket_versioning" "ui" {
  bucket = aws_s3_bucket.ui.id
  versioning_configuration {
    status = "Disabled"
  }
}

import {
  to = aws_s3_bucket_server_side_encryption_configuration.ui
  id = "comic-store-ui-prod"
}

resource "aws_s3_bucket_server_side_encryption_configuration" "ui" {
  bucket = aws_s3_bucket.ui.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

import {
  to = aws_s3_bucket_public_access_block.ui
  id = "comic-store-ui-prod"
}

resource "aws_s3_bucket_public_access_block" "ui" {
  bucket = aws_s3_bucket.ui.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

import {
  to = aws_s3_bucket_policy.ui
  id = "comic-store-ui-prod"
}

resource "aws_s3_bucket_policy" "ui" {
  bucket = aws_s3_bucket.ui.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "cloudfront.amazonaws.com" }
      Action    = "s3:GetObject"
      Resource  = "arn:aws:s3:::comic-store-ui-prod/*"
      Condition = {
        StringEquals = {
          "AWS:SourceArn" = aws_cloudfront_distribution.main.arn
        }
      }
    }]
  })
}
