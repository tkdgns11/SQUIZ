---
description: Workflow for creating or updating a major feature with documentation logging
---

# Feature Development Workflow

Follow this process when building new features to ensure quality and documentation.

1. **Analysis & Planning**
   - Read `frontend/.agent/RULES.md` to refresh memory on constraints.
   - [ ] Confirm strict separation from `backend/`.
   - [ ] Plan component structure (Features vs Shared).

2. **Implementation**
   - Create types first (if applicable).
   - Implement components.
   - Add necessary styles.
   - **Check**: Did I use `any`? If yes, refactor to proper types.

3. **Verification**
   - Run `npm run dev` to verify visual correctness.
   - Fix any strict mode errors.

4. **Documentation (Auto-Prompt)**
   - Check if `frontend/work-log.md` exists. If not, create it.
   - Append the work log entry for this sesson.
   
   **Example Command to append log**:
   ```bash
   echo -e "\n## [$(date +%Y-%m-%d)] FEATURE_NAME\n- **Summary**: SUMMARY\n- **Changes**: DETAILS" >> frontend/work-log.md
   ```
