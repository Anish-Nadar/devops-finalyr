# Phase 7: Jenkins Installation and Continuous Integration Job
## Marathon Registration Portal — DevOps Pipeline Project

---

### 1. Jenkins Environment & Instance Details
- **Jenkins Controller URL:** `http://localhost:8081`
- **Jenkins Version:** `2.579` running on Embedded Winstone / Jetty 12
- **Jenkins Home Directory:** `C:\ProgramData\Jenkins\.jenkins`
- **Runtime JDK:** BellSoft Liberica OpenJDK 21 LTS (`21.0.11+11-LTS`)
- **Git Version:** `2.50.0.windows.1`

---

### 2. Configured Jenkins CI Freestyle Job
- **Job Name:** `marathon-portal-ci`
- **Connected SCM Repository:** [https://github.com/Anish-Nadar/devops-finalyr.git](https://github.com/Anish-Nadar/devops-finalyr.git)
- **Target Branch:** `*/develop` (Integration Branch)
- **Trigger Strategy:** Scheduled SCM Polling (`H/5 * * * *` — checks every 5 minutes for new commits) and Webhook trigger.
- **Build Step:** Windows Batch Command:
  ```cmd
  echo [JENKINS CI] Starting Marathon Registration Portal Build
  java -version
  git --version
  echo [JENKINS CI] Executing Maven Build and Package...
  call .\mvnw.cmd clean compile package -DskipTests=false
  echo [JENKINS CI] Build and Packaging complete.
  dir target\*.jar
  ```
- **Post-Build Action (Artifact Archiver):**
  - Archived Artifacts: `target/*.jar`
  - Fingerprint: `Enabled`
  - Condition: `Archive only if successful`

---

### 3. Build #1 Execution Log
```text
Started by an SCM change
Running as SYSTEM
Building in workspace C:\ProgramData\Jenkins\.jenkins\workspace\marathon-portal-ci
The recommended git tool is: NONE
using credential github-pat
 > git.exe rev-parse --resolve-git-dir C:\ProgramData\Jenkins\.jenkins\workspace\marathon-portal-ci\.git # timeout=10
Fetching changes from the remote Git repository
 > git.exe config remote.origin.url https://github.com/Anish-Nadar/devops-finalyr.git # timeout=10
Fetching upstream changes from https://github.com/Anish-Nadar/devops-finalyr.git
 > git.exe --version # timeout=10
 > git --version # 'git version 2.50.0.windows.1'
 > git.exe fetch --tags --force --progress -- https://github.com/Anish-Nadar/devops-finalyr.git +refs/heads/*:refs/remotes/origin/* # timeout=10
Checking out Revision 23e4609 (origin/develop)
Commit message: "docs: add MVP completion report and git conflict resolution evidence"
[marathon-portal-ci] $ cmd /c call C:\WINDOWS\TEMP\jenkins-build-step.bat
========================================================
[JENKINS CI] Starting Marathon Registration Portal Build
========================================================
openjdk version "21.0.11" 2026-04-21 LTS
git version 2.50.0.windows.1
[JENKINS CI] Executing Maven Build and Package...
[INFO] Scanning for projects...
[INFO] Building Marathon Registration Portal 1.0.0-SNAPSHOT
[INFO] Compiling 12 source files with javac [debug release 21] to target\classes
[INFO] Compiling 1 source file with javac [debug release 21] to target\test-classes
[INFO] Running com.marathon.MarathonApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Building jar: target\marathon-portal-1.0.0-SNAPSHOT.jar
[INFO] Replacing main artifact with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 14.280 s
[JENKINS CI] Build and Packaging complete.
 Directory of C:\ProgramData\Jenkins\.jenkins\workspace\marathon-portal-ci\target
11/02/2026 09:21 PM 25,421,461 marathon-portal-1.0.0-SNAPSHOT.jar
Archiving artifacts
‘target/marathon-portal-1.0.0-SNAPSHOT.jar’ saved to archive
Recording fingerprints
Finished: SUCCESS
```

---

### 4. Archived Artifact Evidence
- **Archived Binary:** `target/marathon-portal-1.0.0-SNAPSHOT.jar`
- **File Size:** `25,421,461 bytes (25.4 MB)`
- **Archive Path:** `C:\ProgramData\Jenkins\.jenkins\jobs\marathon-portal-ci\builds\1\archive\target\marathon-portal-1.0.0-SNAPSHOT.jar`
- **Fingerprinted:** Yes (MD5/SHA256 tracking active)
- **Status:** **SUCCESS**

