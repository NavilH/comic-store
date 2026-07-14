# Comic Store — Terraform

Manages all comic-store AWS infrastructure: ECS/Fargate, RDS, ALB, ECR, S3, CloudFront, IAM, SSM secrets, and CloudWatch (log group retention + a dashboard).

Replaces the old manual console restore process. State is local (`.tfstate`, gitignored) — this is a solo portfolio project, no shared team needing remote state.

## Prerequisites

- [Install Terraform](https://developer.hashicorp.com/terraform/install) (>= 1.5.0 — needed for `import` blocks).
- A `comic-store-terraform` AWS CLI profile with broad permissions (kept separate from the `comic-store-deploy` user used by GitHub Actions).

```powershell
$env:AWS_PROFILE = "comic-store-terraform"
```
```bash
export AWS_PROFILE=comic-store-terraform
```

## Demo on/off

One variable, `demo_enabled`, controls the entire billable backend:

**Wake for a demo:**
```
terraform apply -var="demo_enabled=true"
```
**Hibernate after:**
```
terraform apply -var="demo_enabled=false"
```

That's it — always a plain `terraform apply`, never `terraform destroy -target=...`. RDS, the ALB, and the ALB listener are gated with `count = var.demo_enabled ? 1 : 0`; the ECS service's `desired_count` follows the same variable. CloudFront, the ECS service/task definition, the CloudWatch dashboard, ECR, S3, and IAM are **never** destroyed by this toggle — only updated in place (CloudFront's ALB-facing origin and its 4 cache behaviors are `dynamic` blocks that simply add/remove themselves). This means:
- The public CloudFront URL never changes across hibernate/wake cycles.
- While hibernated, the only ongoing cost is S3/ECR storage and CloudWatch log retention — a few cents a month at most, not the ~$30+/month RDS + ALB cost hourly whenever they exist.

### Why it's built this way (incident note, 2026-07-14)

The original design used `terraform destroy -target=aws_db_instance.main -target=aws_lb.main -target=aws_lb_listener.http` to hibernate. `-target` destroys the named resources **and everything that depends on them**, to keep the state graph consistent. CloudFront's ALB origin referenced `aws_lb.main.dns_name` directly, and the ECS service had an explicit (unnecessary) `depends_on` the listener — so that one command cascaded into destroying the CloudFront distribution, the ECS service, its task definition, the CloudWatch dashboard, and an IAM policy too. The site went fully down (not just the backend), and CloudFront had to be recreated with a brand-new domain name, since that's assigned randomly and can't be recovered or chosen.

The fix: stop using `-target` for routine hibernation entirely. `count`-gate only the resources that should actually vanish (RDS, ALB, listener), and make everything downstream (CloudFront, ECS service, dashboard) reference them defensively (`aws_lb.main[0]`, guarded by the same `demo_enabled` conditional, or via `dynamic` blocks / `concat()` for optional nested config) so a normal `apply` just updates those resources instead of destroying them.

## First-time / from-scratch setup

```
terraform init
terraform plan
```

Review the plan before applying — existing hand-clicked resources (ECS cluster, ECR, S3, CloudFront OAC, both security groups, the IAM role, the target group, the log group) import via native `import` blocks (Terraform 1.5+) and should show **no changes or additive only**.

```
terraform apply -var="demo_enabled=true"
```

Then hit `terraform output cloudfront_domain_name` in a browser and confirm register → login → browse comics works end-to-end.

## Notes

- `random_password` generates the RDS master password and the JWT secret on first apply; both land in SSM `SecureString` parameters, referenced by the ECS task definition's `secrets` block — never as plaintext environment variables, never committed anywhere. Both values persist in state across hibernate/wake cycles (not regenerated), so RDS gets the same password back every time it's recreated.
- `DB_URL` also goes through SSM rather than the task definition's `environment` block, specifically so an RDS endpoint change (e.g. after being recreated) only needs an SSM parameter update, not a new task definition revision — `container_definitions` is `ignore_changes`-frozen after creation because CI (`.github/workflows/deploy.yml`) owns image deploys from then on.
