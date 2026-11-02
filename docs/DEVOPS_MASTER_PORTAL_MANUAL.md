# DevOps Master Pipeline & Operations Manual
## Marathon Registration Portal — End-to-End Enterprise DevOps Engineering

---

## 1. Executive Summary & Project Overview
This manual provides the comprehensive architectural blueprint, operational lifecycle, testing evidence, and pipeline-as-code definitions for the **Marathon Registration Portal DevOps Engineering Project**. Developed strictly according to the 14-step project plan, this platform demonstrates enterprise-grade DevOps practices: Agile planning, Git branching and conflict resolution, Jenkins CI/CD automation, Selenium E2E headless quality gating, containerization with Docker, configuration management with Ansible, and automated self-healing rollback.

---

## 2. 14-Step DevOps Roadmap Matrix

| Phase | Milestone | Key Deliverables | Verification Status |
|:---:|---|---|:---:|
| **01** | **Problem Definition & Scope** | Problem statement, 15-task scope, RACI matrix, KPIs, Personas | **COMPLETED & PUSHED** |
| **02** | **Agile Planning & Workflow** | 7 User stories (Gherkin AC), MoSCoW Product Backlog, 3 Sprints, DoD | **COMPLETED & PUSHED** |
| **03** | **Requirements & Architecture** | Java 21 LTS, Spring Boot 3.2.5, Tomcat 10, Maven skeleton, Ports | **COMPLETED & PUSHED** |
| **04** | **Git & GitHub Repo Init** | Remote GitHub sync, .gitignore, Branching policy (`main`, `develop`) | **COMPLETED & PUSHED** |
| **05** | **Feature 1 Development** | `feature/registration-core`, models, services, UI views, PR peer review | **COMPLETED & PUSHED** |
| **06** | **MVP & Git Collaboration** | Admin approval, Bib generator, simulated merge conflict & resolution, `v1.0.0-mvp` | **COMPLETED & PUSHED** |
| **07** | **Jenkins Installation & CI** | Jenkins freestyle job, SCM poll (`H/5`), Maven compile, JAR archiving | **COMPLETED & PUSHED** |
| **08** | **Pipeline-as-Code & Server Deploy** | Declarative `Jenkinsfile` (5 stages), dynamic deployment on Tomcat 10 | **COMPLETED & PUSHED** |
| **09** | **Selenium Test Design & Execution** | 4 E2E user journeys, headless Chrome, explicit waits, screenshots | **COMPLETED & PUSHED** |
| **10** | **Continuous Testing in Jenkins** | Dual test gates, JUnit XML reporting, deliberate failure gate verification | **COMPLETED & PUSHED** |
| **11** | **Docker Lifecycle Management** | Multi-stage `Dockerfile` (96.9 MB), unprivileged user, health check | **COMPLETED & PUSHED** |
| **12** | **Jenkins-Docker Continuous Deploy** | 8-stage CD pipeline, environment parameterization, automated rollback | **COMPLETED & PUSHED** |
| **13** | **Configuration Management (Ansible)** | `inventory.ini`, `playbook.yml`, secure `.env`, idempotency (`changed=0`) | **COMPLETED & PUSHED** |
| **14** | **Automated Provisioning & Rollback** | End-to-end server provisioning, multi-pass idempotency, automated self-healing | **COMPLETED & PUSHED** |

---

## 3. End-to-End DevOps Pipeline Architecture

