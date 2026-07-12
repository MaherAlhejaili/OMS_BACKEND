# Avnzor OMS Backend

Order Management System backend for Avnzor. A modular Spring Boot application that connects to an **existing MySQL database**, uses **JWT authentication** with role/department-based access, and manages schema changes exclusively through **Flyway**.

---

## Documentation index

| File | Purpose |
|---|---|
| [README.md](README.md) | Developer guide — setup, usage, API, deployment overview |
| [CONTEXT.md](CONTEXT.md) | Project context for future sessions — constraints, status, decisions |
| [FIXES.md](FIXES.md) | Issues encountered and how they were resolved |
| [TESTING.md](TESTING.md) | Testing strategy, TDD workflow, Testcontainers, JaCoCo |
| [TOOLS.md](TOOLS.md) | Technologies and principles (brief reference) |
| [deploy/README.md](deploy/README.md) | Server layout, systemd, GitHub secrets |

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Build | Maven (wrapper included) |
| Database | MySQL |
| ORM | Spring Data JPA (Hibernate `validate` only) |
| Migrations | Flyway |
| Security | Spring Security + JWT |
| API docs | springdoc-openapi (Swagger UI) |
| Container | Docker (multi-stage) |
| CI/CD | GitHub Actions |

---

## Prerequisites

- **Java 21** (Temurin recommended)
- **Maven** — optional; project includes `./mvnw`
- **MySQL** running locally
- **Git**
- **Docker Desktop** — optional, for container builds

---

## Quick start

### 1. Clone

```bash
git clone https://github.com/MaherAlhejaili/OMS_BACKEND.git
cd OMS_BACKEND
```

### 2. Create databases

```sql
CREATE DATABASE IF NOT EXISTS avnzor;
CREATE DATABASE IF NOT EXISTS avnzor_test;
```

### 3. Environment variables (optional for local dev)

```powershell
$env:DATABASE_HOST="127.0.0.1"
$env:DATABASE_NAME="avnzor"
$env:DATABASE_USER="root"
$env:DATABASE_PASSWORD="your-password"
$env:JWT_SECRET="local-dev-secret-at-least-32-characters"
```

> Never commit `.env` files or real secrets to Git.

### 4. Start

```powershell
.\mvnw.cmd spring-boot:run
```

Server: **http://localhost:8080** — profile **`dev`**

### 5. Verify

| URL | Purpose |
|---|---|
| http://localhost:8080/actuator/health | Health check |
| http://localhost:8080/swagger-ui.html | API documentation |

---

## Project structure

```
src/main/java/com/avnzor/oms_backend/
├── OmsBackendApplication.java
├── tenants/                 # Database-per-tenant routing, provisioning, platform admin
├── auth/                    # JWT login, security, warehouse users
├── audit/                   # Global API audit logging (per-tenant audit_log table)
├── tracking/                # Tracking (all authenticated) — placeholder
├── common/
│   ├── config/              # GlobalResponseHandler, JacksonConfig
│   ├── dto/                 # ApiResponse wrapper
│   └── exception/           # GlobalExceptionHandler, custom exceptions
└── config/                  # OpenApiConfig

src/main/resources/
├── application.yml          # Shared config
├── application-{dev,test,staging,prod}.yml
├── logback-spring.xml
└── db/migration/            # Flyway scripts (V2+)

src/test/resources/db/test-migration/   # Test-only migrations
deploy/                                 # Server deploy assets
.github/workflows/                      # CI and CD pipelines
Dockerfile                              # Multi-stage container build
```

### Architecture rules

```
Controller → Service → Repository → Database
```

- Controllers never access repositories directly.
- Business logic lives in services.
- API uses DTOs, never JPA entities in responses.
- Hibernate **validates** only; Flyway owns schema changes.

---

## API response format

All endpoints return a unified `ApiResponse<T>`:

**Success:**
```json
{
  "success": true,
  "message": "Request completed successfully",
  "data": { },
  "timestamp": "2026-07-12T11:00:00Z"
}
```

**Error:**
```json
{
  "success": false,
  "message": "Validation failed",
  "timestamp": "2026-07-12T11:00:00Z",
  "status": 400,
  "path": "/api/v1/auth/login",
  "errors": { "username": "must not be blank" }
}
```

---

## Audit logging

Every request to `/api/**` is recorded automatically in the `audit_log` table (created by Flyway `V2__Create_audit_log.sql`). No controller changes are required for standard API traffic.

