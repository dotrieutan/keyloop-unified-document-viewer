# Three-Day Delivery Plan

## Objective

Deliver an interview-ready backend implementation of Keyloop Scenario D between July 13 and July 15, 2026.

## Day 1 - Monday, July 13: Design and foundation

### Outcomes

- [x] Select Scenario D and the backend service layer.
- [x] Establish durable repository instructions and handoff documentation.
- [x] Select and record the implementation technology stack.
- [x] Reconcile Scenario D's UI wording with the backend-only implementation choice.
- [x] Resolve and document the remaining product assumptions.
- [x] Define the normalized API contract and downstream mock contracts.
- [x] Complete the architecture and request-sequence diagrams.
- [x] Scaffold the aggregation API and both mocked downstream APIs.
- [x] Configure persistent local database startup.
- [x] Implement basic health endpoints and VIN validation.
- [x] Add exact build, run, format, and test commands to `AGENTS.md` and `README.md`.

### Day 1 exit criteria

- The project builds from a clean checkout.
- The aggregation API and both mock systems start locally.
- The database connection is verified.
- The API contract and architecture are documented.

## Day 2 - Tuesday, July 14: Behavior and verification

### Outcomes

- [x] Call Sales and Service systems concurrently.
- [x] Enforce an independent timeout for each downstream call.
- [x] Normalize both downstream document formats.
- [x] Attribute every result to its source system.
- [x] Apply deterministic sorting and documented deduplication.
- [x] Preserve partial results when one downstream system fails.
- [x] Persist search audit records.
- [x] Add correlation IDs and structured logging.
- [x] Test success, partial failure, timeout, total failure, invalid VIN, empty results, duplicates, malformed responses, and persistence.
- [x] Document actual verification evidence.

### Day 2 exit criteria

- All assessment acceptance criteria are implemented.
- Core tests pass.
- Success and partial-failure demonstrations work locally.
- No major functionality is deferred to Day 3.

## Day 3 - Wednesday, July 15: Submission quality

### Outcomes

- [x] Refactor unclear code and remove unused dependencies.
- [x] Run the complete formatter, static-analysis, and test suite.
- [x] Finalize README setup and cURL examples.
- [x] Finalize the system-design document and diagrams.
- [x] Finalize the AI collaboration narrative.
- [x] Verify the documented clean-start workflow.
- [x] Review the repository for secrets and machine-specific artifacts.
- [x] Push the repository to GitHub.
- [x] Rehearse the documented walkthrough and live demonstrations.
- [ ] Record the 5-10 minute walkthrough.
- [x] Verify anonymous access to the public repository and README.
- [ ] Verify that the final video link is accessible.

### Day 3 exit criteria

- A reviewer can build, run, test, and understand the system from the repository alone.
- The video demonstrates success and a meaningful failure mode.
- The submission email contains working repository and video links.

## Priority order if time becomes constrained

1. Correct aggregation and partial-failure behavior.
2. Tests for core business and resilience behavior.
3. Clear setup documentation and API examples.
4. System-design and AI-collaboration narratives.
5. Additional polish.

Optional features must never displace the first four priorities.
