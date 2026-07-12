# Tools and Technologies

Brief reference — name and use case only.

---

## Core

| Tool | Use case |
|---|---|
| Java 21 | Runtime language |
| Spring Boot 4.1 | Application framework |
| Maven | Build and dependency management |
| Maven Wrapper (`mvnw`) | Reproducible builds without global Maven install |

---

## Data

| Tool | Use case |
|---|---|
| MySQL | Primary database (existing production schema) |
| Spring Data JPA | Repository layer and ORM access |
| Hibernate (`validate`) | Entity mapping validation only — never alters schema |
| Flyway | Versioned SQL schema migrations |
| HikariCP | Connection pooling (via Spring Boot) |

---

## Security

| Tool | Use case |
|---|---|
| Spring Security | Authentication and authorization framework |
| JWT (jjwt 0.12.6) | Stateless token-based auth |
| BCrypt | Password hashing for new/updated credentials |
| `@PreAuthorize` | Method-level RBAC on controllers |

---

## API

| Tool | Use case |
|---|---|
| Spring WebMVC | REST controllers |
| Jakarta Validation | Request DTO validation (`@NotBlank`, etc.) |
| springdoc-openapi 2.8.8 | Swagger UI and OpenAPI docs |
| `ApiResponse<T>` | Uniform API response envelope |
| `GlobalResponseHandler` | Auto-wraps successful responses |
| `GlobalExceptionHandler` | Centralized error responses |

---

## JSON

| Tool | Use case |
|---|---|
| Jackson 3 (`tools.jackson`) | Spring Boot 4 HTTP JSON serialization |
| Jackson 2 (`com.fasterxml.jackson`) | Security error JSON + jjwt; explicit `ObjectMapper` bean in `JacksonConfig` |

---

## Operations

| Tool | Use case |
|---|---|
| Spring Actuator | Health checks (`/actuator/health`) |
| Logback | Logging; file appenders on staging/prod |
| Docker | Multi-stage container builds |
| systemd | Linux service management on servers |
| GitHub Actions | CI (PR verify) and CD (staging/prod deploy) |

---

## Principles

| Principle | Use case |
|---|---|
| Package-by-feature | Modular monolith (`auth`, `orders`, `tracking`, `common`) |
| Controller → Service → Repository | Layered architecture; no repository access from controllers |
| DTOs only in API | Never expose JPA entities in responses |
| Environment variables for secrets | No hardcoded credentials; profile-based config |
| Immutable deploy artifact | Same JAR/Docker image for staging and production |
| Flyway owns schema | Hibernate validates only; all DDL via migrations |
