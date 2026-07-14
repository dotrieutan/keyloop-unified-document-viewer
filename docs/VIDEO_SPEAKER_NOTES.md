# Video Speaker Notes

Target: about 7 minutes. Text inside `[square brackets]` is an action and should not be spoken.

Keep these notes open on a second screen or beside the recording window. Speak slowly and naturally; you do not need to memorize every sentence.

## Before pressing Record

- Start PostgreSQL first:

  ```bash
  podman compose up -d postgres
  ```

- In three separate terminals, start the two external-system mocks and then the aggregator:

  ```bash
  ./gradlew :mock-sales-system:bootRun
  ./gradlew :mock-service-system:bootRun
  ./gradlew :aggregator-service:bootRun
  ```

- Verify that Swagger UI opens at `http://localhost:8080/swagger-ui.html` before recording.
- Open the repository README, `docs/SYSTEM_DESIGN.md`, `DocumentAggregationService.kt`, `DocumentSearchAuditIntegrationTest.kt`, and Swagger UI.
- In Swagger UI, expand `GET /api/v1/vehicles/{vin}/documents`.
- Increase editor and terminal text to at least 16-18 points.
- Run the tests and both demo VINs once before recording.
- Turn off notifications and close unrelated tabs.

## 0:00 - Introduction

`[ON SCREEN: Repository README title]`

Hello, my name is Do. Thank you for reviewing my technical challenge.

I selected Scenario D, the Unified Document Viewer. I implemented the backend service layer using Kotlin, Spring Boot, and PostgreSQL.

The business problem is that dealership users currently need to search separate Sales and Service systems for vehicle documents. My solution provides one API where a user supplies a VIN and receives one consolidated, source-attributed document list.

The repository contains the aggregator plus two external-system mocks named `mock-sales-system` and `mock-service-system`. Those names make it clear that they simulate separate source systems rather than adding more business service layers.

The assessment allows the candidate to implement either the backend or frontend. I chose the backend, so Swagger UI and cURL act as the client stub. A custom frontend is intentionally outside the scope of this submission.

## 0:45 - Requirements and behavior

`[ON SCREEN: README fixture table]`

The public endpoint is a GET request at `/api/v1/vehicles/{vin}/documents`.

The VIN is normalized and validated at the API boundary. The aggregator then calls two independently mocked systems. These systems deliberately use different API paths and response formats, so the integration boundary is visible.

If both systems respond correctly, the result status is COMPLETE. A valid empty response is still successful.

If one system fails or times out, the API preserves the documents from the successful system and returns PARTIAL with a safe warning.

If both systems fail, the API returns a 503 problem response. This makes the difference between no documents and unavailable systems explicit.

## 1:35 - Architecture

`[ON SCREEN: Architecture and sequence diagrams in docs/SYSTEM_DESIGN.md]`

This is the implemented request flow.

The aggregation API validates the VIN and establishes a correlation ID. It calls the Sales and Service system mocks concurrently, with a separate two-second timeout for each request.

The source-specific responses are converted into one normalized document model. The service then deduplicates and sorts the documents deterministically before persisting the search audit and returning the response.

I chose to store search audit outcomes instead of copying document data into PostgreSQL. Sales and Service remain the document owners, and this avoids creating an unjustified cache. The audit record contains outcomes, timing, result count, correlation ID, and an HMAC fingerprint of the VIN. It does not contain the raw VIN or document metadata.

## 2:30 - Implementation highlights

`[ON SCREEN: DocumentAggregationService.kt, at fetchAll]`

The aggregation service uses a coroutine scope and async calls on the I/O dispatcher. Both blocking HTTP adapters therefore start concurrently instead of waiting for one another.

Each HTTP client owns its source-specific contract, timeout configuration, mapping, and failure classification. The application service only works with normalized documents and source outcomes.

`[ON SCREEN: Move to outcome handling and normalize function]`

The overall outcome is calculated from the two independent source results. Successful data is preserved during a partial failure.

Document identity is the combination of source system and source document ID. I only deduplicate within one source because matching IDs from two different systems may represent different documents.

