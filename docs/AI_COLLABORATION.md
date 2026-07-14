# AI Collaboration Narrative

## Purpose

This document records how AI is directed, challenged, verified, and corrected throughout the assessment. It will be condensed into the final README and video narrative.

## Collaboration principles

- The owner retains responsibility for architecture and submitted code.
- AI proposals are treated as hypotheses until verified through reasoning, documentation, compilation, or tests.
- Important assumptions and tradeoffs are written into the repository rather than left in chat history.
- Generated code must be reviewed for correctness, unnecessary complexity, privacy concerns, failure behavior, and test coverage.
- Verification evidence must name the command or review performed; vague claims are not accepted.

## Session log

### July 13, 2026 - Assessment analysis and scenario selection

**Direction given to AI:** Extract and analyze the supplied Keyloop assessment, compare the four scenarios, and recommend one suitable for a Senior Software Engineer submission.

**AI contribution:**

- Extracted and visually checked all four pages of the assessment.
- Compared scenarios by senior-level signal, delivery risk, testability, and differentiation.
- Recommended Scenario D with a backend implementation as the strongest risk-adjusted choice.
- Identified an ambiguity between Scenario D's aggregation behavior and the general persistent-database requirement.
- Proposed search auditing as a minimal persistence responsibility.

**Owner decision:** Accepted Scenario D and the backend service-layer recommendation.

**Verification and refinement:**

- The recommendation was checked against the assessment's stated acceptance criteria and evaluation dimensions.
- Scope was reduced to fit a three-day schedule.
- Production frontend, authentication, Kubernetes, messaging, and cloud deployment were explicitly excluded.

### July 13, 2026 - Continuity and usage-risk planning

**Direction given to AI:** Establish a workflow that another AI agent can resume if usage becomes unavailable.

**AI contribution:**

- Proposed repository-based durable context through `AGENTS.md`, planning, design, decision, handoff, and collaboration files.
- Proposed small Git checkpoints and test-backed handoffs.
- Created the initial documentation structure and a resume prompt.

**Owner verification required:**

- Review all initial assumptions.
- Confirm the implementation technology based on interview fluency.
- Review and accept or revise all proposed decisions before scaffolding.

### July 13, 2026 - Requirement re-analysis and stack selection

**Direction given to AI:** Re-read Scenario D, verify whether a UI must be implemented, and adopt current stable Kotlin and Spring Boot technologies.

**AI contribution:**

- Re-extracted the exact Scenario D and common implementation wording from the assessment.
- Distinguished the complete product requirements from the selected backend service-layer implementation.
- Confirmed that the document explicitly permits the client to be mocked with cURL, a test harness, or OpenAPI.
- Proposed Swagger UI plus cURL as the client stub so the video remains interactive without a custom frontend.
- Verified stable versions from official Kotlin, Spring, Gradle, PostgreSQL, Java, and springdoc project sources.

**Owner decision:** Accepted Kotlin and Spring Boot and requested current versions. The documentation records latest stable compatible versions rather than milestones, release candidates, or snapshots.

**Verification and refinement:**

- The new requirements matrix maps every Scenario D and common backend requirement to planned evidence.
- Java 25 LTS was chosen instead of the newer non-LTS Java 26 line to provide a defensible production baseline.
- Spring Boot dependency management will control compatible transitive library versions rather than forcing every library independently.

**Build correction:** The first dependency-resolution run showed unresolved Testcontainers modules. AI initially assumed Spring Boot 4.1 would manage them and also used the pre-2.0 artifact names. After checking the official 2.0.5 documentation, the project imported the Testcontainers BOM and corrected the modules to `testcontainers-junit-jupiter` and `testcontainers-postgresql`. Configuration cache was also disabled after the unresolved classpath triggered a secondary cache-serialization failure; correctness and repeatability take priority over that optimization.

**Runtime correction:** The first PostgreSQL 18.4 container exited because the Compose file used the pre-18 `/var/lib/postgresql/data` mount. PostgreSQL 18 images require the volume at `/var/lib/postgresql` to support major-version-specific data directories. Container logs identified the issue; the empty project-local volume was removed and the mount was corrected before retrying.

**Migration correction:** The aggregation service started and connected to PostgreSQL, but a direct schema query found no tables. In Spring Boot 4, the database-specific Flyway module is not sufficient to activate Boot's integration; `spring-boot-starter-flyway` is also required. The official Boot 4 initialization guide confirmed the modular starter requirement, so it was added before the migration was reverified.

