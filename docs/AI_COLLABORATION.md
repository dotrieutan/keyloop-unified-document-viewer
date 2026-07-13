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

## AI mistakes and corrections

### Incorrect Testcontainers management assumption

AI assumed Spring Boot 4.1 would manage the Testcontainers test modules because earlier Spring Boot lines commonly did, and it used the pre-2.0 artifact names. Gradle resolved them with empty versions and failed the build. Official Testcontainers documentation showed that 2.0 prefixes module names with `testcontainers-`. The failure was corrected by importing the 2.0.5 BOM and using the new coordinates, and the correction was retained as evidence of build-driven AI verification.

### Outdated PostgreSQL container mount

AI initially used the traditional `/var/lib/postgresql/data` volume destination. PostgreSQL 18 changed its image layout and rejected that mount. The container logs explained the version-specific directory strategy, so the Compose configuration was updated to mount `/var/lib/postgresql` and the failed empty volume was safely recreated.

### Missing Spring Boot 4 Flyway starter

AI added Flyway's PostgreSQL database module based on older Spring Boot conventions. The application connected successfully but did not run migrations. A direct database query exposed the missing table, and the Spring Boot 4 guide confirmed that `spring-boot-starter-flyway` must activate the integration in addition to the database-specific Flyway module.

## Final narrative prompts

Before submission, this document must answer:

1. What work was delegated to AI?
2. What important decisions remained human-owned?
3. Which AI suggestions were rejected or changed, and why?
4. How was generated code verified?
5. Which defect or design weakness did verification uncover?
6. What would be done differently in a production implementation?
