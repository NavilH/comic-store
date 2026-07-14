# Comic Store

A full-stack inventory management system for a comic book store. Built with Spring Boot and Angular, deployed on AWS with the entire infrastructure managed as code via Terraform.

**Live demo:** https://d1k1pjeod92ng4.cloudfront.net *(backend may be hibernated to save cost — the frontend loads either way; message me if you want it woken up for a live demo)*
**API docs:** https://d1k1pjeod92ng4.cloudfront.net/swagger-ui/index.html

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Angular 19, Angular Material |
| Backend | Java 21, Spring Boot 3.5, Spring Security (JWT) |
| Database | PostgreSQL (AWS RDS) |
| Infrastructure | AWS ECS (Fargate), ALB, S3, CloudFront, ECR, IAM, SSM Parameter Store, CloudWatch — provisioned via **Terraform** |
| CI/CD | GitHub Actions |

---

## Features

- JWT authentication — register, login, Bearer token on all protected routes
- Role-based access control — first registered user is ADMIN, subsequent users are USER
- Full CRUD for Comics, Authors, Publishers, Inventory, and Sales
- Paginated comic and sale listings with genre and publisher filters
- Inventory restocking with stock tracking
- Referential integrity — deleting a publisher or author with linked comics returns 409; deleting a sold comic returns 409
- OpenAPI docs with Bearer auth support at `/swagger-ui/index.html`

---

## Architecture

```
Browser
  │
  └── CloudFront (d1k1pjeod92ng4.cloudfront.net)
        ├── /api/*          → ALB → ECS Fargate (Spring Boot)
        ├── /swagger-ui/*   → ALB → ECS Fargate
        ├── /v3/api-docs*   → ALB → ECS Fargate
        └── /*              → S3  (Angular build)

ECS Fargate (Spring Boot)
  └── RDS PostgreSQL (Flyway migrations)
```

---

## Infrastructure as Code

The entire AWS stack — ECS, RDS, ALB, ECR, S3, CloudFront, IAM, and CloudWatch — is provisioned and managed with Terraform (`terraform/`), replacing what was originally hand-clicked through the console.

**Cost-conscious by design:** a single `demo_enabled` boolean toggles the two hourly-billed resources (RDS, the ALB) on and off:
```
terraform apply -var="demo_enabled=true"   # wake for a demo
terraform apply -var="demo_enabled=false"  # hibernate — RDS + ALB destroyed, everything else untouched
```
CloudFront, the ECS service, and the CloudWatch dashboard are never destroyed by this toggle — only updated in place (via `count` and `dynamic` blocks) — so the public URL stays stable and idle cost is a few cents/month instead of ~$30+/month.

**Secrets management:** the JWT signing key and database credentials are auto-generated (`random_password`) and stored in AWS Systems Manager Parameter Store, resolved by ECS at container runtime — never as plaintext environment variables, never committed to source control.

This design exists because the first version of the hibernate command (`terraform destroy -target=...`) taught a real lesson: `-target` destroys not just the named resource but everything that depends on it. A scoped destroy of just the ALB cascaded into destroying the live CloudFront distribution and the ECS service too. The fix — conditional resources driven by one variable, with a normal `terraform apply` instead of `-target` — is what's running today.

---

## CI/CD

Every push to `main` triggers a GitHub Actions workflow with two parallel jobs:

- **Backend** — Maven test → Docker build → push to ECR → ECS force-deploy
- **Frontend** — `ng build` → S3 sync → CloudFront invalidation

---

## Running Locally

**Prerequisites:** Java 21, Maven, Node 22, PostgreSQL

**Backend**
```bash
cd comic-store
DB_URL=jdbc:postgresql://localhost:5432/comic_store \
DB_USERNAME=postgres \
DB_PASSWORD=yourpassword \
mvn spring-boot:run
```
Runs on `http://localhost:8081`

**Frontend**
```bash
cd comic-store-ui
npm install
ng serve
```
Runs on `http://localhost:4200`

---

## API Overview

| Resource | Endpoints |
|----------|-----------|
| Auth | `POST /api/auth/register`, `POST /api/auth/login` |
| Comics | `GET /api/comics`, `GET /api/comics/{id}`, `POST`, `PUT`, `DELETE` |
| Authors | `GET /api/authors`, `GET /api/authors/{id}`, `POST`, `PUT`, `DELETE` |
| Publishers | `GET /api/publishers`, `GET /api/publishers/{id}`, `POST`, `PUT`, `DELETE` |
| Inventory | `GET /api/inventory`, `GET /api/inventory/comic/{id}`, `POST`, `PATCH /{id}/restock` |
| Sales | `GET /api/sales`, `GET /api/sales/{id}`, `POST` |

Full interactive docs at [`/swagger-ui/index.html`](https://d3rkwmo70slodw.cloudfront.net/swagger-ui/index.html).
