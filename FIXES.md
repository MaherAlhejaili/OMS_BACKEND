# Fixes and Troubleshooting Log

Record of issues encountered during initial project setup and their resolutions.

---

## Build and configuration

| Issue | Cause | Fix |
|---|---|---|
| Java version mismatch | `pom.xml` had Java 17 | Set `java.version` to **21** |
| Profile file format | Used `application.yaml` | Renamed to `application.yml` per convention |
| Test DB missing tables | `sma_warehouse_users` exists in prod baseline, not in test DB | Added test-only migration `src/test/resources/db/test-migration/V3__Create_warehouse_users.sql`; enabled via `application-test.yml` flyway locations |

---

## Flyway

| Issue | Cause | Fix |
|---|---|---|
| Checksum mismatch on `V2__Create_audit_log.sql` | Git CRLF/LF line-ending changes on Windows after migration was applied | Run `.\mvnw.cmd flyway:repair`; added `.gitattributes` rules for `*.sql` and `*.yml` to enforce LF |
| Need CLI repair tool | No Flyway Maven plugin | Added `flyway-maven-plugin` to `pom.xml` |
| Schema version 3 newer than repo migration 2 | DB has a V3 history entry not present in `src/main/resources/db/migration/` | Harmless warning at startup; do not add conflicting V3 to main migrations without coordinating DB state |
| Adopting Flyway on existing DB | Tables exist but no `flyway_schema_history` | Run `.\mvnw.cmd flyway:baseline` once, then `flyway:migrate`; `baseline-on-migrate` is disabled after adoption |
| `Schema validation: missing table [audit_log]` | `flyway_schema_history` is at version 3 but `audit_log` was never created (V2 skipped) | Added `V4__Ensure_audit_log.sql` with `CREATE TABLE IF NOT EXISTS`; restart app or run `.\mvnw.cmd flyway:migrate` |
| `wrong column type ... details ... longtext ... expecting json` | MySQL 5.5 stores `JSON` columns as `LONGTEXT`; Hibernate `SqlTypes.JSON` validation fails | `AuditLog.details` uses `JsonMapConverter` + `LONGVARCHAR`; `V5__Audit_log_details_longtext.sql` normalizes the column |
| `Detected applied migration not resolved locally: 3` | Test-only `V3__Create_warehouse_users.sql` was applied to the dev `avnzor` DB | Added `spring.flyway.ignore-migration-patterns: "*:missing"` in `application.yml` |

---

## Startup failures

| Issue | Cause | Fix |
|---|---|---|
| `Unknown database 'avnzor_test'` | Test database not created | `CREATE DATABASE avnzor_test;` |
| `Port 8080 was already in use` | Previous Spring Boot process still running | `Get-NetTCPConnection -LocalPort 8080` then `Stop-Process -Id <PID> -Force` |
| `ObjectMapper` bean not found | Spring Boot 4 uses Jackson 3 (`tools.jackson`); no auto-configured `com.fasterxml.jackson.databind.ObjectMapper` bean | Added `spring-boot-starter-jackson` + `JacksonConfig` with explicit `ObjectMapper` bean |
| Logback warning: `Appender named [FILE] not referenced` | FILE appender defined globally but only used in staging/prod profiles | Moved FILE appender definition inside `<springProfile name="staging,prod">` in `logback-spring.xml` |

---

## Docker

| Issue | Cause | Fix |
|---|---|---|
| `docker_engine` pipe not found | Docker Desktop not running on Windows | Start Docker Desktop before `docker build` / `docker run` |
| `--env-file /opt/avnzor/config/application.env` fails locally | That path is for Linux servers | Use `-e` flags or a local `.env.docker` file with `host.docker.internal` as `DATABASE_HOST` |

---

## CI/CD

| Issue | Cause | Fix |
|---|---|---|
| Production and staging appeared parallel in GitHub UI | Both deploy jobs had `needs: build` | Changed production to `needs: deploy-staging` so pipeline is Build → Staging → Production |
| Staging deploy fails in GitHub Actions | GitHub Environment secrets not configured yet | Expected until servers are ready; secrets documented in `deploy/README.md` and `CONTEXT.md` |

---

## Security / API

| Issue | Cause | Fix |
|---|---|---|
| 403 on `/orders` for non-Logistic users | RBAC by department | Expected behaviour; department must be `Logistic`, `Logistics`, or `Logistic team` |
| Legacy plain-text passwords | Old `sma_warehouse_users` records may not be BCrypt | `AuthService` supports both BCrypt (`$2a$`/`$2b$`/`$2y$`) and plain-text comparison |
| Security 401/403 JSON format inconsistent | `SecurityProblemSupport` used hand-written JSON | Updated to serialize `ApiResponse` via `ObjectMapper` |

---

## Commands reference

```powershell
# Repair Flyway checksums
.\mvnw.cmd flyway:repair

# Kill process on port 8080 (Windows)
Get-NetTCPConnection -LocalPort 8080 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }

# Start application
.\mvnw.cmd spring-boot:run
```