| Column | Purpose |
|---|---|
| `event_type` | Always `API_REQUEST` for HTTP traffic; use `AuditLogService.log()` for domain events |
| `entity_type` | API resource segment (e.g. `orders`, `auth`, `tracking`) |
| `entity_id` | Path identifier when present (e.g. `/api/v1/orders/42` → `42`) |
| `actor` | Authenticated username, or `anonymous` for public endpoints |
| `tenant_id` | Reserved for future multi-tenancy |
| `details` | JSON metadata: method, path, status code, duration, client IP, user id |
| `created_at` | Request completion timestamp |

**What is logged:** HTTP method, path, query string, response status, duration, client IP, and authenticated user (when present).

**What is never logged:** Passwords, JWT tokens, or `Authorization` headers.

Audit writes run **asynchronously** so they do not block API responses. Failures are logged to the application log only.

### Programmatic audit events

Inject `AuditLogService` in any service to record domain-specific actions (e.g. order created, return approved):

```java
auditLogService.log(new AuditLogEntry(
    "ORDER_CREATED",
    "orders",
    orderId.toString(),
    principal.getUsername(),
    null,
    Map.of("orderNumber", orderNumber)
));
```

---

## Authentication and authorization

Users are loaded from existing table **`sma_warehouse_users`**.

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{ "username": "your-username", "password": "your-password" }
```

Use `data.accessToken` from the wrapped response on protected calls:

```http
Authorization: Bearer <accessToken>
```

### Endpoints

| Endpoint | Access |
|---|---|
| `POST /api/v1/auth/login` | Public |
| `GET /api/v1/tracking` | Any authenticated user |
| `GET /api/v1/orders` | Logistic department only |

Logistic departments: `Logistic`, `Logistics`, `Logistic team` (case-insensitive).

### Swagger

1. Open http://localhost:8080/swagger-ui.html
2. Call `POST /api/v1/auth/login`
3. Copy `accessToken` from response `data`
4. Click **Authorize** → `Bearer <token>`

Swagger is **disabled** in the `prod` profile.

---

## Spring profiles

| Profile | Used for | Activated by |
|---|---|---|
| `dev` | Local development | Default |
| `test` | Maven tests / CI | `SPRING_PROFILES_ACTIVE=test` |
| `staging` | Staging server | `application.env` on server |
| `prod` | Production server | `application.env` on server |

---

## Multi-tenancy (database per tenant)

Each tenant has its **own MySQL database**. Business data is never shared and **no `tenant_id` column** exists in tenant tables.

| Database | Purpose |
|---|---|
| `oms` (platform) | `tenants` registry, `platform_users`, encrypted DB credentials |
| `tenant_1`, `tenant_2`, … | Users, orders, audit logs, all business modules |

### Dev databases

| DB | Created by |
|---|---|
| `oms` | Platform Flyway on startup (`db/platform-migration/`) |
| `tenant_1` | Dev seed + tenant Flyway (`db/tenant-migration/`) |

### Login (tenant users)

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "tenantSlug": "tenant-1",
  "username": "logistic.user",
  "password": "password"
}
```

JWT includes `tenantId` and `tenantSlug`. Every authenticated request resolves the tenant and routes to the correct database automatically.

### Platform admin (provision tenants)

```http
POST /api/v1/platform/auth/login
{ "username": "platform.admin", "password": "admin123" }

POST /api/v1/platform/tenants
Authorization: Bearer <platformToken>
{
  "slug": "tenant-2",
  "name": "Tenant Two",
  "databaseHost": "127.0.0.1",
  "databasePort": 3306,
  "databaseName": "tenant_2",
  "databaseUsername": "root",
  "databasePassword": ""
}
```

New tenants are provisioned **without restart**: database created, Flyway migrations applied, datasource registered dynamically.

### Required env var

| Variable | Purpose |
|---|---|
| `TENANT_DB_ENCRYPTION_KEY` | AES encryption key for tenant DB passwords (min 32 chars) |
| `PLATFORM_DATABASE_NAME` | Platform DB name (default: `oms`) |

---

## Database and Flyway

Connects to an **existing production database**. Existing tables are recorded as **baseline version 1**; application-owned changes start at **V2__**.

### One-time baseline (existing databases)

Run **once per database** that already has tables but no `flyway_schema_history` (staging, production, or a fresh clone of prod):

```powershell
.\mvnw.cmd flyway:baseline
.\mvnw.cmd flyway:migrate
```

