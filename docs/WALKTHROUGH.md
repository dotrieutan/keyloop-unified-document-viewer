# Video Walkthrough Script

Target length: 7-8 minutes. The goal is to explain decisions and evidence, not read every file.

For a script that can be read while recording, use [`VIDEO_SPEAKER_NOTES.md`](VIDEO_SPEAKER_NOTES.md).

## Before recording

- Use a clean `main` branch and increase terminal/editor font size.
- Start PostgreSQL first, then start `mock-sales-system`, `mock-service-system`, and `aggregator-service` in separate terminals using the README commands.
- Open `docs/SYSTEM_DESIGN.md`, `DocumentAggregationService.kt`, `DocumentSearchAuditIntegrationTest.kt`, and Swagger UI.
- Run the complete and timeout fixtures once before recording so there are no first-run surprises.
- Hide notifications, credentials, unrelated browser tabs, and local file paths where possible.

## 0:00-0:40 - Introduction

> Hi, I’m Do. I selected Scenario D, the Unified Document Viewer, and implemented the backend service layer in Kotlin and Spring Boot. The goal is one VIN search that queries separate Sales and Service systems and returns a single source-attributed document list.

Mention that Swagger UI and cURL are the permitted client stub; a custom frontend is intentionally out of scope.

## 0:40-1:35 - Scope and contract

Show the README fixture table and briefly state:

- `GET /api/v1/vehicles/{vin}/documents` is the public endpoint.
- Both downstream systems have deliberately different contracts.
- A two-source success is `COMPLETE`; one unusable source is `PARTIAL`; two unusable sources return `503`.
- Valid empty responses remain successful and are not confused with failures.

## 1:35-2:35 - Architecture and request flow

Show the architecture and sequence diagrams in `docs/SYSTEM_DESIGN.md`.

> The service validates and normalizes the VIN, calls Sales and Service concurrently with independent two-second deadlines, normalizes the two schemas, applies source-scoped deduplication and deterministic sorting, synchronously persists the audit outcome, then returns the response.

Explain why the database stores audit outcomes rather than copied documents: the source systems remain the document owners, and the assessment’s persistence requirement is satisfied without creating an unjustified document cache.

## 2:35-3:45 - Important implementation details

Show `DocumentAggregationService.kt`:

- `coroutineScope` plus `async(Dispatchers.IO)` starts both blocking HTTP adapters concurrently.
- Successful results survive one dependency failure.
- Sorting is newest first, then source and source ID.
- Metrics are tagged by overall and downstream outcome.

Show one client adapter to demonstrate that source-specific DTOs do not leak into the application service. Briefly point to the correlation filter and HMAC audit writer.

## 3:45-4:50 - AI collaboration and verification

> I used AI for assessment extraction, scenario comparison, design alternatives, scaffolding, implementation assistance, and review. I kept scenario selection, scope, stack, and tradeoff acceptance human-owned. I treated generated output as a hypothesis and checked it through official documentation, compilation, tests, container logs, direct SQL, and live HTTP behavior.

Use two concrete corrections:

1. PostgreSQL 18 changed the container volume layout; container logs exposed the outdated mount.
2. A PostgreSQL integration test found that `CrudRepository.save` with an assigned UUID performed an update instead of an insert. The implementation now uses an explicit aggregate insert.

Optionally mention that checking the JUnit XML found two coroutine tests that compiled but were not initially discovered.

## 4:50-6:20 - Live demonstration

In Swagger UI or a terminal, run the complete fixture:

```bash
curl -s http://localhost:8080/api/v1/vehicles/WVWZZZ1JZXW000001/documents
```

Point out `COMPLETE`, four documents, deterministic order, and `SALES`/`SERVICE` attribution.

Then run the timeout fixture:

```bash
time curl -s http://localhost:8080/api/v1/vehicles/WVWZZZ1JZXW000003/documents
```

Point out `PARTIAL`, preserved Sales documents, the Service `TIMEOUT` warning, and total latency bounded near two seconds rather than three.

## 6:20-7:10 - Persistence, observability, and tests

Show privacy-safe audit rows:

```bash
podman compose exec postgres psql \
  -U document_viewer \
  -d document_viewer \
  -c 'select outcome, sales_outcome, service_outcome, result_count, length(vin_fingerprint) from document_search_audit order by completed_at desc limit 5;'
```

Mention that raw VINs and document metadata are not persisted. Show the final Gradle result or test report: 15 tests, including a real PostgreSQL migration/write/read integration test and a synchronization-barrier concurrency test.

## 7:10-7:45 - Tradeoffs and close

> For production, I would add authentication and dealership authorization, OpenTelemetry traces, measured retry and circuit-breaker policies, response limits, and potentially a durable asynchronous audit pipeline. I intentionally excluded those from this three-day assessment so the implemented behavior remains focused, testable, and easy to explain.

End with the repository URL on screen.

## If the recording runs long

Keep the complete and timeout demonstrations. Shorten the technology list and show only one AI correction.
