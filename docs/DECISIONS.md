# Architecture Decision Log

## Decision status legend

- **Accepted:** implementation should follow this decision.
- **Proposed:** requires owner review before implementation.
- **Superseded:** retained for history but no longer active.

## ADR-001: Implement Scenario D

- **Status:** Accepted
- **Date:** July 13, 2026

### Context

The assessment offers four scenarios and evaluates system design, technical execution, AI verification, and communication under a short deadline.

### Decision

Implement Scenario D, the Unified Document Viewer.

### Rationale

It provides strong senior-level discussion around parallel I/O, dependency isolation, partial failure, schema normalization, observability, and API design while remaining achievable and presentable within three days.

### Consequences

The design must explicitly handle two independent mocked dependencies and explain the persistent-database interpretation.

## ADR-002: Implement the backend service layer

- **Status:** Accepted
- **Date:** July 13, 2026

### Decision

Fully implement the backend and represent the client with OpenAPI documentation, cURL examples, and a simple demonstration harness if needed.

### Consequences

The implementation must expose a RESTful API and use a persistent database. A production frontend is out of scope.

## ADR-003: Use search auditing for persistence

- **Status:** Accepted
- **Date:** July 13, 2026

### Context

Scenario D aggregates data owned by external systems, but the general backend instructions require a persistent database.

### Decision

Persist minimal search outcome audit records. Do not copy document bodies into the aggregation service database.

### Rationale

Audit persistence satisfies the assessment requirement while preserving clear data ownership and avoiding an unjustified cache or document repository.

### Consequences

The design must address data minimization, VIN privacy, retention, and persistence failure behavior.

## ADR-004: Preserve partial results

- **Status:** Accepted
- **Date:** July 13, 2026

### Decision

When one downstream system succeeds and the other fails or times out, return the successful documents with a structured warning. When both dependencies fail, return a service error.

### Rationale

Users receive useful data during a localized outage, while the response remains explicit about incompleteness.

### Consequences

The public contract and tests must distinguish complete success, partial success, empty success, and total failure.

## ADR-005: Technology stack

- **Status:** Accepted
- **Date:** July 13, 2026

### Decision

Use Kotlin 2.4.0, Spring Boot 4.1.0, Java 25 LTS, Gradle 9.5.0 through the wrapper, PostgreSQL 18.4, and springdoc-openapi 3.0.3. Allow Spring Boot to manage compatible versions of Flyway, Testcontainers, Jackson, Micrometer, and other supported dependencies.

### Selection criteria

- Fluency during the technical interview.
- Fast local build and startup.
- Clear concurrent HTTP composition.
- Strong automated testing support.
- Database migration and OpenAPI support.
- Structured logging and telemetry support.

### Consequences

The project adopts current major versions, including Spring Boot 4 and Java 25. Tests and a clean-checkout build must validate that the selected stable versions work together. Dependency versions managed by Spring Boot must not be overridden without a specific compatibility reason.

## ADR-006: Stub the client with Swagger UI and cURL

- **Status:** Accepted
- **Date:** July 13, 2026

### Context

Scenario D's complete product includes a VIN search interface and an aggregated UI. The common implementation instructions require only one service layer to be implemented fully and explicitly allow backend candidates to mock or stub the client using a test harness, cURL examples, or an OpenAPI contract.

### Decision

Do not build a custom frontend. Provide Swagger UI backed by the OpenAPI contract plus reproducible cURL examples. Document the production UI in the system design.

### Consequences

Swagger UI demonstrates VIN input and aggregated results interactively. UI product behavior remains represented in the design and response contract, while implementation effort stays focused on backend correctness, resilience, tests, and observability.

## ADR-007: Public API and partial-failure semantics

- **Status:** Accepted
- **Date:** July 13, 2026

### Decision

Expose `GET /api/v1/vehicles/{vin}/documents`. Return `200 COMPLETE` when both sources produce valid responses, including empty responses. Return `200 PARTIAL` with source-specific warnings when exactly one source fails or times out. Return an RFC 9457-style `503` problem when neither source produces a usable response. Invalid VINs return `400`.

### Consequences

The response contains normalized documents, per-source outcomes, client-safe warnings, retrieval time, and the correlation ID. The same correlation ID is returned in the `X-Correlation-ID` header.

## ADR-008: VIN validation and audit privacy

- **Status:** Accepted
- **Date:** July 13, 2026

### Decision

Normalize VINs to uppercase and validate the global 17-character format, excluding I, O, and Q. Do not implement the North American check-digit algorithm because it is not globally applicable and was not requested. Persist an HMAC-SHA256 VIN fingerprint using a configurable secret; never persist the raw VIN or document metadata in the audit table.

### Consequences

The audit schema stores correlation ID, VIN fingerprint, request and completion timestamps, overall outcome, both dependency outcomes, result count, and duration. The local environment may use a clearly marked development-only HMAC key.

## ADR-009: Timeout and retry policy

- **Status:** Accepted
- **Date:** July 13, 2026

### Decision

Give each downstream request an independent two-second response timeout and execute both concurrently. Do not retry in this implementation.

### Rationale

Retries can improve transient-failure recovery for safe reads, but they complicate the latency budget and amplify load during outages. The three-day submission demonstrates bounded latency and graceful partial results; production retry, jitter, and circuit-breaking policy is documented as future work.

## ADR-010: Deduplication and ordering

- **Status:** Accepted
- **Date:** July 13, 2026

### Decision

Treat `(sourceSystem, sourceDocumentId)` as document identity. Deduplicate only repeated records from the same source; never collapse records across Sales and Service because their identifiers and ownership domains differ. When a source repeats an identity, prefer the newest `createdAt`, then the lexicographically smallest title for a deterministic tie. Sort the final list by `createdAt` descending, then `sourceSystem`, then `id` ascending.

### Consequences

Response order does not depend on which parallel call completes first, and potentially distinct cross-system records remain visible.

## ADR-011: Required synchronous audit write

- **Status:** Accepted
- **Date:** July 13, 2026

### Decision

Persist the completed search audit synchronously before returning the response. If the audit record cannot be stored, return a server error rather than claiming the request completed according to the persistent-backend contract.

### Consequences

Database availability participates in request availability. In production, a durable asynchronous audit pipeline could decouple this concern, but that infrastructure is outside the assessment scope.