### July 13, 2026 - Core implementation and verification

**Direction given to AI:** Proceed with the recommended Kotlin/Spring Boot backend and implement the Scenario D behavior without adding a custom UI.

**AI contribution:**

- Implemented separate Sales and Service HTTP adapters, correlation handling, concurrent orchestration, normalization, deterministic deduplication, complete/partial/failed responses, audit persistence, safe errors, metrics, and tests.
- Added deterministic mock fixtures for unavailable, delayed, empty, total-failure, and invalid-response behavior.
- Kept the public model independent from both source-specific schemas and kept raw VIN and document data out of persistence.

**Verification and correction:**

- The first implementation compile passed before tests were added.
- Unit tests use a two-party synchronization barrier to prove the blocking source ports are entered concurrently without relying on fragile wall-clock thresholds.
- Mock HTTP tests verify each source schema and correlation header independently.
- The initial PostgreSQL integration test exposed that `CrudRepository.save` treated an application-assigned UUID as an existing aggregate and issued an update instead of an insert. The write path was corrected to use `JdbcAggregateTemplate.insert`, which states the intended operation explicitly; the focused PostgreSQL 18.4 test then passed.
- Reviewing the generated JUnit XML showed that two expression-bodied coroutine tests compiled but were not discovered because Kotlin inferred a non-`Unit` return type. Explicit `Unit` return types restored discovery, and the final report was checked for all 14 expected test cases.
- Live HTTP probes verified complete, partial, timeout, empty, total-failure, invalid-downstream, and invalid-VIN behavior. Direct SQL verified persisted outcomes and 64-character HMAC fingerprints; Prometheus output verified request and source metrics.

### July 14, 2026 - Submission review and presentation preparation

**Direction given to AI:** Proceed with Day 3 submission-quality work, including reviewer-oriented code cleanup, clean-start verification, repository preparation, and the video walkthrough.

**AI contribution:**

- Reviewed the production and test code against the accepted decisions and removed an unused validation dependency and repository abstraction.
- Replaced stringly typed audit outcomes and warning conversion with explicit enums and mappings.
- Added downstream latency metrics, request duration logging, explicit correlation on unexpected-error logs, and a test proving required audit failure aborts the request.
- Reconciled the design document with the implementation, clearly separating implemented correlation from future distributed tracing.
- Prepared a timed 7-8 minute walkthrough and a final submission/email checklist.

**Verification:** The complete multi-module check, fresh-clone workflow, live demonstrations, repository hygiene scan, and public-link checks are recorded in the ledger and handoff once executed.

## Verification ledger

| Date | Artifact or behavior | Verification | Result |
|---|---|---|---|
| 2026-07-13 | Assessment extraction | Text extraction plus visual review of all four PDF pages | Complete |
| 2026-07-13 | Documentation structure | File listing and internal-reference search | Complete |
| 2026-07-13 | Repository initialization | `git init -b main` and Git status inspection | Complete |
| 2026-07-13 | Scenario D UI interpretation | Re-extracted assessment pages 2-4 and mapped scenario wording to the backend-choice clause | Complete |
| 2026-07-13 | Technology versions | Checked current stable releases in official project documentation | Complete |
| 2026-07-13 | Initial dependency resolution | `./gradlew test ktlintCheck`; detected missing Testcontainers versions and a secondary configuration-cache failure | Corrected |
| 2026-07-13 | Corrected JVM build | `./gradlew test ktlintCheck --no-daemon --console plain` on provisioned Java 25.0.1 | Complete |
| 2026-07-13 | Initial PostgreSQL startup | `podman compose up -d postgres`, aggregator startup, and container logs | PostgreSQL 18 mount corrected |
| 2026-07-13 | First healthy service startup | Health probes for ports 8080, 8081, and 8082 plus Swagger redirect | Complete |
| 2026-07-13 | Initial migration verification | Direct `pg_tables` query returned no tables | Spring Boot 4 Flyway starter added |
| 2026-07-13 | Corrected migration verification | Flyway startup logs plus direct `pg_tables` query | `flyway_schema_history` and `document_search_audit` present |
| 2026-07-13 | Core compilation | Aggregator formatting plus Kotlin compilation | Complete |
| 2026-07-13 | Core automated behavior | Multi-module Gradle tests and ktlint | Initial persistence test found a defect; corrected |
| 2026-07-13 | Persistence correction | Focused Testcontainers PostgreSQL 18.4 integration test | Complete |
| 2026-07-13 | Live acceptance paths | cURL against seven deterministic fixtures | Expected 200/400/503 and COMPLETE/PARTIAL outcomes verified |
| 2026-07-13 | Live persistence and telemetry | Direct PostgreSQL query plus Prometheus scrape | Expected privacy-safe rows and tagged metrics verified |
| 2026-07-13 | Final automated check | `./gradlew test ktlintCheck --no-daemon --console plain`; JUnit XML count reviewed | 14 tests and all style checks complete |
| 2026-07-14 | Day 3 behavior refinement | Focused aggregator tests after telemetry, enum, and audit-failure changes | 15 tests complete |
| 2026-07-14 | Fresh dependency/build workflow | Fresh local clone and empty Gradle home; `./gradlew test ktlintCheck --no-daemon --console plain` | All 28 tasks executed successfully in 1m43s |
| 2026-07-14 | Fresh test discovery | Counted generated JUnit XML cases | All 15 expected tests present |
| 2026-07-14 | Fresh runtime rehearsal | New PostgreSQL volume, Flyway migration, all three applications, cURL, direct SQL, metrics, and Swagger | COMPLETE 0.21s, PARTIAL timeout 2.10s, 503 and 400 verified |
| 2026-07-14 | Repository hygiene | Tracked-file inspection, ignore rules, whitespace checks, and common secret-pattern scan | No blockers found |

