# AI Working Agreement

This repository is the submission for Keyloop's Scenario D: Unified Document Viewer.
These instructions apply to every human or AI contributor working in this repository.

## Mission

Implement the backend service layer for a document aggregation system that:

- accepts a Vehicle Identification Number (VIN),
- queries mocked Sales and Service system APIs in parallel,
- normalizes their different responses,
- returns one consolidated document list with source attribution,
- handles downstream failures predictably, and
- uses a persistent database as required by the assessment.

The solution must be small enough to finish and present by July 15, 2026.

## Source of truth

Before changing code, read these files in order:

1. `docs/HANDOFF.md`
2. `docs/PLAN.md`
3. `docs/REQUIREMENTS.md`
4. `docs/SYSTEM_DESIGN.md`
5. `docs/DECISIONS.md`
6. `docs/AI_COLLABORATION.md`

If these documents disagree, stop and resolve the inconsistency in the documents before implementing a materially different design.

## Scope boundaries

Implement only:

- one document aggregation backend,
- two lightweight mocked downstream APIs,
- persistent search-audit storage,
- an OpenAPI/Swagger UI client stub and cURL-based demonstration,
- automated tests for core and failure behavior, and
- local development tooling needed to build, run, and test the system.

Do not add a production frontend, authentication, Kubernetes, messaging infrastructure, or cloud deployment unless the owner explicitly changes the scope.

## Technology baseline

- Kotlin 2.4.0
- Spring Boot 4.1.0
- Java 25 LTS
- Gradle 9.5.0 via the Gradle Wrapper
- PostgreSQL 18.4
- springdoc-openapi 3.0.3
- Testcontainers 2.0.5 BOM
- ktlint Gradle plugin 14.2.0

Use the latest stable version that is compatible with this baseline. Prefer versions managed by Spring Boot instead of independently overriding every transitive dependency. Do not use milestone, release-candidate, snapshot, or experimental releases merely because they are newer.

## Engineering rules

- Keep the architecture simple and explain every non-obvious abstraction.
- Validate VINs at the API boundary.
- Call downstream systems concurrently, with an independent timeout for each.
- Preserve successful results when only one downstream system fails.
- Include source-system attribution on every normalized document.
- Use deterministic ordering and an explicit deduplication rule.
- Propagate or create a correlation ID and use structured logs.
- Never log document contents or unnecessary customer data.
- Persist search audit information, not document bodies, unless a later recorded decision changes this.
- Prefer contract, unit, and focused integration tests over large end-to-end infrastructure.
- Do not claim a check passed unless it was run in the current workspace.

## Required workflow

For each implementation milestone:

1. Confirm the next unfinished item in `docs/HANDOFF.md`.
2. Make one coherent change.
3. Run the relevant formatter, static checks, and tests.
4. Update `docs/HANDOFF.md` with results and the exact next action.
5. Update `docs/AI_COLLABORATION.md` when AI materially influenced a decision or implementation.
6. Update `docs/DECISIONS.md` before changing an accepted architectural decision.

## Commands

Run commands from the repository root.

```bash
# Compile and test every module
./gradlew test

# Run the full submission check
./gradlew test ktlintCheck

# Apply Kotlin and Gradle Kotlin DSL formatting
./gradlew ktlintFormat

# Start and stop PostgreSQL 18.4
podman compose up -d postgres
podman compose down

# Start each application in its own terminal
./gradlew :mock-sales-system:bootRun
./gradlew :mock-service-system:bootRun
./gradlew :aggregator-service:bootRun
```

Docker Compose can be used instead of Podman Compose when available. Gradle runs on JDK 17 or newer and automatically provisions the Java 25 compilation toolchain through the checked-in toolchain resolver.

## Definition of done

The submission is done only when:

- all Scenario D acceptance criteria are demonstrated,
- the persistent database requirement is satisfied,
- failure and timeout behavior is tested,
- a clean setup can follow the README successfully,
- the system-design and AI-collaboration narratives match the code,
- no secrets or machine-specific files are committed, and
- the video demonstration steps have been rehearsed.
