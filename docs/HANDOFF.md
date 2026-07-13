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
- The Gradle 9.5.0 wrapper and Java 25 auto-provisioning are configured.
- Three Spring Boot 4.1/Kotlin 2.4 modules are scaffolded: aggregator, Sales mock, and Service mock.
- PostgreSQL 18.4 Compose startup and the Flyway V1 audit migration are verified.
- Mock fixtures provide success, empty, downstream failure, and delayed-response VIN cases.
- VIN normalization and format validation have unit tests.
- All application health endpoints and the Swagger UI redirect have been verified.

## Accepted decisions

- Implement the backend rather than the frontend.
- Call Sales and Service systems in parallel.
- Return partial results when exactly one dependency succeeds.
- Use persistence for minimal search auditing, not document content.
- Keep authentication, production UI, messaging, Kubernetes, and cloud deployment out of scope.

## Decisions still required

1. Implement the aggregation service domain and public response models.
2. Implement both downstream HTTP clients with independent two-second timeouts.
3. Execute both client calls concurrently and apply complete/partial/failed semantics.
4. Implement deterministic normalization, source-scoped deduplication, and ordering.
5. Persist the HMAC VIN audit record and expose the public controller.

## Exact next action

Implement the core aggregation slice from HTTP request through both mocked dependencies to the normalized response and synchronous audit write. Add focused tests for complete success, partial failure, total failure, ordering, and persistence.

## Verification status

- Documentation presence and internal file references: checked on July 13, 2026.
- Git repository: initialized on the `main` branch; use `git log -1 --oneline` and `git status --short` to identify the latest checkpoint and any pending work.
- Build and formatting: `./gradlew test ktlintCheck --no-daemon --console plain` passed on Java 25.0.1.
- Tests: three VIN unit tests passed; mock modules currently have no tests.
- Runtime: ports 8080, 8081, and 8082 reported health `UP`; Swagger returned a redirect.
- Persistence: PostgreSQL 18.4 reported healthy and Flyway created `flyway_schema_history` plus `document_search_audit`.

## Known risks

- The aggregator has only foundation code; core acceptance behavior remains unimplemented.
- The synchronous audit requirement means database failure will fail the request and must be tested and explained.
- Mock behavior is VIN-fixture-driven and must be clearly documented before the video.
- Day 2 functionality must remain focused so Day 3 stays available for documentation and presentation.

## Resume prompt for another AI

> Read `AGENTS.md`, `docs/PLAN.md`, `docs/REQUIREMENTS.md`, `docs/SYSTEM_DESIGN.md`, `docs/DECISIONS.md`, and this handoff. Inspect the workspace before editing. Continue only the exact next action above. Preserve the accepted Kotlin/Spring Boot baseline and backend-only scope. After making changes, run all available checks and update this handoff with evidence and the next exact action.
