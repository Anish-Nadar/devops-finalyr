# Phase 11: Docker Image and Container Lifecycle

## 1. Executive Summary
Phase 11 containerizes the Marathon Registration Portal microservice into an optimized, production-hardened Docker image using a multi-stage build on **Eclipse Temurin OpenJDK 21 (Alpine Linux)**. The image adheres to modern container security and performance best practices: non-root unprivileged execution (`appuser:appgroup`), JVM container ergonomics (`-XX:+UseG1GC -XX:MaxRAMPercentage=75.0`), native Docker health checking, and minimal footprint (**96.9 MB** content size).

---

## 2. Multi-Stage Dockerfile Architecture

```mermaid
flowchart TD
    subgraph Stage1["Stage 1: Build & Assembly (eclipse-temurin:21-jre-alpine)"]
        S1_1["Copy pom.xml & target/*.jar"]
        S1_2["Validate SHA/Jar Integrity & JRE Runtime"]
    end

    subgraph Stage2["Stage 2: Production Hardened Runtime (eclipse-temurin:21-jre-alpine)"]
        S2_1["Create unprivileged group 'appgroup' & user 'appuser'"]
        S2_2["Copy artifact from Stage 1 (--chown=appuser:appgroup)"]
        S2_3["Set environment: SERVER_PORT=8080, SPRING_PROFILES_ACTIVE=prod"]
        S2_4["Configure Docker HEALTHCHECK via wget /health"]
        S2_5["ENTRYPOINT with 'exec java' for SIGTERM signal handling"]
    end

    Stage1 &rarr; Stage2
    Stage2 &rarr; Final["marathon-portal:1.0.0 (96.9 MB Content Size)"]
```

### 2.1 Optimization Highlights
- **Base OS:** Alpine Linux 3.20 minimal distribution.
- **Runtime:** Eclipse Temurin 21.0.13 JRE headless.
- **Security:** Dropped root permissions; executed under system user `appuser` (UID 100).
- **Process Signal Forwarding:** Configured `exec java` to ensure PID 1 gracefully traps Docker `SIGTERM` signals for zero-downtime stops.
- **Integrated Health Check:** Native Docker health check (`wget --quiet --spider http://localhost:8080/health`).

---

## 3. Image Build and Versioning Scheme

### 3.1 Build Command
```bash
docker build -t marathon-portal:1.0.0 -t marathon-portal:latest .
```

### 3.2 Built Image Inventory
```text
IMAGE                    ID             DISK USAGE   CONTENT SIZE   EXTRA
marathon-portal:1.0.0    b3bf6183117f        335MB         96.9MB        
marathon-portal:latest   b3bf6183117f        335MB         96.9MB        
```

---

## 4. Container Lifecycle Execution Evidence

Automated lifecycle validation was executed via [scripts/test_container_lifecycle.ps1](file:///C:/Users/Admin/Desktop/Projects/devops/scripts/test_container_lifecycle.ps1):

```text
==========================================================
STEP 11: DOCKER CONTAINER LIFECYCLE VALIDATION
==========================================================
[1/7] Cleaning up previous containers...
[2/7] Running container marathon-portal:1.0.0 on port 8080...
Container started with ID: 0fcd0d08d33c1672557d400fc38ccd95f81e6e8cc1410441b41fc4b535ef449e
[3/7] Waiting for Spring Boot application to become ready...
Attempt 1 - Waiting for server port 8080...
Attempt 2 - Waiting for server port 8080...
Attempt 3 - Waiting for server port 8080...
Attempt 4 - Waiting for server port 8080...
Application is READY after 10 seconds.

[4/7] Inspecting running container status and logs...
CONTAINER ID   NAMES          STATUS                    PORTS
0fcd0d08d33c   marathon-app   Up 11 seconds (healthy)   0.0.0.0:8080->8080/tcp, [::]:8080->8080/tcp

--- Tail Container Logs ---
2026-11-02T16:51:10.899Z  INFO 1 --- [marathon-portal] [           main] com.marathon.MarathonApplication         : Starting MarathonApplication v1.0.0-SNAPSHOT using Java 21.0.13 with PID 1 (/app/app.jar started by appuser in /app)
2026-11-02T16:51:10.906Z  INFO 1 --- [marathon-portal] [           main] com.marathon.MarathonApplication         : The following 1 profile is active: "prod"
2026-11-02T16:51:15.017Z  INFO 1 --- [marathon-portal] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2026-11-02T16:51:17.255Z  INFO 1 --- [marathon-portal] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path ''
2026-11-02T16:51:17.293Z  INFO 1 --- [marathon-portal] [           main] com.marathon.MarathonApplication         : Started MarathonApplication in 7.644 seconds (process running for 9.05)

[5/7] Testing HTTP Endpoints against containerized application...
Health check response: {"totalRegistrations":3,"service":"marathon-registration-portal","memoryTotalBytes":67108864,"startTime":"2026-11-02T16:51:15.571795289","version":"1.0.0-mvp","systemTimestamp":"2026-11-02T16:51:19.466247432","status":"UP","memoryFreeBytes":32862384}
Landing page status code: 200 OK

[6/7] Testing Container Stop and Restart lifecycle...
Stopping container...
Status after stop: Exited (143) Less than a second ago
Restarting container...
Status after restart: Up 6 seconds (health: starting)

[7/7] Cleaning up container...
Container removed successfully.

Docker container lifecycle test COMPLETED SUCCESSFULLY!
```

---

## 5. Artifact Links
- Multi-Stage Definition: [Dockerfile](file:///C:/Users/Admin/Desktop/Projects/devops/Dockerfile)
- Docker Ignore Rules: [.dockerignore](file:///C:/Users/Admin/Desktop/Projects/devops/.dockerignore)
- Automated Lifecycle Test Script: [scripts/test_container_lifecycle.ps1](file:///C:/Users/Admin/Desktop/Projects/devops/scripts/test_container_lifecycle.ps1)
- Phase 11 Documentation: [docs/11_docker_image_and_container_lifecycle.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/11_docker_image_and_container_lifecycle.md)

