# syntax=docker/dockerfile:1

# =============================================================================
# Stage 1 — Build
# Maven and source compilation stay in this stage only.
# =============================================================================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy the POM first so dependency downloads are cached in a separate layer.
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# Copy sources and build the executable JAR (tests run in CI, not in image build).
COPY src ./src
RUN mvn -B -ntp package -DskipTests

# =============================================================================
# Stage 2 — Extract layers
# Splits the fat JAR into layers so dependency layers cache across app releases.
# =============================================================================
FROM eclipse-temurin:21-jre-jammy AS layers

WORKDIR /extract

COPY --from=builder /build/target/OMS_BACKEND-*.jar app.jar

# Spring Boot 3.2+ layered JAR layout (stable dependencies vs. application code).
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# =============================================================================
# Stage 3 — Runtime
# Minimal JRE-only image: no Maven, no source, no build tools.
# =============================================================================
FROM eclipse-temurin:21-jre-jammy AS runtime

# curl is used only for the HEALTHCHECK; apt metadata is removed to limit image size.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Run as a dedicated non-root user (never run Spring Boot as root in production).
RUN groupadd --system avnzor \
    && useradd --system --gid avnzor --no-create-home --home-dir /app avnzor

WORKDIR /app

# Copy layers from least-frequently-changed to most-frequently-changed.
COPY --from=layers --chown=avnzor:avnzor /extract/extracted/dependencies/ ./
COPY --from=layers --chown=avnzor:avnzor /extract/extracted/spring-boot-loader/ ./
COPY --from=layers --chown=avnzor:avnzor /extract/extracted/snapshot-dependencies/ ./
COPY --from=layers --chown=avnzor:avnzor /extract/extracted/application/ ./

USER avnzor

# Default profile; override per environment (staging, prod) at container start.
# All secrets and DB settings must be injected via environment variables — never baked in.
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

EXPOSE 8080

# Actuator health endpoint; start-period allows Flyway + JPA validate to finish on boot.
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD curl -fsS "http://localhost:${SERVER_PORT:-8080}/actuator/health" || exit 1

# Layered JAR launcher; JAVA_OPTS supports heap tuning without rebuilding the image.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
