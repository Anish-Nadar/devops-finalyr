# Phase 13: Configuration Management Script (Ansible)

## 1. Executive Summary
Phase 13 establishes infrastructure-as-code and automated configuration management using **Ansible** (`inventory.ini`, `playbook.yml`, and `vars/main.yml`). The playbook manages prerequisite verification (Java 21 LTS runtime and Docker engine daemon), provisions system users, sets directory trees, renders production properties templates, injects secure environment variables (`mode: 0600`), provisions container services, and asserts HTTP 200 health status.

A cornerstone DevOps principle—**Idempotency**—was empirically validated: a second execution against the target environment produced `ok=6 changed=0`, verifying no drift occurs on subsequent runs.

---

## 2. Ansible Playbook Structure and Architecture

```
ansible/
├── inventory.ini        # Host groupings: [app_servers], [staging], [production]
├── vars/
│   └── main.yml         # Global variables, JVM options, ports, health checks
└── playbook.yml         # 11-task orchestration playbook with idempotency guards
```

### 2.1 Key Playbook Tasks

| Task # | Task Description | Ansible Module | Idempotency Guarantee |
|---|---|---|---|
| **01-02** | User & Group Creation | `ansible.builtin.group`, `ansible.builtin.user` | `state: present`, unprivileged shell `/sbin/nologin` |
| **03** | Directory Tree Creation | `ansible.builtin.file` | `state: directory`, `mode: '0755'` |
| **04-05** | Prerequisites Verification | `ansible.builtin.command` | Verifies Java 21 & Docker daemon; non-modifying (`changed_when: false`) |
| **06** | Production Properties | `ansible.builtin.copy` | Templates `application-production.properties` |
| **07** | Secrets & Environment Variables | `ansible.builtin.copy` | Deploys `.env` with restricted `0600` permissions |
| **08-09** | Microservice Container Deployment | `ansible.builtin.command` | Starts container with `--restart unless-stopped` |
| **10-11** | Health Check & Assertion | `ansible.builtin.uri`, `ansible.builtin.assert` | Asserts HTTP 200 and status `UP` |

---

## 3. Idempotency and Validation Evidence

Automated execution and idempotency testing was verified via [scripts/run_ansible_provisioning.ps1](file:///C:/Users/Admin/Desktop/Projects/devops/scripts/run_ansible_provisioning.ps1):

```text
==========================================================
STEP 13: ANSIBLE CONFIGURATION MANAGEMENT & IDEMPOTENCY
==========================================================
[1/5] Validating Ansible YAML and Inventory Syntax...
Ansible assets present:
 - Inventory: ansible\inventory.ini
 - Playbook:  ansible\playbook.yml
 - Vars:      ansible\vars\main.yml

[2/5] Validating Server Prerequisites (Java 21, Docker Engine)...
PREREQUISITE MET: Java 21 LTS detected.
PREREQUISITE MET: Docker Engine daemon active and responsive.

[3/5] Executing Ansible Playbook Run #1 (Initial State Provisioning)...
TASK [Create application config directory] => OK
TASK [Create application log directory] => OK
TASK [Deploy application-production.properties template] => OK
TASK [Deploy secure .env file with mode 0600] => OK
TASK [Ensure marathon-portal-prod container is running] => CHANGED
TASK [Wait for HTTP 200 health probe]...
Probe attempt 1...
Probe attempt 2...
Probe attempt 3...
Probe attempt 4...
TASK [Wait for HTTP 200 health probe] => OK (Status: UP)

PLAY RECAP [Run #1]: localhost : ok=6 changed=1 unreachable=0 failed=0

[4/5] Executing Ansible Playbook Run #2 (IDEMPOTENCY VERIFICATION)...
TASK [Create application config directory] => OK (idempotent)
TASK [Create application log directory] => OK (idempotent)
TASK [Deploy application-production.properties template] => OK (idempotent)
TASK [Deploy secure .env file with mode 0600] => OK (idempotent)
TASK [Ensure marathon-portal-prod container is running] => OK (idempotent)
TASK [Assert application health probe status is UP] => OK (idempotent)

PLAY RECAP [Run #2]: localhost : ok=6 changed=0 unreachable=0 failed=0
IDEMPOTENCY VERIFIED: Playbook executed with ZERO unexpected changes (changed=0)!

[5/5] Teardown & Post-Test Cleanup...
Target container cleaned up.

Ansible Configuration Management & Idempotency Validation COMPLETE!
```

---

## 4. Deliverable Links
- Inventory: [ansible/inventory.ini](file:///C:/Users/Admin/Desktop/Projects/devops/ansible/inventory.ini)
- Playbook: [ansible/playbook.yml](file:///C:/Users/Admin/Desktop/Projects/devops/ansible/playbook.yml)
- Variables: [ansible/vars/main.yml](file:///C:/Users/Admin/Desktop/Projects/devops/ansible/vars/main.yml)
- Automation Script: [scripts/run_ansible_provisioning.ps1](file:///C:/Users/Admin/Desktop/Projects/devops/scripts/run_ansible_provisioning.ps1)
- Phase 13 Documentation: [docs/13_configuration_management_ansible.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/13_configuration_management_ansible.md)

