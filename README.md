# Marathon Registration Portal — DevOps Pipeline

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Java Version](https://img.shields.io/badge/Java-21%20LTS-blue.svg)](https://bell-sw.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-Jenkins%20Declarative-orange.svg)](https://www.jenkins.io/)
[![Testing](https://img.shields.io/badge/Tests-Selenium%20WebDriver-green.svg)](https://www.selenium.dev/)
[![Container](https://img.shields.io/badge/Container-Docker-2496ED.svg)](https://www.docker.com/)
[![IaC](https://img.shields.io/badge/IaC-Ansible-EE0000.svg)](https://www.ansible.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)]()

> A robust, containerized, and automated web portal for marathon runner registrations, equipped with an end-to-end DevOps pipeline incorporating Git branching, Jenkins CI/CD, Selenium automated UI regression testing, Docker containerization, and idempotent Ansible server provisioning.

---

## 📌 Repository Information
- **Remote Repository URL:** [https://github.com/Anish-Nadar/devops-finalyr.git](https://github.com/Anish-Nadar/devops-finalyr.git)
- **Primary Branches:** `main` (Production Releases), `develop` (Integration Baseline)

---

## 🚀 Key Features

1. **Multi-Category Runner Registration:**
   - Full Marathon (42.195 km)
   - Half Marathon (21.0975 km)
   - 10K Challenge (10 km)
   - 5K Fun Run (5 km)
2. **Dual-Layer Validation Engine:**
   - Client-side HTML5/JavaScript immediate field feedback.
   - Server-side Spring Bean Validation enforcing category age limits (FM: 18+, HM: 16+, 10K: 14+, 5K: 10+), email uniqueness, and emergency contact formats.
3. **Atomic Unique ID & Bib Number Generator:**
   - Immediate generation of human-readable Registration IDs (e.g., `REG-FM-B4C2`).
   - Atomic allocation of unique race Bib numbers upon admin approval (e.g., `BIB-FM-1001`).
4. **Real-Time Registration Status Tracking:**
   - Instant search by Registration ID or Email address.
   - Visual status badges (`PENDING_APPROVAL`, `APPROVED`, `REJECTED`) and event details.
5. **Admin Management Dashboard:**
   - Live category quota utilization bars.
   - Filterable participant roster.
   - One-click approval and rejection workflow.
6. **DevOps Health Check Endpoint:**
   - `/health` endpoint returning system uptime, status, and memory metrics for container and load-balancer probes.

---

## 🏗 System Architecture

```mermaid
flowchart TD
    subgraph Client_Layer["Client Tier"]
        UI["Thymeleaf UI + Bootstrap 5 + Vanilla JS"]
        ST["Selenium WebDriver Automated Test Suite"]
    end

    subgraph App_Layer["Spring Boot Application (Port 8080)"]
        Ctrl["Web & API Controllers"]
        Val["Validation & Bib Services"]
        Repo["Concurrent In-Memory Data Store"]
        Act["Health & Metrics Actuator"]
    end

    subgraph DevOps_Layer["DevOps Pipeline"]
        Git["Git / GitHub Version Control"]
        Jen["Jenkins CI/CD Declarative Pipeline"]
        Doc["Docker Multi-Stage Container"]
        Ans["Ansible Idempotent Server Provisioning"]
    end

    UI --> Ctrl
    ST --> UI
    Ctrl --> Val
    Val --> Repo
    Ctrl --> Act
    Git --> Jen
    Jen --> ST
    Jen --> Doc
    Doc --> Ans
```

---

## 🌿 Git Branching & Contribution Policy

This repository adheres to a strict GitFlow branching standard:

| Branch Pattern | Source | Target | Purpose |
| :--- | :--- | :--- | :--- |
| `main` | Tagged Release | None | Production-ready stable release baseline. |
| `develop` | `main` | `main` | Integration branch for ongoing feature development. |
| `feature/<name>` | `develop` | `develop` | New functionality development (e.g., `feature/registration-form`). |
| `bugfix/<name>` | `develop` | `develop` | Defect fixes during testing iterations. |
| `hotfix/<name>` | `main` | `main`, `develop` | Urgent production fixes. |

### Commit Message Conventions
All commit messages must follow the Conventional Commits specification:
- `feat:` A new user-facing feature.
- `fix:` A bug fix.
- `docs:` Documentation only changes.
- `ci:` Changes to CI/CD configuration (Jenkinsfile, Docker, Ansible).
- `test:` Adding or updating tests (JUnit, Selenium).
- `refactor:` Code change that neither fixes a bug nor adds a feature.

---

## 🛠 Local Setup & Execution

### Prerequisites
- Java OpenJDK 21 LTS
- Maven 3.9+ (or use the included `mvnw.cmd` / `mvnw`)
- Docker Desktop 29+ (optional for container execution)

### 1. Clone the Repository
```bash
git clone https://github.com/Anish-Nadar/devops-finalyr.git
cd devops-finalyr
```

### 2. Build the Application
```bash
# On Windows
.\mvnw.cmd clean compile

# On Linux/macOS
./mvnw clean compile
```

### 3. Run the Application Locally
```bash
# On Windows
.\mvnw.cmd spring-boot:run

# On Linux/macOS
./mvnw spring-boot:run
```
The application will be live at: **`http://localhost:8080`**

---

## 🧪 Testing Suite

### Run Unit Tests
```bash
.\mvnw.cmd test
```

### Run Selenium Automated UI Tests
```bash
.\mvnw.cmd test -Dtest=RegistrationSeleniumTest
```

---

## 🐳 Docker Containerization

### Build Image
```bash
docker build -t marathon-portal:1.0.0 .
```

### Run Container
```bash
docker run -d -p 8080:8080 --name marathon-portal-app marathon-portal:1.0.0
```

---

## ⚙️ Configuration Management (Ansible)
Server provisioning, user creation, firewall rules, and container deployment are automated via Ansible:
```bash
ansible-playbook -i ansible/inventory.ini ansible/deploy.yml
```

---

## 📄 License
This project is licensed under the MIT License — see the LICENSE file for details.

