# Project Handoff

## Last updated

July 13, 2026

## Current milestone

Day 1: Design and foundation.

## Current state

- Scenario D and the backend service layer have been selected.
- The submission is scoped to one aggregation API, two mocked dependencies, persistent search auditing, automated tests, and cURL/OpenAPI demonstration.
- Durable AI working instructions and planning documents have been created.
- The initial architecture, sequence, assumptions, observability strategy, and scope boundaries are drafted.
- A local Git repository has been initialized on the `main` branch.
- Kotlin/Spring Boot and the stable technology baseline have been accepted.
- Scenario D's UI language has been reconciled with the backend-only choice: Swagger UI and cURL will stub the client, and no custom frontend is required.
- A requirements traceability matrix has been added.
- No application code or technology-specific project has been scaffolded.
- No test, build, or runtime command exists yet.

## Accepted decisions

- Implement the backend rather than the frontend.
- Call Sales and Service systems in parallel.
- Return partial results when exactly one dependency succeeds.
- Use persistence for minimal search auditing, not document content.
- Keep authentication, production UI, messaging, Kubernetes, and cloud deployment out of scope.

## Decisions still required

1. Finalize the public API response and partial-warning contract.
2. Define downstream Sales and Service mock schemas.
3. Define the audit schema and VIN privacy treatment.
4. Define document deduplication and result ordering.
5. Choose dependency timeouts and retry policy.

## Exact next action

Finalize the public and mocked downstream API contracts, record the decisions, and then scaffold the Kotlin/Spring Boot multi-module project. As part of scaffolding, add exact build, run, test, and formatting commands to `AGENTS.md` and `README.md`.

## Verification status

- Documentation presence and internal file references: checked on July 13, 2026.
- Git repository: initialized on the `main` branch; use `git log -1 --oneline` and `git status --short` to identify the latest checkpoint and any pending work.
- Build: not available.
- Tests: not available.
- Runtime: not available.

## Known risks

- A stack chosen for novelty rather than interview fluency will weaken the walkthrough.
- The assessment requires persistence even though Scenario D does not naturally own document data; the audit-only interpretation must be clearly justified.
- Retry behavior can make latency and failure semantics harder to reason about; it should not be added automatically.
- Day 1 must end with a runnable skeleton to protect Day 3 for documentation and video work.

## Resume prompt for another AI

> Read `AGENTS.md`, `docs/PLAN.md`, `docs/REQUIREMENTS.md`, `docs/SYSTEM_DESIGN.md`, `docs/DECISIONS.md`, and this handoff. Inspect the workspace before editing. Continue only the exact next action above. Preserve the accepted Kotlin/Spring Boot baseline and backend-only scope. After making changes, run all available checks and update this handoff with evidence and the next exact action.
