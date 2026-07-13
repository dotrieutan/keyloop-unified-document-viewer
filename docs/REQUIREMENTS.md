# Scenario D Requirements and Traceability

## Source

Keyloop Technical Assessment, Scenario D: The Unified Document Viewer, plus the common challenge structure on pages 3-4.

## Important interpretation

Scenario D describes an end-to-end product with a search interface and aggregated UI. The challenge does **not** require both service layers to be implemented. It explicitly requires the candidate to fully implement either the backend or the frontend and mock or stub the other layer.

This submission implements the backend. The client-side layer will therefore be represented by:

- generated OpenAPI documentation and Swagger UI,
- reproducible cURL examples, and
- optionally a scripted demonstration harness if it materially improves the video.

A custom production frontend is not required and is out of scope.

## Scenario D core requirements

| Source requirement | Meaning for the complete product | Backend submission evidence |
|---|---|---|
| Unified Search | A user can enter a VIN through one search interface. | One REST endpoint accepts and validates a VIN. Swagger UI supplies an interactive client stub; cURL provides a reproducible alternative. |
| Data Aggregation | The backend makes parallel requests to mocked Sales and Service APIs. | Two independently mocked HTTP APIs are called concurrently with observable, independently bounded outcomes. |
| Aggregated View | The UI displays one consolidated list and identifies each source system. | The REST response exposes one normalized list with explicit source attribution; Swagger UI renders the response contract. The designed production UI is documented but not implemented. |

## Common backend implementation requirements

| Requirement | Planned evidence |
|---|---|
| Expose a RESTful API. | Versioned document-search endpoint and generated OpenAPI contract. |
| Use a persistent database. | PostgreSQL-backed search audit records, with schema migrations. |
| Mock or stub the client-side layer. | Swagger UI, cURL examples, and optional script harness. |
| Fulfil the chosen scenario for the backend layer. | Parallel downstream calls, normalized aggregation, source attribution, and defined failure semantics. |
| Consider scalability, performance, reliability, maintainability, and observability. | System-design sections, implementation boundaries, structured telemetry, resilience tests, and documented future work. |

## System design deliverables

- [x] Architecture diagram drafted.
- [x] Component responsibilities drafted.
- [x] Data flow drafted.
- [x] Technology selection and justification recorded.
- [x] Observability strategy drafted.
- [x] GenAI design-phase usage documented.
- [x] Review and finalize all sections against the implementation.

## Repository deliverables

- [x] Git repository initialized.
- [x] README structure created.
- [x] Dedicated AI collaboration narrative created.
- [x] Working backend implementation.
- [x] Clear clean-checkout build, run, and test instructions.
- [x] Automated tests for core business logic and dependency failures.
- [x] Final OpenAPI contract and cURL examples.

## Video deliverables

- [ ] Brief personal introduction and selected scenario.
- [ ] System design and implementation highlights.
- [ ] One-to-two-minute AI collaboration story.
- [ ] Live success demonstration.
- [ ] Live partial-failure or timeout demonstration.
- [ ] Lessons learned and challenges.

## Definition of acceptance for this backend

The submission will be considered functionally complete when it demonstrates:

1. A valid VIN search returning normalized documents from both systems.
2. Proof that the downstream calls execute concurrently rather than sequentially.
3. Explicit `SALES` or `SERVICE` attribution for every returned document.
4. A deterministic combined response independent of downstream completion order.
5. Useful partial results with a clear warning when one system fails or times out.
6. A defined service error when both systems fail.
7. A persisted audit outcome for complete, partial, empty, and failed searches.
8. Automated tests for core success and failure behavior.
9. Interactive backend demonstration through Swagger UI without a custom frontend.
