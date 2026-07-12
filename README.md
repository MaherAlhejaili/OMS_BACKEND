# Avnzor OMS Backend

Order Management System backend for Avnzor. A modular Spring Boot application that connects to an **existing MySQL database**, uses **JWT authentication** with role/department-based access, and manages schema changes exclusively through **Flyway**.

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.x |
| Build | Maven (wrapper included) |
| Database | MySQL |
| ORM | Spring Data JPA (Hibernate `validate` only) |
| Migrations | Flyway |
| Security | Spring Security + JWT |
| CI/CD | GitHub Actions |

---

## Prerequisites

Before you start, install:

- **Java 21** (Temurin recommended)
- **Maven** — optional; the project includes `./mvnw`
- **MySQL 8+** running locally
- **Git**

---

## Quick start

### 1. Clone the repository

```bash
git clone https://github.com/MaherAlhejaili/OMS_BACKEND.git
cd OMS_BACKEND
```

### 2. Create the local database

The `dev` profile connects to database `avnzor` by default. The schema already exists in shared environments; for local development ensure the database is available:

```sql
CREATE DATABASE IF NOT EXISTS avnzor;
```

For automated tests, also create:

```sql
CREATE DATABASE IF NOT EXISTS avnzor_test;
```

### 3. Configure environment variables (optional)

Local development works with defaults in `application-dev.yml`. Override when needed:

```powershell
# Windows PowerShell
$env:DATABASE_HOST="127.0.0.1"
$env:DATABASE_NAME="avnzor"
$env:DATABASE_USER="root"
$env:DATABASE_PASSWORD="your-password"
$env:JWT_SECRET="local-dev-secret-at-least-32-characters"
```

```bash
# Linux / macOS
export DATABASE_HOST=127.0.0.1
export DATABASE_NAME=avnzor
export DATABASE_USER=root
export DATABASE_PASSWORD=your-password
export JWT_SECRET=local-dev-secret-at-least-32-characters
```

> **Never commit `.env` files or real secrets to Git.**

### 4. Start the application

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

The server starts on **http://localhost:8080** with the **`dev`** profile.

### 5. Verify it is running

```bash
curl http://localhost:8080/actuator/health
```

Expected response: `{"status":"UP"}`

---

## Project structure

The codebase follows a **package-by-feature** modular monolith:

```
src/main/java/com/avnzor/oms_backend/
├── OmsBackendApplication.java
├── auth/                    # JWT login, security, warehouse users
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── filter/
│   ├── security/
│   └── config/
├── orders/                  # Orders feature (Logistic team only)
│   └── controller/
├── tracking/                # Tracking feature (all authenticated users)
│   └── controller/
└── common/                  # Shared exceptions and cross-cutting code
    └── exception/

src/main/resources/
├── application.yml          # Shared configuration
├── application-dev.yml      # Local development
├── application-test.yml     # Automated tests
├── application-staging.yml  # Staging server
├── application-prod.yml     # Production server
├── logback-spring.xml       # Logging (console + file on staging/prod)
└── db/migration/            # Flyway SQL migrations
```

### Architecture rules

```
Controller → Service → Repository → Database
```

- Controllers never access repositories directly.
- Business logic lives in services.
- API responses use DTOs, never JPA entities.
- Hibernate **validates** mappings only; Flyway owns schema changes.
- Security is configured in the `auth` module, not in controllers.

---

## Spring profiles

| Profile | Used for | Activated by |
|---|---|---|
| `dev` | Local development | Default in `application.yml` |
| `test` | Maven tests / CI | `SPRING_PROFILES_ACTIVE=test` |
| `staging` | Staging server | `application.env` on server |
| `prod` | Production server | `application.env` on server |

Shared settings (Flyway, JPA `validate`, dialect) live in `application.yml`. Profile files contain **only environment-specific overrides**.

---

## Database and Flyway

This project connects to an **existing production database**. Flyway was introduced safely using a baseline:

- Existing schema = **version 1** (baseline, not recreated)
- New changes start at **V2__** and above
- `spring.jpa.hibernate.ddl-auto=validate` — Hibernate never alters tables

### Adding a migration

1. Create a new file under `src/main/resources/db/migration/`:

   ```
   V3__Describe_your_change.sql
   ```

2. Follow naming: `V{version}__{Snake_Case_Description}.sql`

3. **Never edit** a migration that has already been applied to shared environments.

4. Test locally, then commit.

### Flyway checksum mismatch (common on Windows)

If startup fails with a checksum mismatch after line-ending changes:

```bash
./mvnw flyway:repair
```

---

## Authentication and authorization

