# Keyloop Unified Document Viewer

Backend service-layer implementation for **Scenario D: The Unified Document Viewer** from the Keyloop Technical Assessment.

## Current status

Planning and architecture setup. Application code has not been scaffolded yet.

See:

- [Three-day delivery plan](docs/PLAN.md)
- [Requirements and traceability](docs/REQUIREMENTS.md)
- [System design](docs/SYSTEM_DESIGN.md)
- [Current handoff](docs/HANDOFF.md)
- [Architecture decisions](docs/DECISIONS.md)
- [AI collaboration narrative](docs/AI_COLLABORATION.md)

## Problem

A dealership user needs one search interface for all documents related to a vehicle. The backend accepts a VIN, queries mocked Sales and Service systems in parallel, normalizes the results, and returns a consolidated list that identifies the source of every document.

## Intended service behavior

- Validate the supplied VIN.
- Query both mocked downstream systems concurrently.
- Normalize incompatible downstream response formats.
- Return deterministic, source-attributed results.
- Return useful partial results if exactly one dependency fails or times out.
- Persist a minimal search audit record.
- Emit structured, correlation-aware telemetry.

## Repository structure

```text
.
|-- AGENTS.md
|-- README.md
`-- docs
    |-- AI_COLLABORATION.md
    |-- DECISIONS.md
    |-- HANDOFF.md
    |-- PLAN.md
    |-- REQUIREMENTS.md
    `-- SYSTEM_DESIGN.md
```

The application and mock-service directories will be added during the next milestone.

## Selected technology baseline

- Kotlin 2.4.0
- Spring Boot 4.1.0
- Java 25 LTS
- Gradle 9.5.0 via the Gradle Wrapper
- PostgreSQL 18.4
- springdoc-openapi 3.0.3 for OpenAPI and Swagger UI

Spring Boot dependency management will control compatible transitive library versions. The project will use current stable releases, not milestones, release candidates, snapshots, or experimental APIs.

## Client-side scope

Scenario D describes a search UI and aggregated view, but the assessment explicitly allows backend candidates to mock or stub the client through cURL or OpenAPI. This repository will provide Swagger UI and cURL examples instead of implementing a custom frontend. See [requirements and traceability](docs/REQUIREMENTS.md).

## Build, run, and test

To be added as part of the first implementation milestone. The final submission will provide one-command local startup and exact test instructions.

## AI collaboration narrative

AI is being used as an implementation collaborator, not as an unreviewed code generator. Architectural decisions, AI proposals, verification steps, corrections, and test evidence are recorded in [docs/AI_COLLABORATION.md](docs/AI_COLLABORATION.md).
