# Avnzor OMS Backend

Order Management System backend for Avnzor. A modular Spring Boot application that connects to an **existing MySQL database**, uses **JWT authentication** with role/department-based access, and manages schema changes exclusively through **Flyway**.

---

## Documentation index

| File | Purpose |
|---|---|
| [README.md](README.md) | Developer guide — setup, usage, API, deployment overview |
| [CONTEXT.md](CONTEXT.md) | Project context for future sessions — constraints, status, decisions |
| [FIXES.md](FIXES.md) | Issues encountered and how they were resolved |
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
├── auth/                    # JWT login, security, warehouse users
├── orders/                  # Orders (Logistic team only) — placeholder
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

## Database and Flyway

Connects to an **existing production database**. Flyway baseline treats current schema as **version 1**; new scripts start at **V2__**.

```bash
# Add migration
src/main/resources/db/migration/V3__Your_change.sql

# Repair checksum mismatch (Windows line endings)
./mvnw flyway:repair
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

## Common commands

```powershell
.\mvnw.cmd spring-boot:run          # Start locally
.\mvnw.cmd test                     # Run tests
.\mvnw.cmd clean verify             # Full CI verification
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
- [ ] Audit log entity and service
- [ ] Multi-tenancy
- [ ] BCrypt migration for legacy passwords
- [ ] GitHub secrets + server provisioning
- [ ] Docker-based CD (optional)

---

## License

Internal Avnzor project. All rights reserved.
