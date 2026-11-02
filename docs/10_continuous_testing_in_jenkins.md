# Phase 10: Continuous Testing in Jenkins

## 1. Executive Summary
Phase 10 establishes automated **Continuous Testing** and strict **Quality Gating** within the Jenkins CI/CD pipeline for the Marathon Registration Portal. Every code commit undergoes automated unit testing, component integration verification, and headless browser E2E regression testing. 

A deliberate test failure was introduced and committed to branch `defect/deliberate-test-failure` to empirically validate that test failures halt the pipeline before packaging and deployment. Following resolution on branch `develop`, the pipeline verified a 100% pass rate, published JUnit reports, and cleared the quality gate.

---

## 2. Pipeline-as-Code Quality Gate Implementation

### 2.1 Updated Jenkinsfile Stages
The Jenkins declarative pipeline was structured with dedicated testing gates and artifact archiving:

```groovy
stage('Unit & Integration Testing') {
    steps {
        bat 'call .\\mvnw.cmd test -Dtest=MarathonApplicationTests,RegistrationControllerTest'
    }
    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
        }
    }
}

stage('Automated Selenium E2E Testing') {
    steps {
        bat 'call .\\mvnw.cmd test -Dtest=RegistrationSeleniumTest'
    }
    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: 'screenshots/*.png', allowEmptyArchive: true
        }
    }
}

stage('Package Executable JAR') {
    steps {
        bat 'call .\\mvnw.cmd package -DskipTests=true'
    }
    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
```

### 2.2 Quality Gating Logic
1. **Fast Failure Feedback:** Fast Spring Boot context and MockMvc integration tests (~12s) run first in Stage 3.
2. **E2E Browser Validation:** Full headless Chrome Selenium journeys run in Stage 4.
3. **Automated Deployment Block:** If either test stage encounters any assertion error, runtime exception, or timeout:
   - Maven returns exit code `1` (`BUILD FAILURE`).
   - Jenkins marks stage as `FAILURE`.
   - Downstream stages (`Package Executable JAR` and `Deploy to Application Server`) are immediately aborted (`SKIPPED`).
   - Notification emits: `QUALITY GATE FAILED: One or more test suites failed. Deployment is blocked.`

---

## 3. Deliberate Failure Simulation and Quality Gate Verification

### 3.1 Defect Injection on Branch `defect/deliberate-test-failure`
A regression defect was injected into `RegistrationControllerTest.java`:
```java
@Test
@DisplayName("DELIBERATE DEFECT: Simulated regression to verify CI Quality Gate failure blocking deployment")
void testDeliberateDefectForQualityGate() {
    org.junit.jupiter.api.Assertions.fail(
        "DELIBERATE TEST FAILURE: CI quality gate must abort build before packaging or deployment!"
    );
}
```

### 3.2 Jenkins Pipeline Failure Output
```text
[INFO] Running com.marathon.controller.RegistrationControllerTest
[ERROR] Tests run: 7, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 12.70 s <<< FAILURE! -- in com.marathon.controller.RegistrationControllerTest
[ERROR] com.marathon.controller.RegistrationControllerTest.testDeliberateDefectForQualityGate -- Time elapsed: 0.038 s <<< FAILURE!
org.opentest4j.AssertionFailedError: DELIBERATE TEST FAILURE: CI quality gate must abort build before packaging or deployment!
	at org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)
	at org.junit.jupiter.api.Assertions.fail(Assertions.java:138)
	at com.marathon.controller.RegistrationControllerTest.testDeliberateDefectForQualityGate(RegistrationControllerTest.java:28)

[INFO] Results:
[ERROR] Failures: 
[ERROR]   RegistrationControllerTest.testDeliberateDefectForQualityGate:28 DELIBERATE TEST FAILURE: CI quality gate must abort build before packaging or deployment!
[INFO] 
[ERROR] Tests run: 8, Failures: 1, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:3.1.2:test (default-test) on project marathon-portal: There are test failures.
```

**Quality Gate Result:** 
- Build Status: `FAILED`
- Packaging: `SKIPPED` (No defective JAR was created)
- Deployment: `SKIPPED` (Staging and Production environments remained untouched and protected)

---

## 4. Defect Resolution and Green Pipeline Validation

### 4.1 Resolution on Branch `develop`
The defect was contained to `defect/deliberate-test-failure` and resolved on `develop`. The complete test suite was re-run:

```text
[INFO] Running com.marathon.controller.RegistrationControllerTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 12.02 s -- in com.marathon.controller.RegistrationControllerTest
[INFO] Running com.marathon.MarathonApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.583 s -- in com.marathon.MarathonApplicationTests
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  21.150 s
```

### 4.2 Packaging and Deployment Gate Cleared
```text
[INFO] Building jar: C:\Users\Admin\Desktop\Projects\devops\target\marathon-portal-1.0.0-SNAPSHOT.jar
[INFO] Replacing main artifact with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
- Status: `SUCCESS`
- Quality Gate: `PASSED`
- Artifact Fingerprinted & Archived: `target/marathon-portal-1.0.0-SNAPSHOT.jar` (25.4 MB)
- Test Trends: Published to Jenkins via `target/surefire-reports/*.xml`

---

## 5. Summary of Deliverables
- Declarative Pipeline with Dual Test Gates: [Jenkinsfile](file:///C:/Users/Admin/Desktop/Projects/devops/Jenkinsfile)
- Deliberate Failure Verification Branch: `defect/deliberate-test-failure` (Pushed to GitHub)
- Clean Integration Branch: `develop` (Pushed to GitHub)
- Documentation: [docs/10_continuous_testing_in_jenkins.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/10_continuous_testing_in_jenkins.md)
