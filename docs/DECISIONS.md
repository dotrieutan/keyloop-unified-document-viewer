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

- **Status:** Proposed
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

- **Status:** Proposed
- **Date:** July 13, 2026

### Decision

When one downstream system succeeds and the other fails or times out, return the successful documents with a structured warning. When both dependencies fail, return a service error.

### Rationale

Users receive useful data during a localized outage, while the response remains explicit about incompleteness.

### Consequences

The public contract and tests must distinguish complete success, partial success, empty success, and total failure.

## ADR-005: Technology stack

- **Status:** Proposed
- **Date:** July 13, 2026

### Decision

Pending discussion of the owner's strongest interview stack.

### Selection criteria

- Fluency during the technical interview.
- Fast local build and startup.
- Clear concurrent HTTP composition.
- Strong automated testing support.
- Database migration and OpenAPI support.
- Structured logging and telemetry support.