This marks the current schema as version **1** without running any scripts. After that, the app only applies new migrations on startup.

> Your local `avnzor` DB is already baselined (currently at V5). You do **not** need to run baseline again.

### Ongoing migrations

`baseline-on-migrate` is **disabled** — Flyway will not auto-baseline on startup. If `flyway_schema_history` is missing on a non-empty database, startup fails until you run `flyway:baseline` manually.

```bash
# Add a new migration (use next version number)
src/main/resources/db/migration/V6__Your_change.sql

# Repair checksum mismatch (Windows line endings)
.\mvnw.cmd flyway:repair

# Check migration status
.\mvnw.cmd flyway:info
```

---

## Environment variables

| Variable | Required on servers |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Yes |
| `DATABASE_HOST` | Yes |
| `DATABASE_PORT` | Yes |
| `DATABASE_NAME` | Yes |
| `DATABASE_USER` | Yes |
| `DATABASE_PASSWORD` | Yes |
| `JWT_SECRET` | Yes |
| `JWT_EXPIRATION` | Yes |
| `SERVER_PORT` | Yes |
| `LOG_FILE_PATH` | Recommended |

Templates: `deploy/config/application.env.example`, `production.env.example`

---

## Docker

```bash
# Build
docker build -t avnzor-oms:latest .

# Run locally (Windows — use host.docker.internal for MySQL)
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e DATABASE_HOST=host.docker.internal \
  -e DATABASE_NAME=avnzor \
  -e DATABASE_USER=root \
  -e DATABASE_PASSWORD= \
  -e JWT_SECRET=local-dev-secret-at-least-32-characters \
  -e JWT_EXPIRATION=86400000 \
  -e SERVER_PORT=8080 \
  avnzor-oms:latest
```

Same image for staging and production — only env vars and profile differ.

---

## CI/CD

| Workflow | Trigger | Pipeline |
|---|---|---|
| `ci.yml` | Pull requests | Build + test |
| `cd.yml` | Push to `master` | Build → deploy **staging** |
| `cd.yml` | Manual run | Build → staging → **production** (approval required) |

GitHub Environment secrets are **not yet configured** — deployment deferred until servers are ready. See [deploy/README.md](deploy/README.md).

---

## Testing

See [TESTING.md](TESTING.md) for the full strategy. Quick commands:

```powershell
.\mvnw.cmd test              # Unit tests only
.\mvnw.cmd clean verify      # Unit + integration + JaCoCo report
```

Integration tests require **Docker Desktop** (Testcontainers). They use an isolated MySQL instance — not your local `oms` / `tenant_1` databases.

---

## Common commands

```powershell
.\mvnw.cmd spring-boot:run          # Start locally
.\mvnw.cmd test                     # Unit tests
.\mvnw.cmd clean verify            # Full test suite + coverage
.\mvnw.cmd flyway:repair            # Fix Flyway checksum mismatch
.\mvnw.cmd package -DskipTests       # Build JAR

# Kill process on port 8080 (Windows)
Get-NetTCPConnection -LocalPort 8080 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

---

## Troubleshooting

See [FIXES.md](FIXES.md) for detailed issue history.

| Problem | Solution |
|---|---|
| Port 8080 in use | Kill process (command above) or `$env:SERVER_PORT="8081"` |
| Unknown database | Create `avnzor` / `avnzor_test` |
| Flyway checksum mismatch | `.\mvnw.cmd flyway:repair` |
| Docker daemon not running | Start Docker Desktop |
| `ObjectMapper` bean error | Ensure `spring-boot-starter-jackson` and `JacksonConfig` exist |
| 403 on `/orders` | User department must be Logistic |
| 401 Unauthorized | Include `Authorization: Bearer <token>` |

---

## Contributing

1. Branch from `master`.
2. Follow package-by-feature structure.
3. Add Flyway migrations for schema changes.
4. Keep secrets out of source control.
5. Open a PR — `ci.yml` must pass.
6. Merge to `master` triggers staging deploy (once secrets are configured).

---

## Roadmap

- [ ] Full orders implementation (map existing tables)
- [ ] Full tracking implementation
- [ ] Returns module
- [x] Global audit logging for all `/api/**` requests
- [x] Database-per-tenant multi-tenancy
- [ ] BCrypt migration for legacy passwords
- [ ] GitHub secrets + server provisioning
- [ ] Docker-based CD (optional)

---

## License

Internal Avnzor project. All rights reserved.