## AI mistakes and corrections

### Incorrect Testcontainers management assumption

AI assumed Spring Boot 4.1 would manage the Testcontainers test modules because earlier Spring Boot lines commonly did, and it used the pre-2.0 artifact names. Gradle resolved them with empty versions and failed the build. Official Testcontainers documentation showed that 2.0 prefixes module names with `testcontainers-`. The failure was corrected by importing the 2.0.5 BOM and using the new coordinates, and the correction was retained as evidence of build-driven AI verification.

### Outdated PostgreSQL container mount

AI initially used the traditional `/var/lib/postgresql/data` volume destination. PostgreSQL 18 changed its image layout and rejected that mount. The container logs explained the version-specific directory strategy, so the Compose configuration was updated to mount `/var/lib/postgresql` and the failed empty volume was safely recreated.

### Missing Spring Boot 4 Flyway starter

AI added Flyway's PostgreSQL database module based on older Spring Boot conventions. The application connected successfully but did not run migrations. A direct database query exposed the missing table, and the Spring Boot 4 guide confirmed that `spring-boot-starter-flyway` must activate the integration in addition to the database-specific Flyway module.

### Incorrect Spring Data JDBC save semantics for an assigned ID

AI initially called `CrudRepository.save` with a preassigned UUID. Spring Data JDBC interpreted the non-null identifier as an existing row and performed an update, so the method returned without inserting anything. A real PostgreSQL integration test—not compilation or a repository mock—caught the missing row. The audit writer now uses `JdbcAggregateTemplate.insert`, making new-record intent explicit and avoiding ambiguous entity-state detection.

## Final collaboration summary

1. **Delegated to AI:** assessment extraction, scenario comparison, architecture alternatives, scaffolding, implementation assistance, test generation, documentation, and reviewer-style audits.
2. **Human-owned decisions:** Scenario D selection, backend-only scope, Kotlin/Spring Boot stack, accepted reliability semantics, audit interpretation, final review, and submission responsibility.
3. **Changed AI suggestions:** incorrect dependency assumptions, the PostgreSQL 18 mount, Flyway activation, repository save semantics, and initially undiscovered coroutine tests were all corrected through evidence.
4. **Verification approach:** official documentation for unstable version facts, compiler and formatter checks, unit and mock-HTTP tests, Testcontainers PostgreSQL, container logs, direct SQL, live cURL requests, Prometheus output, and explicit JUnit test-count inspection.
5. **Most valuable defect found:** the real database integration test showed that a seemingly successful repository call did not insert its audit row, demonstrating why mocked repository tests alone were insufficient.
6. **Production evolution:** add identity and tenancy controls, secrets management, OpenTelemetry, measured resilience policies, response limits, and a durable audit pipeline where availability requirements justify it.

## Final narrative prompts

Before submission, this document must answer:

1. What work was delegated to AI?
2. What important decisions remained human-owned?
3. Which AI suggestions were rejected or changed, and why?
4. How was generated code verified?
5. Which defect or design weakness did verification uncover?
6. What would be done differently in a production implementation?
