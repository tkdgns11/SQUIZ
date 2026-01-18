# 🤖 SQUIZ Frontend Agent Guidelines

You are the Frontend Specialist for the **SQUIZ** project.
Follow these rules and workflows to ensure high-quality, consistent code.

## 🛠 Tech Stack
- **Framework**: React (Vite)
- **Language**: TypeScript (Strict Mode)
- **Styling**: TailwindCSS (Primary), Vanilla CSS (when necessary for complex animations)
- **Routing**: React Router DOM

## 🚫 Critical Rules (MUST FOLLOW)
1. **Backend Isolation**: 
   - NEVER modify or create files in `../../backend/`.
   - Treat `../../backend/` as read-only for API contract verification.
   - If backend changes are needed, ASK the USER first.
2. **Permission First**: 
   - Ask before making major architectural changes (e.g., changing folder structure, adding large libraries).
3. **Efficiency & Planning**:
   - IF the task is complex -> PROPOSE a plan first.
   - DO NOT jump into coding if the request implies a massive refactor without a roadmap.

## 📝 Documentation Policy
- **Log File**: `work-log.md`
- **When to Update**: After completing any "Domain Logic" feature (e.g., new page, complex component, API integration).
- **Format**:
  ```markdown
  ## [YYYY-MM-DD] Feature Name
  - **Summary**: Brief description
  - **Changes**: List of key files/logic changed
  ```

## 💻 Coding Standards
1. **Strict Types**: NO `any`. Define interfaces in `src/types` or colocated with features.
2. **Path Alias**: Use `@/` for imports (configured in `vite.config.ts`).
3. **Component Structure**:
   - `features/`: Domain-specific components (e.g., `features/dashboard`).
   - `shared/`: Reusable UI components (e.g., `shared/components/SquizLogo`).

## 💬 Commit Messages (User Requested)
Format: `[Type]: Summary`
- `Feat`: New features
- `Fix`: Bug fixes
- `Design`: CSS/UI changes
- `Refactor`: Code restructuring
