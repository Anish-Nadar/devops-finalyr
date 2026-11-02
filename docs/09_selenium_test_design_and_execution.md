# Phase 9: Selenium Test Design and Local Execution

## 1. Executive Summary
Phase 9 delivers automated End-to-End (E2E) browser verification for the Marathon Registration Portal using **Selenium WebDriver (v4.19.1)** and **JUnit 5 Jupiter**, running in a headless Chrome environment. The suite validates core user journeys, client-side/server-side validations, status tracking, and admin moderation workflows against an active Spring Boot test server on dynamic ports (`@SpringBootTest(webEnvironment = RANDOM_PORT)`).

---

## 2. Test Architecture and Implementation

### 2.1 Technology Stack
- **Framework:** JUnit 5 (`org.junit.jupiter.api.*`) & Spring Boot Test (`@SpringBootTest`)
- **Automation Driver:** Selenium WebDriver 4.19.1 with Selenium Manager automated driver resolution
- **Browser:** Headless Google Chrome (`--headless=new`, `--disable-gpu`, `--window-size=1920,1080`, `--no-sandbox`)
- **Locators:** Robust CSS Selectors, ID bindings, and XPath fallbacks
- **Synchronization:** Explicit synchronization using `WebDriverWait` (`Duration.ofSeconds(10)`) and `ExpectedConditions`
- **Artifact Capture:** Auto-capture of PNG screenshots into `screenshots/` directory for key milestones and assertion verifications

### 2.2 Test Journeys Implemented

| Test ID | Test Method | Flow Description | Locators Used | Verification Target |
|---|---|---|---|---|
| **E2E-01** | `testSuccessfulRegistrationFlow` | Full registration flow from landing page to confirmation | `#fullName`, `#email`, `#phone`, `#age`, `#category`, `#waiverAccepted`, `#submitBtn` | Checks redirection to `/confirmation`, validates generated `REG-` ID and `ATH-` Bib number |
| **E2E-02** | `testValidationUnderageParticipant` | Enforces minimum age boundary rules (age < 18) | `#fullName`, `#email`, `#age`, `#waiverAccepted`, `#submitBtn` | Checks validation message appearance and form block |
| **E2E-03** | `testRegistrationStatusLookupFlow` | Participant self-service status verification lookup | `#searchRegistrationId`, `#searchBtn`, `#participantCard`, `.status-badge` | Confirms status card renders with `PENDING_REVIEW` |
| **E2E-04** | `testAdminDashboardApprovalFlow` | Event admin review and approval lifecycle | `table.table tbody tr`, `.btn-success`, `.badge-confirmed` | Confirms transition from `PENDING_REVIEW` to `CONFIRMED` |

---

## 3. Test Execution and Results

### 3.1 Command Line Execution
```bash
# Execute Selenium E2E Suite
.\mvnw.cmd test -Dtest=RegistrationSeleniumTest

# Execute complete test suite (Unit + MockMvc + Selenium E2E)
.\mvnw.cmd test
```

### 3.2 Test Results Summary
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.marathon.selenium.RegistrationSeleniumTest
[SELENIUM] Captured screenshot: screenshots\journey1_form_filled_20261102_215211_201.png
[SELENIUM] Captured screenshot: screenshots\journey1_confirmation_success_20261102_215214_305.png
[SELENIUM] Captured screenshot: screenshots\journey3_status_lookup_success_20261102_215224_491.png
[SELENIUM] Captured screenshot: screenshots\journey2_age_validation_error_20261102_215241_764.png
[SELENIUM] Captured screenshot: screenshots\journey4_admin_approval_success_20261102_215250_587.png
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 62.40 s -- in com.marathon.selenium.RegistrationSeleniumTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 3.3 Test Suite Evidence
- **JUnit Surefire XML Report:** `target/surefire-reports/TEST-com.marathon.selenium.RegistrationSeleniumTest.xml`
- **Surefire Text Summary:** `target/surefire-reports/com.marathon.selenium.RegistrationSeleniumTest.txt`
- **Captured Screenshots:**
  - `screenshots/journey1_form_filled_*.png`: Registration form populated with participant data
  - `screenshots/journey1_confirmation_success_*.png`: Confirmation screen with BIB and Registration ID
  - `screenshots/journey2_age_validation_error_*.png`: Validation banner preventing underage registration
  - `screenshots/journey3_status_lookup_success_*.png`: Status lookup card rendering pending participant
  - `screenshots/journey4_admin_approval_success_*.png`: Admin portal showing confirmed status badge

---

## 4. Source Files Created
- [src/test/java/com/marathon/selenium/RegistrationSeleniumTest.java](file:///C:/Users/Admin/Desktop/Projects/devops/src/test/java/com/marathon/selenium/RegistrationSeleniumTest.java)
- [src/test/java/com/marathon/controller/RegistrationControllerTest.java](file:///C:/Users/Admin/Desktop/Projects/devops/src/test/java/com/marathon/controller/RegistrationControllerTest.java)
- [docs/09_selenium_test_design_and_execution.md](file:///C:/Users/Admin/Desktop/Projects/devops/docs/09_selenium_test_design_and_execution.md)