Users are loaded from the existing `sma_warehouse_users` table.

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "your-username",
  "password": "your-password"
}
```

Response includes `accessToken`. Send it on protected requests:

```http
Authorization: Bearer <accessToken>
```

### Access rules

| Endpoint | Access |
|---|---|
| `POST /api/v1/auth/login` | Public |
| `GET /api/v1/tracking` | Any authenticated user |
| `GET /api/v1/orders` | Logistic department only |

Logistic departments recognized: `Logistic`, `Logistics`, `Logistic team` (case-insensitive).

### Test users (test profile only)

When running tests, seeded users are available:

| Username | Password | Department | Orders access |
|---|---|---|---|
| `logistic.user` | `password` | Logistic | Yes |
| `warehouse.user` | `password` | Warehouse | No |

---

## API testing (manual)

### PowerShell

```powershell
# Login
$login = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"YOUR_USERNAME","password":"YOUR_PASSWORD"}'

$headers = @{ Authorization = "Bearer $($login.accessToken)" }

# Tracking (any authenticated user)
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/tracking" -Headers $headers

# Orders (Logistic team only)
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/orders" -Headers $headers
```

### curl

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"YOUR_USERNAME","password":"YOUR_PASSWORD"}'

# Protected endpoint
curl http://localhost:8080/api/v1/tracking \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Use real credentials from `sma_warehouse_users` when running against the `dev` profile.

---

## Running tests

```bash
./mvnw test
```

Full verification (what CI runs on pull requests):

```bash
./mvnw clean verify
```

Tests use the `test` profile and database `avnzor_test`.

---

## Environment variables

| Variable | Description | Required on servers |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Active profile (`staging`, `prod`) | Yes |
| `DATABASE_HOST` | MySQL host | Yes |
| `DATABASE_PORT` | MySQL port | Yes |
| `DATABASE_NAME` | Database name | Yes |
| `DATABASE_USER` | Database user | Yes |
| `DATABASE_PASSWORD` | Database password | Yes |
| `JWT_SECRET` | JWT signing secret (min 32 chars) | Yes |
| `JWT_EXPIRATION` | Token lifetime in milliseconds | Yes |
| `SERVER_PORT` | HTTP port | Yes |
| `LOG_FILE_PATH` | Log file path on server | Recommended |

Optional integration settings (see `application.yml`):

| Variable | Description |
|---|---|
| `TRACKING_API_URL` | External tracking API base URL |
| `SHIPPING_PROVIDER_URL` | Shipping provider API URL |
| `SHIPPING_PROVIDER_API_KEY` | Shipping provider API key |
| `NOTIFICATION_WEBHOOK_URL` | Webhook for notifications |

Copy `deploy/config/application.env.example` as a starting point. **Do not commit real `.env` files.**

---

## CI/CD

| Workflow | Trigger | What it does |
|---|---|---|
| `ci.yml` | Pull requests | Build and test |
| `cd.yml` | Push to `master` | Build, test, deploy to **staging** |
| `cd.yml` | Manual run | Build, test, deploy to **production** (requires approval) |

### Deployment model

- Staging and production run the **same JAR artifact**.
- Only environment variables and Spring profile differ.
- Promoting staging → production requires **no code changes**.

See [deploy/README.md](deploy/README.md) for server layout, systemd setup, and GitHub Environment secrets.

---

## Common commands

```bash
# Start locally
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build JAR
./mvnw package -DskipTests

# Repair Flyway checksums after line-ending issues
./mvnw flyway:repair
```

### IntelliJ IDEA

1. Open the project as a Maven project.
2. Run `OmsBackendApplication` from `src/main/java`.
3. The `dev` profile is active by default.

---

## Troubleshooting

| Problem | Solution |
|---|---|
| `Port 8080 was already in use` | Stop the other process or set `$env:SERVER_PORT="8081"` |
| `Unknown database 'avnzor'` | Create the database in MySQL |
| `Communications link failure` | Ensure MySQL is running |
| Flyway checksum mismatch | Run `./mvnw flyway:repair` |
| `Access denied` on `/orders` | User department must be Logistic |
| `401 Unauthorized` | Login again; include `Authorization: Bearer <token>` |
| Hibernate validation error | Entity mapping does not match the database table |

---

## Contributing

1. Create a feature branch from `master`.
2. Follow the existing package-by-feature structure.
3. Add Flyway migrations for schema changes (never use `ddl-auto: update`).
4. Keep secrets out of source control.
5. Open a pull request — CI must pass before merge.
6. Merging to `master` deploys automatically to staging.

### Code conventions

- Constructor injection only (`@RequiredArgsConstructor` + `final` fields).
- DTOs for all API request/response objects.
- Jakarta Validation on request DTOs.
- SLF4J for logging; never log passwords or JWTs.
- Keep methods short and focused.

---

## Roadmap

Planned capabilities:

- Multi-tenancy
- Expanded RBAC
- Orders, Returns, Tracking (full implementation)
- Audit logging
- Docker images
- OpenAPI documentation

---

## License

Internal Avnzor project. All rights reserved.
