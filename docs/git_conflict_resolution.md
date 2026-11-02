# Git Merge Conflict Simulation & Resolution Report
## Marathon Registration Portal — DevOps Pipeline Project

---

### 1. Scenario & Root Cause
During concurrent multi-developer collaboration on the Marathon Registration Portal:
- Developer 1 branched `feature/event-branding` off `develop` to update the event branding in `src/main/resources/application.properties` to `City Marathon 2026 - Global Runner Series`.
- Developer 2 simultaneously branched `feature/admin-status-workflow` to implement the administrative approval dashboard and updated the same configuration line to `City Marathon 2026 - Championship Edition`.
- Developer 1's branch was merged first into `develop`.
- When Developer 2 attempted to merge `feature/admin-status-workflow` into `develop`, Git detected non-reconcilable modifications to the exact same line, raising a merge conflict.

---

### 2. Conflict Evidence
Command executed:
```bash
git checkout develop
git merge feature/admin-status-workflow
```

Terminal output:
```text
Auto-merging src/main/resources/application.properties
CONFLICT (content): Merge conflict in src/main/resources/application.properties
Automatic merge failed; fix conflicts and then commit the result.
```

Unmerged content with Git conflict markers in `src/main/resources/application.properties`:
```properties
# Application Metadata
spring.application.name=marathon-portal
<<<<<<< HEAD
marathon.event.name=City Marathon 2026 - Global Runner Series
=======
marathon.event.name=City Marathon 2026 - Championship Edition
>>>>>>> feature/admin-status-workflow
marathon.event.date=2026-12-15
```

---

### 3. Resolution Procedure
1. Inspected unmerged files using `git status`.
2. Communicated across teams to agree on a unified branding descriptor: `City Marathon 2026 - Global Championship Series`.
3. Edited `src/main/resources/application.properties` removing conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`) and applying the agreed-upon configuration:
   ```properties
   marathon.event.name=City Marathon 2026 - Global Championship Series
   ```
4. Marked the file as resolved:
   ```bash
   git add src/main/resources/application.properties
   ```
5. Completed the merge commit:
   ```bash
   git commit -m "Merge branch 'feature/admin-status-workflow' into develop (resolved branding merge conflict)"
   ```

---

### 4. Outcome & Verification
- Clean working directory verified with `git status`.
- Application compiled cleanly with `BUILD SUCCESS` verifying zero syntax corruption.
- Complete merge graph documented in Git history.