The final order is creation time descending, then source system and document ID. This means the response does not change based on which parallel request completes first.

The same correlation ID is propagated to both dependencies and returned in the response header and body. Metrics record overall request outcomes, request duration, result count, and downstream latency and outcomes.

## 3:45 - AI collaboration and verification

`[ON SCREEN: docs/AI_COLLABORATION.md, session log or mistakes section]`

I used AI as an implementation collaborator. I delegated assessment extraction, scenario comparison, design alternatives, scaffolding, implementation assistance, test generation, and reviewer-style audits.

I kept the important decisions human-owned: scenario selection, backend scope, technology stack, failure semantics, persistence interpretation, tradeoffs, and final submission responsibility.

I treated AI output as a hypothesis rather than accepting it automatically. I checked unstable facts against official documentation and verified the implementation through compilation, formatting, unit tests, mock HTTP tests, a real PostgreSQL integration test, container logs, direct SQL, live HTTP requests, and Prometheus metrics.

One useful correction came from PostgreSQL 18. The initial container used the older volume location. The container logs exposed the problem, and I corrected the Compose configuration.

A more important defect was found by the database integration test. A repository save with an assigned UUID was interpreted as an update, so no audit row was inserted. I replaced it with an explicit aggregate insert and verified the real database row.

I also inspected the generated JUnit reports and found two coroutine tests that compiled but were not initially discovered. After correcting their return types, all 15 expected tests executed.

## 5:05 - Complete-result demonstration

`[ON SCREEN: Swagger UI]`

I will now demonstrate the normal success path using a deterministic fixture VIN ending in one.

`[ACTION: Click Try it out. Enter WVWZZZ1JZXW000001. Click Execute.]`

The response is HTTP 200 with status COMPLETE. It contains two Service documents and two Sales documents in one list.

Each document clearly identifies its source system. The documents are ordered by creation time, independent of downstream completion order. The response also shows the same two successful source outcomes and no warnings.

## 5:45 - Timeout and partial-result demonstration

`[ON SCREEN: Swagger UI, same endpoint]`

Next, I will use a fixture VIN ending in three. The Service mock deliberately waits for three seconds, while the configured client deadline is two seconds.

`[ACTION: Enter WVWZZZ1JZXW000003. Click Execute.]`

The response still returns HTTP 200, but the status is PARTIAL. The successful Sales documents are preserved. The Service source is marked TIMEOUT, and the client receives a stable warning without internal exception details.

The response finishes near the two-second deadline instead of waiting for the mock’s full three seconds. This demonstrates bounded dependency latency and graceful degradation.

## 6:25 - Persistence and tests

`[ON SCREEN: Terminal with the audit SQL command already prepared]`

The audit table records COMPLETE, PARTIAL, and FAILED searches, along with each source outcome and result count.

`[ACTION: Run the audit query from docs/WALKTHROUGH.md.]`

The VIN fingerprint is 64 hexadecimal characters. The raw VIN and document metadata are not stored.

`[ON SCREEN: Gradle BUILD SUCCESSFUL output or test report]`

The project has 15 tests. They cover VIN validation, correlation IDs, both downstream schemas, invalid responses, parallel execution, complete and partial behavior, total failure, deterministic deduplication, required audit failure, HMAC privacy, and a real PostgreSQL migration, write, and read cycle.

A fresh clone with an empty dependency cache also completed the documented build successfully.

## 7:05 - Tradeoffs and closing

`[ON SCREEN: README or public GitHub repository]`

For a production implementation, I would add authentication and dealership-level authorization, secrets management, OpenTelemetry traces, measured retry and circuit-breaker policies, response limits, and a durable asynchronous audit pipeline if search availability must be independent from the audit database.

I deliberately excluded those features from this three-day implementation so the submitted behavior remains focused, testable, and easy to explain.

The complete source code, setup instructions, system design, tests, API contracts, and AI collaboration evidence are available in this public repository.

Thank you for your time. I look forward to discussing the design and tradeoffs in more detail.

## Emergency Short Version

If the recording approaches 10 minutes, skip the detailed sorting explanation, mention only the database-insert AI correction, and keep both live API demonstrations.
