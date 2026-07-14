import {
  to = aws_cloudfront_origin_access_control.ui
  id = "EDRK2P38IC99C"
}

resource "aws_cloudfront_origin_access_control" "ui" {
  name                              = "comic-store-ui-oac"
  description                       = "OAC for comic-store-ui-prod S3 bucket"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

# CloudFront itself is permanent — never gated by var.demo_enabled. Only the
# ALB-facing origin and its 4 cache behaviors are conditional (dynamic
# blocks), so hibernating updates this distribution in place instead of
# destroying it. That's what keeps the public domain name stable across
# every future hibernate/wake cycle.
resource "aws_cloudfront_distribution" "main" {
  enabled             = true
  default_root_object = "index.html"
  comment              = "Comic Store UI distribution"
  price_class          = "PriceClass_100"
  http_version          = "http2"
  is_ipv6_enabled       = true

  origin {
    domain_name              = aws_s3_bucket.ui.bucket_regional_domain_name
    origin_id                = "S3-comic-store-ui-prod"
    origin_access_control_id = aws_cloudfront_origin_access_control.ui.id
  }

  dynamic "origin" {
    for_each = var.demo_enabled ? [1] : []
    content {
      domain_name = aws_lb.main[0].dns_name
      origin_id   = "ALB-comic-store-api"

      custom_origin_config {
        http_port                = 80
        https_port                = 443
        origin_protocol_policy   = "http-only"
        origin_ssl_protocols     = ["TLSv1.2"]
        origin_read_timeout       = 30
        origin_keepalive_timeout = 5
      }
    }
  }

  default_cache_behavior {
    allowed_methods         = ["GET", "HEAD"]
    cached_methods           = ["GET", "HEAD"]
    target_origin_id         = "S3-comic-store-ui-prod"
    viewer_protocol_policy   = "redirect-to-https"
    compress                  = true

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = 86400
    max_ttl     = 31536000
  }

  dynamic "ordered_cache_behavior" {
    for_each = var.demo_enabled ? [1] : []
    content {
      path_pattern            = "/api/*"
      allowed_methods          = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
      cached_methods            = ["GET", "HEAD"]
      target_origin_id          = "ALB-comic-store-api"
      viewer_protocol_policy    = "redirect-to-https"
      compress                   = false

      forwarded_values {
        query_string = true
        headers      = ["Origin", "Authorization", "Access-Control-Request-Method", "Access-Control-Request-Headers"]
        cookies {
          forward = "all"
        }
      }

      min_ttl     = 0
      default_ttl = 0
      max_ttl     = 0
    }
  }

  dynamic "ordered_cache_behavior" {
    for_each = var.demo_enabled ? [1] : []
    content {
      path_pattern            = "/swagger-ui/*"
      allowed_methods          = ["GET", "HEAD"]
      cached_methods            = ["GET", "HEAD"]
      target_origin_id          = "ALB-comic-store-api"
      viewer_protocol_policy    = "redirect-to-https"
      compress                   = false

      forwarded_values {
        query_string = true
        cookies {
          forward = "none"
        }
      }

      min_ttl     = 0
      default_ttl = 0
      max_ttl     = 0
    }
  }

  dynamic "ordered_cache_behavior" {
    for_each = var.demo_enabled ? [1] : []
    content {
      path_pattern            = "/v3/api-docs*"
      allowed_methods          = ["GET", "HEAD"]
      cached_methods            = ["GET", "HEAD"]
      target_origin_id          = "ALB-comic-store-api"
      viewer_protocol_policy    = "redirect-to-https"
      compress                   = false

      forwarded_values {
        query_string = true
        cookies {
          forward = "none"
        }
      }

      min_ttl     = 0
      default_ttl = 0
      max_ttl     = 0
    }
  }

  dynamic "ordered_cache_behavior" {
    for_each = var.demo_enabled ? [1] : []
    content {
      path_pattern            = "/webjars/*"
      allowed_methods          = ["GET", "HEAD"]
      cached_methods            = ["GET", "HEAD"]
      target_origin_id          = "ALB-comic-store-api"
      viewer_protocol_policy    = "redirect-to-https"
      compress                   = false

      forwarded_values {
        query_string = true
        cookies {
          forward = "none"
        }
      }

      min_ttl     = 0
      default_ttl = 0
      max_ttl     = 0
    }
  }

  custom_error_response {
    error_code            = 403
    response_code          = 200
    response_page_path    = "/index.html"
    error_caching_min_ttl = 0
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
    minimum_protocol_version        = "TLSv1"
  }
}
