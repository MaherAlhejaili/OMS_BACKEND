# Testing Strategy

This project uses **JUnit 5**, **Mockito** (via Spring Boot Test), **AssertJ**, **MockMvc**, **Testcontainers (MySQL)**, and **JaCoCo**.

> **Note:** The application runs on **Spring Boot 4.1** (not 3.x). Test dependencies are aligned with the Spring Boot BOM.

---

## Test types and when to use them

| Type | Naming | Maven phase | When to use |
|---|---|---|---|
| **Unit** | `*Test.java` | Surefire (`test`) | Pure logic: services, utilities, security rules, JWT parsing. No Spring context. Fast feedback for TDD. |
| **Integration** | `*IT.java` | Failsafe (`verify`) | Full Spring context, real MySQL via Testcontainers, multi-tenant routing, Flyway, repositories. |
| **API / Controller** | `*IT.java` + MockMvc | Failsafe | HTTP contract, validation, response shape, status codes. Extend `AbstractMockMvcIntegrationTest`. |
| **Security** | `*IT.java` or `*Test.java` | Either | RBAC rules unit-tested (`DepartmentAccessTest`); endpoint security integration-tested with JWT (`OrderControllerIT`). |
| **Repository** | `*IT.java` | Failsafe | Real SQL against tenant DB when repository logic grows beyond simple CRUD. |
| **Multi-tenant** | `*IT.java` | Failsafe | Always use `AbstractIntegrationTest` — seeds platform + tenant DBs in isolated Testcontainers MySQL. |

### Prefer real integration tests over excessive mocking

- **Mock** external systems (shipping APIs, webhooks), not your own database or security stack.
- **Do not mock** JPA repositories in integration tests — use Testcontainers.
- **Do mock** in unit tests when dependencies are slow or unrelated to the behavior under test.

---

## Project layout

```
src/test/java/com/avnzor/oms_backend/
├── support/                          # Shared base classes and utilities
│   ├── AbstractIntegrationTest       # Testcontainers + Spring Boot context
│   ├── AbstractMockMvcIntegrationTest
│   ├── TestMultiTenancyConfig        # Seeds test tenant + users
│   ├── TestUserFactory
│   ├── JwtTestSupport
│   └── TestPropertyRegistrySupport
├── auth/
│   ├── service/JwtServiceTest        # Unit
│   ├── security/DepartmentAccessTest # Unit
│   └── controller/AuthControllerIT   # API integration
├── audit/support/AuditPathResolverTest
├── orders/controller/OrderControllerIT
├── tracking/controller/TrackingControllerIT
└── OmsBackendApplicationIT
```

Mirror the main package-by-feature structure when adding new modules (`returns/`, `tenants/`, etc.).

---

## Running tests

```powershell
# Unit tests only (fast)
.\mvnw.cmd test

# Full verification: unit + integration + JaCoCo report
.\mvnw.cmd clean verify

# View coverage report
start target\site\jacoco\index.html
```

**Requirements for integration tests:**
- Docker Desktop running (Testcontainers starts `mysql:8.4` automatically)
- Integration tests are **skipped** if Docker is unavailable (`disabledWithoutDocker = true`)

---

## Testcontainers

Integration tests use an isolated MySQL container — never your local `oms` or `tenant_1` databases.

| Database | Purpose |
|---|---|
| `oms_test` | Platform schema (tenants registry) |
| `tenant_test` | Tenant business schema (users, audit_log) |

Configuration:
- `AbstractIntegrationTest` — shared static MySQL container
- `application-test.yml` — test profile properties
- `TestMultiTenancyConfig` — seeds tenant registry via `TenantBootstrapContributor`

---

## MockMvc and JWT testing

Extend `AbstractMockMvcIntegrationTest` for API tests:

```java
String token = JwtTestSupport.bearerToken(jwtService, TestUserFactory.logisticPrincipal());

mockMvc.perform(get("/api/v1/orders").header("Authorization", token))
    .andExpect(status().isOk());
```

`TestUserFactory` provides logistic and warehouse users bound to `test-tenant`.

---

## TDD workflow

1. **Write a failing test** — unit test for business logic, or `*IT` for HTTP/security behavior.
2. **Implement the minimum code** to pass.
3. **Refactor** while keeping tests green.
4. **Repeat.**

### TDD recommendations for Spring Boot

| Layer | Start with |
|---|---|
| Service business rules | Unit test (`*Test`) — no `@SpringBootTest` |
| REST endpoints | `*IT` with MockMvc — define expected JSON contract first |
| Security / RBAC | Unit test for rule classes; `*IT` for endpoint enforcement |
| Repository queries | `*IT` with Testcontainers when queries become non-trivial |
| Multi-tenant behavior | Always `*IT` extending `AbstractIntegrationTest` |

Avoid `@SpringBootTest` for simple unit tests — it slows the TDD loop.

---

## JaCoCo coverage goals

Reports are generated at `target/site/jacoco/` during `mvn verify`.

| Layer | Recommended minimum | Notes |
|---|---|---|
| **Services** | 70–80% | Focus on business branches and error paths |
| **Controllers** | 60–70% | Cover success, validation, auth, and forbidden paths |
| **Repositories** | 50–60% | Test custom queries; skip boilerplate CRUD |
| **Security** | 80%+ | RBAC and JWT paths are high-risk |
| **Utilities** | 80%+ | Small, pure classes should be fully covered |

Do **not** target 100% coverage. Prioritize meaningful tests over line counts.

---

## CI pipeline

Every pull request to `master` runs:

1. `mvn clean verify`
2. Unit tests (Surefire)
3. Integration tests (Failsafe)
4. JaCoCo merged report
5. Build fails if any test fails

JaCoCo HTML report is uploaded as a CI artifact.

---

## Adding tests for new modules

1. Create `feature/service/FeatureServiceTest.java` for unit tests.
2. Create `feature/controller/FeatureControllerIT.java` extending `AbstractMockMvcIntegrationTest`.
3. For tenant-scoped data, use `TestUserFactory` and `test-tenant` — data goes to `tenant_test` only.
4. Run `.\mvnw.cmd test` frequently during development; run `.\mvnw.cmd verify` before opening a PR.
