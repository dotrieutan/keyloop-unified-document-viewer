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
- No application code or technology-specific project has been scaffolded.
- No test, build, or runtime command exists yet.

## Accepted decisions

- Implement the backend rather than the frontend.
- Call Sales and Service systems in parallel.
- Return partial results when exactly one dependency succeeds.
- Use persistence for minimal search auditing, not document content.
- Keep authentication, production UI, messaging, Kubernetes, and cloud deployment out of scope.

## Decisions still required

1. Select the language, framework, build system, and database migration tool.
2. Finalize the public API response and partial-warning contract.
3. Define downstream Sales and Service mock schemas.
4. Define the audit schema and VIN privacy treatment.
5. Define document deduplication and result ordering.
6. Choose dependency timeouts and retry policy.

## Exact next action

Discuss the owner's strongest interview technology stack, select the implementation stack, and record the decision in `docs/DECISIONS.md`. Then update `AGENTS.md` with exact build, run, test, and formatting commands before scaffolding application code.

## Verification status

- Documentation presence and internal file references: checked on July 13, 2026.
- Git repository: initialized on the `main` branch; no commit has been created yet.
- Build: not available.
- Tests: not available.
- Runtime: not available.

## Known risks

- A stack chosen for novelty rather than interview fluency will weaken the walkthrough.
- The assessment requires persistence even though Scenario D does not naturally own document data; the audit-only interpretation must be clearly justified.
- Retry behavior can make latency and failure semantics harder to reason about; it should not be added automatically.
- Day 1 must end with a runnable skeleton to protect Day 3 for documentation and video work.

## Resume prompt for another AI

> Read `AGENTS.md`, `docs/PLAN.md`, `docs/SYSTEM_DESIGN.md`, `docs/DECISIONS.md`, and this handoff. Inspect the workspace before editing. Continue only the exact next action above. Do not select a technology stack without confirming the owner's interview fluency. After making changes, run all available checks and update this handoff with evidence and the next exact action.
