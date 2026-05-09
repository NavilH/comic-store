# Comic Store

A full-stack inventory management system for a comic book store. Built with Spring Boot and Angular, deployed on AWS.

**Live demo:** https://d3rkwmo70slodw.cloudfront.net  
**API docs:** https://d3rkwmo70slodw.cloudfront.net/swagger-ui/index.html

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Angular 19, Angular Material |
| Backend | Java 21, Spring Boot 3.5, Spring Security (JWT) |
| Database | PostgreSQL (AWS RDS) |
| Infrastructure | AWS ECS (Fargate), ALB, S3, CloudFront, ECR |
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
  └── CloudFront (d3rkwmo70slodw.cloudfront.net)
        ├── /api/*          → ALB → ECS Fargate (Spring Boot)
        ├── /swagger-ui/*   → ALB → ECS Fargate
        ├── /v3/api-docs*   → ALB → ECS Fargate
        └── /*              → S3  (Angular build)

ECS Fargate (Spring Boot)
  └── RDS PostgreSQL (Flyway migrations)
```

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
