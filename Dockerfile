# ==============================================================================
# Multi-Stage Dockerfile for Marathon Registration Portal
# Base: Eclipse Temurin OpenJDK 21 (Alpine Linux - JRE Minimal)
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build & Artifact Assembly
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS builder

WORKDIR /workspace

# Copy build descriptor and application package
COPY pom.xml .
COPY target/marathon-portal-1.0.0-SNAPSHOT.jar app.jar

# Validate jar presence and integrity
RUN test -f /workspace/app.jar && java -version

# ------------------------------------------------------------------------------
# Stage 2: Hardened Minimal JRE Runtime
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="DevOps Team <devops@marathon-portal.com>" \
      application="marathon-registration-portal" \
      version="1.0.0" \
      description="Production container image for Marathon Registration Portal"

WORKDIR /app

# Security Hardening: Run as unprivileged user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled artifact from builder stage
COPY --from=builder --chown=appuser:appgroup /workspace/app.jar app.jar

# Switch to non-root user
USER appuser:appgroup

# Expose Spring Boot web port
EXPOSE 8080

# Environment variables
ENV SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Native Docker Container Health Check
HEALTHCHECK --interval=10s --timeout=3s --start-period=15s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
