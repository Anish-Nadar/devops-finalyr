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

## 🐳 Docker Containerization & Lifecycle

### Build Production Image
`ash
docker build -t marathon-portal:1.0.0 -t marathon-portal:latest .
`

### Run Containerized Application
`ash
docker run -d -p 8080:8080 --name marathon-app marathon-portal:1.0.0
`

### Automated Lifecycle Validation Script
`ash
powershell -ExecutionPolicy Bypass -File scripts\test_container_lifecycle.ps1
`

---

## ⚙️ Configuration Management & Automated Provisioning (Ansible)
Server provisioning, prerequisite verification, directory hierarchy, secure secrets deployment, containerization, and idempotency (changed=0) are managed via Ansible:

`ash
# Execute Ansible Playbook natively
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml

# Run Automated Provisioning & Idempotency Validation Script
powershell -ExecutionPolicy Bypass -File scripts\run_ansible_provisioning.ps1

# Run Phase 14 Reliability & Automated Rollback Validation Script
powershell -ExecutionPolicy Bypass -File scripts\test_automated_provisioning_and_rollback.ps1
`

---

## 📚 Complete DevOps Documentation Index (Phases 1 - 14)
- **Master Manual:** [docs/DEVOPS_MASTER_PORTAL_MANUAL.md](docs/DEVOPS_MASTER_PORTAL_MANUAL.md)
- **Phase 01:** [Problem Definition and Scope](docs/01_problem_definition_and_scope.md)
- **Phase 02:** [Agile Planning and DevOps Workflow](docs/02_agile_planning_and_devops_workflow.md)
- **Phase 03:** [Requirements, Architecture and Technology Setup](docs/03_requirements_architecture_and_technology_setup.md)
- **Phase 04:** [Git and GitHub Repository Initialization](docs/04_git_and_github_repository_initialization.md)
- **Phase 05:** [Feature Development with Branching](docs/05_feature_development_with_branching.md)
- **Phase 06:** [MVP Completion and Git Collaboration](docs/06_mvp_completion_and_git_collaboration.md)
- **Phase 07:** [Jenkins Installation and CI Job](docs/07_jenkins_installation_and_ci_job.md)
- **Phase 08:** [Pipeline-as-Code and Server Deployment](docs/08_pipeline_as_code_and_server_deployment.md)
- **Phase 09:** [Selenium Test Design and Local Execution](docs/09_selenium_test_design_and_execution.md)
- **Phase 10:** [Continuous Testing in Jenkins](docs/10_continuous_testing_in_jenkins.md)
- **Phase 11:** [Docker Image and Container Lifecycle](docs/11_docker_image_and_container_lifecycle.md)
- **Phase 12:** [Jenkins-Docker Continuous Deployment](docs/12_jenkins_docker_continuous_deployment.md)
- **Phase 13:** [Configuration Management Script (Ansible)](docs/13_configuration_management_ansible.md)
- **Phase 14:** [Automated Provisioning and Reliability Validation](docs/14_automated_provisioning_and_reliability_validation.md)

---

## 📄 License
This project is licensed under the MIT License — see the LICENSE file for details.