```mermaid
flowchart TD
    subgraph DeveloperWorkstation["Developer & Version Control"]
        Dev["Developer Commit"] &rarr; GitBranch{"Branch: feature/*"}
        GitBranch &rarr; PR["PR Review &amp; Non-FF Merge"]
        PR &rarr; DevBranch["develop branch"]
        DevBranch &rarr; GitPush["Push to GitHub<br/>(Anish-Nadar/devops-finalyr)"]
    end

    subgraph JenkinsServer["Jenkins CI/CD Automation (Pipeline-as-Code)"]
        GitPush &rarr; SCM["Stage 1: SCM Checkout"]
        SCM &rarr; Build["Stage 2: Compile &amp; Static Check"]
        Build &rarr; Test1["Stage 3: Unit &amp; MockMvc Tests (JUnit XML)"]
        Test1 &rarr; Gate1{"Tests Pass?"}
        Gate1 -- Yes --> Test2["Stage 4: Headless Chrome Selenium E2E"]
        Gate1 -- No --> Abort["Quality Gate Tripped &rarr; Abort"]
        Test2 &rarr; Gate2{"E2E Pass?"}
        Gate2 -- Yes --> Package["Stage 5: Package Fat JAR (Fingerprint)"]
        Gate2 -- No --> Abort
        Package &rarr; DockerBuild["Stage 6: Multi-Stage Docker Build (:build-N)"]
        DockerBuild &rarr; DockerRun["Stage 7: Zero-Downtime Container Deploy"]
        DockerRun &rarr; HealthProbe{"Stage 8: Post-Deploy Health Check"}
        HealthProbe -- 200 OK --> Live["RELEASE PROMOTED<br/>(HTTP 200 UP)"]
        HealthProbe -- Failure --> Rollback["AUTOMATED ROLLBACK<br/>(Deploy :latest)"]
    end

    subgraph ConfigManagement["Infrastructure-as-Code (Ansible)"]
        AnsiblePlaybook["Ansible Playbook (11 Tasks)"]
        AnsiblePlaybook &rarr; ServerPrep["Prerequisites (Java 21, Docker)"]
        ServerPrep &rarr; ConfigFiles["Template configs &amp; Deploy secrets (0600)"]
        ConfigFiles &rarr; Idempotent["Multi-Pass Verification (changed=0)"]
    end
```

---

## 4. Verification Evidence & Quick Reference Commands

### 4.1 Running Tests Locally
```powershell
# Run Unit and MockMvc test suite
.\mvnw.cmd test -Dtest=MarathonApplicationTests,RegistrationControllerTest

# Run Headless Chrome Selenium E2E Browser Suite
.\mvnw.cmd test -Dtest=RegistrationSeleniumTest

# Run Complete Test Suite
.\mvnw.cmd test
```

### 4.2 Building & Running Container Image
```powershell
# Build multi-stage Docker image
docker build -t marathon-portal:1.0.0 -t marathon-portal:latest .

# Run container with health check
docker run -d --name marathon-app -p 8080:8080 marathon-portal:1.0.0

# Verify container health
Invoke-RestMethod -Uri "http://localhost:8080/health"
```

### 4.3 Automated Provisioning & Rollback Verification
```powershell
# Execute Phase 14 automated provisioning, multi-pass idempotency, and rollback test
powershell -ExecutionPolicy Bypass -File scripts\test_automated_provisioning_and_rollback.ps1
```

---

## 5. Repository Documentation Index
- [docs/01_problem_definition_and_scope.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/01_problem_definition_and_scope.md)
- [docs/02_agile_planning_and_devops_workflow.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/02_agile_planning_and_devops_workflow.md)
- [docs/03_requirements_architecture_and_technology_setup.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/03_requirements_architecture_and_technology_setup.md)
- [docs/04_git_and_github_repository_initialization.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/04_git_and_github_repository_initialization.md)
- [docs/05_feature_development_with_branching.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/05_feature_development_with_branching.md)
- [docs/06_mvp_completion_and_git_collaboration.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/06_mvp_completion_and_git_collaboration.md)
- [docs/07_jenkins_installation_and_ci_job.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/07_jenkins_installation_and_ci_job.md)
- [docs/08_pipeline_as_code_and_server_deployment.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/08_pipeline_as_code_and_server_deployment.md)
- [docs/09_selenium_test_design_and_execution.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/09_selenium_test_design_and_execution.md)
- [docs/10_continuous_testing_in_jenkins.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/10_continuous_testing_in_jenkins.md)
- [docs/11_docker_image_and_container_lifecycle.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/11_docker_image_and_container_lifecycle.md)
- [docs/12_jenkins_docker_continuous_deployment.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/12_jenkins_docker_continuous_deployment.md)
- [docs/13_configuration_management_ansible.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/13_configuration_management_ansible.md)
- [docs/14_automated_provisioning_and_reliability_validation.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/14_automated_provisioning_and_reliability_validation.md)
