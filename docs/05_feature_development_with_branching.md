# Phase 5: Feature Development with Branching
## Marathon Registration Portal — DevOps Pipeline Project

---

### 1. Feature 1 Implementation Overview
- **Branch Name:** `feature/registration-core` (branched from `develop`)
- **Core Functionality Implemented:**
  - **Domain Models:** `RaceCategory`, `RegistrationStatus`, and `Participant` with Bean Validation annotations (`@NotBlank`, `@Email`, `@Min`, `@Pattern`).
  - **Data Persistence:** Thread-safe in-memory `ParticipantRepository` with querying by ID, Registration ID, Email, Category, and Status.
  - **Service Layer:** `RegistrationService` implementing business rules (category age gating: FM: 18+, HM: 16+, 10K: 14+, 5K: 10+), waiver verification, and unique registration identifier generation (`REG-{CAT}-{HEX}`).
  - **Controller Routing:** `RegistrationController` handling `GET /`, `POST /register`, and `GET /confirmation/{id}`.
  - **Presentation Layer:** Responsive, mobile-first Thymeleaf templates (`index.html` and `confirmation.html`) built with Bootstrap 5 and icons.

---

### 2. Git Branching & Workflow Evidence
1. **Branch Creation:**
   ```bash
   git checkout develop
   git checkout -b feature/registration-core
   ```
2. **Staging & Commits:**
   ```bash
   git add src/ docs/
   git commit -m "feat(registration): implement domain models, thread-safe repository, and UI templates"
   ```
3. **Remote Push:**
   ```bash
   git push -u origin feature/registration-core
   ```
4. **Pull Request & Code Review:**
   - Raised PR #1 to merge `feature/registration-core` into `develop`.
   - Addressed review notes regarding emergency phone regex and Selenium DOM element IDs.
5. **Merge Execution:**
   ```bash
   git checkout develop
   git merge --no-ff feature/registration-core -m "Merge pull request #1 from feature/registration-core"
   git push origin develop
   ```

---

### 3. Verification & Compilation
- Clean Maven compilation verified:
  ```
  [INFO] Compiling 7 source files with javac [debug release 21] to target\classes
  [INFO] BUILD SUCCESS
  ```
