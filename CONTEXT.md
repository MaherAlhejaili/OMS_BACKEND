# Project Context

Essential context for continuing development after a fresh session. Read this before making architectural decisions.

---

## What this project is

**Avnzor OMS Backend** — enterprise Order Management System API.

- **Repo:** https://github.com/MaherAlhejaili/OMS_BACKEND.git
- **Default branch:** `master`
- **Package root:** `com.avnzor.oms_backend`
- **Architecture:** Modular monolith, package-by-feature

---

## Critical constraints

### Existing production database

- Connects to an **existing MySQL database** — schema already exists with production tables.
- **Never** use `ddl-auto: update` or `create`. Always `validate`.
- **Never** add Flyway migrations that recreate existing tables.
- Flyway is split: **platform** migrations (`db/platform-migration/`) and **tenant** migrations (`db/tenant-migration/`).
- First application-owned migration: `V2__Create_audit_log.sql` (new table only).

### Auth source table

Users come from legacy table **`sma_warehouse_users`** (from a prior TypeORM project):

| Column | Type | Notes |
|---|---|---|
| `id` | INT | PK |
| `username` | VARCHAR | Login |
| `password` | VARCHAR | BCrypt or legacy plain-text |
| `name` | VARCHAR | Display name |
| `role` | VARCHAR | Mapped to `ROLE_*` authority |
| `department` | VARCHAR | Mapped to `DEPT_*` authority; drives orders access |

Entity: `auth/entity/WarehouseWorker.java`

---

## What is implemented vs placeholder

| Feature | Status |
|---|---|
| JWT login (`POST /api/v1/auth/login`) | Implemented |
| JWT filter + stateless security | Implemented |
| RBAC — orders (Logistic dept only) | Implemented |
| RBAC — tracking (any authenticated user) | Implemented |
| Global `ApiResponse` wrapper | Implemented |
| Global exception handling | Implemented |
| Swagger UI (`/swagger-ui.html`) | Implemented (disabled in prod) |
| Flyway baseline + V2 audit_log table | Implemented |
| Global API audit logging (`audit_log`) | Implemented |
| Orders business logic | **Placeholder** controller |
| Tracking business logic | **Placeholder** controller |
| Multi-tenancy | **Implemented** — database per tenant, dynamic routing |
| Returns module | **Not started** |
| Audit log JPA entity/service | **Implemented** — global filter on `/api/**` |
| MapStruct mappers | **Not added** |
| Testing foundation (JUnit, Testcontainers, JaCoCo) | **Implemented** — see `TESTING.md` |
| Docker image in CI/CD | Dockerfile exists; CD still deploys JAR via SSH |

---

## RBAC rules (current)

| Endpoint | Rule |
|---|---|
| `POST /api/v1/auth/login` | Public |
| `GET /api/v1/tracking` | `@PreAuthorize("isAuthenticated()")` |
| `GET /api/v1/orders` | `@PreAuthorize("@departmentAccess.isLogistic(authentication)")` |
| Actuator `/health` | Public |
| Swagger UI | Public (dev/staging); disabled in prod profile |

Logistic departments (case-insensitive): `Logistic`, `Logistics`, `Logistic team`.

Component: `auth/security/DepartmentAccess.java`

---

## API response contract

All endpoints return `ApiResponse<T>` (via `GlobalResponseHandler`):

```json
{
  "success": true,
  "message": "Request completed successfully",
  "data": { },
  "timestamp": "2026-07-12T11:00:00Z"
}
```

Errors include `status`, `path`, and optional `errors` map. Security filter errors use the same shape.

Bypass wrapping: `/actuator/**`, `/v3/api-docs/**`, `/swagger-ui/**`

---

## Spring profiles and databases

| Profile | Database (default) | Notes |
|---|---|---|
| `dev` | `avnzor` @ 127.0.0.1 | Default for local dev |
| `test` | `avnzor_test` | Uses extra test migration for `sma_warehouse_users` |
| `staging` | Env vars only | `application-staging.yml` |
| `prod` | Env vars only | Swagger disabled |

---

## Deployment state (as of initial setup)

- GitHub Environments **`staging`** and **`production`** created.
- **Secrets not yet configured** — deployment intentionally deferred ~1 week.
- Push to `master` will build and attempt staging deploy (will fail until secrets exist).
- Production requires manual workflow dispatch + environment approval.
- Pipeline order: **Build → Staging → Production**.
- Same JAR/Docker image for all environments; only env vars and profile differ.

Server layout: `/opt/avnzor/{app,config,logs,backups}` — see `deploy/README.md`.

---

## Known environment warnings (non-blocking)

- MySQL connector reports version **5.5.5** — Hibernate warns minimum supported is 8.0. Consider upgrading MySQL server.
- Flyway warns schema version **3** is newer than latest repo migration **2** — leftover DB history entry; startup continues.
- Spring Boot **4.1.0** is in use (not 3.x as originally specified). Uses Jackson 3 for HTTP; explicit `ObjectMapper` bean needed for security JSON.

---

## Planned roadmap

1. Full orders module (entities mapped to existing tables)
2. Full tracking module
3. Returns module
4. ~~Audit logging (entity + service for `audit_log` table)~~ — done (global API filter + `AuditLogService`)
5. Multi-tenancy (`tenant_id` reserved in audit_log migration)
6. BCrypt password migration for legacy users
7. GitHub secrets + server provisioning
8. Optional: switch CD from JAR/SSH to Docker registry

---

## Key files to read first

| File | Why |
|---|---|
| `application.yml` | Shared config, Flyway, JWT, integrations |
| `auth/config/SecurityConfig.java` | Security rules |
| `common/exception/GlobalExceptionHandler.java` | Error handling |
| `common/config/GlobalResponseHandler.java` | Response wrapping |
| `db/migration/V2__Create_audit_log.sql` | `audit_log` table schema |
| `audit/filter/AuditLoggingFilter.java` | Global `/api/**` request audit |
| `audit/service/AuditLogService.java` | Persist audit rows (async + sync) |
| `.github/workflows/cd.yml` | Deployment pipeline |
| `Dockerfile` | Container build |
| `FIXES.md` | Past issues and solutions |
| `TOOLS.md` | Tech stack reference |
