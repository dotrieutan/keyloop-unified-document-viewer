# Project Handoff

## Last updated

July 14, 2026

## Current milestone

The code submission is complete, verified, and publicly accessible. The owner-recorded video remains.

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
- The public REST endpoint concurrently calls both source adapters using Kotlin coroutines and independent two-second HTTP timeouts.
- Sales and Service schemas are normalized into a deterministic, source-attributed response with source-scoped deduplication.
- Complete, partial, empty, invalid-response, timeout, and total-failure semantics are implemented.
- Correlation IDs are propagated or generated and emitted in safe structured logs and responses.
- Every completed downstream search synchronously inserts a privacy-safe audit row using an HMAC-SHA256 VIN fingerprint.
- Prometheus metrics cover overall outcomes, duration, returned document count, and per-source outcomes.
- Unit, mock-HTTP contract, correlation, privacy, concurrency, and PostgreSQL 18.4 integration tests are implemented.
- Day 3 review removed unused abstractions, added explicit audit outcomes, request/downstream latency telemetry, and required-audit-failure coverage.
- The README, final system design, AI narrative, 7-8 minute walkthrough script, and submission email checklist are complete.
- Word-for-word speaker notes with on-screen cues are available in `docs/VIDEO_SPEAKER_NOTES.md`.
- A fresh clone with an empty Gradle dependency cache completed all 28 tasks and all 15 expected tests.
- The public repository is `https://github.com/dotrieutan/keyloop-unified-document-viewer`; anonymous HTTP access, `main`, and the published README were verified.

## Accepted decisions

- Implement the backend rather than the frontend.
- Call Sales and Service systems in parallel.
- Return partial results when exactly one dependency succeeds.
- Use persistence for minimal search auditing, not document content.
- Keep authentication, production UI, messaging, Kubernetes, and cloud deployment out of scope.

## Decisions still required

No functional architecture decision remains. The owner must choose the video host and record the walkthrough.

## Exact next action

Record the walkthrough using `docs/WALKTHROUGH.md`, upload it, verify the video link in an incognito browser, and replace `<VIDEO_URL>` in the submission email template.

## Verification status

- Documentation presence and internal file references: checked on July 13, 2026.
- Git repository: initialized on the `main` branch; use `git log -1 --oneline` and `git status --short` to identify the latest checkpoint and any pending work.
- Build and formatting: a fresh clone with a new Gradle home ran `./gradlew test ktlintCheck --no-daemon --console plain`; all 28 tasks executed successfully in 1 minute 43 seconds.
- Tests: 15 tests cover VIN validation, correlation IDs, both downstream schema adapters, invalid responses, concurrency, aggregation policies, deduplication, required audit failure, audit privacy, and real PostgreSQL persistence. Generated JUnit XML confirmed the expected count.
- Runtime: all three services started successfully. Fixture `...000001` returned `200 COMPLETE` with four ordered documents in 0.27 seconds; `...000003` returned `200 PARTIAL` with a Service `TIMEOUT` in 2.10 seconds; `...000005` returned an RFC 9457-style `503`; malformed VIN returned `400`.
- Persistence: PostgreSQL rows were queried directly and contained COMPLETE, PARTIAL, FAILED, EMPTY, TIMEOUT, UNAVAILABLE, and INVALID_RESPONSE outcomes with 64-character VIN fingerprints.
- Observability: the live Prometheus endpoint exposed request counts/durations/result distributions and per-source outcome counters; a supplied correlation UUID was echoed in both header and response body.
- Fresh-clone rehearsal: COMPLETE returned four documents in 0.21 seconds, Service timeout returned PARTIAL in 2.10 seconds, total failure returned 503, invalid VIN returned 400, Flyway initialized an empty database, and Swagger redirected correctly.
- Repository hygiene: tracked-file review, ignored-output review, whitespace checks, and common secret-pattern scans found no submission blockers.
- GitHub publication: `main` was pushed to a PUBLIC repository; GitHub reported the correct default branch and anonymous HTTP and raw README requests returned successfully.

## Known risks

- The walkthrough is rehearsed but cannot be recorded or hosted without the owner.
- Testcontainers requires a Docker-compatible runtime; Podman was verified from a fresh clone.

## Resume prompt for another AI

> Read `AGENTS.md`, `docs/PLAN.md`, `docs/REQUIREMENTS.md`, `docs/SYSTEM_DESIGN.md`, `docs/DECISIONS.md`, and this handoff. Inspect the workspace before editing. Continue only the exact next action above. Preserve the accepted Kotlin/Spring Boot baseline and backend-only scope. After making changes, run all available checks and update this handoff with evidence and the next exact action.
