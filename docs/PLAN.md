# Three-Day Delivery Plan

## Objective

Deliver an interview-ready backend implementation of Keyloop Scenario D between July 13 and July 15, 2026.

## Day 1 - Monday, July 13: Design and foundation

### Outcomes

- [x] Select Scenario D and the backend service layer.
- [x] Establish durable repository instructions and handoff documentation.
- [ ] Select and record the implementation technology stack.
- [ ] Resolve and document the remaining product assumptions.
- [ ] Define the normalized API contract and downstream mock contracts.
- [ ] Complete the architecture and request-sequence diagrams.
- [ ] Scaffold the aggregation API and both mocked downstream APIs.
- [ ] Configure persistent local database startup.
- [ ] Implement basic health endpoints and VIN validation.
- [ ] Add exact build, run, format, and test commands to `AGENTS.md` and `README.md`.

### Day 1 exit criteria

- The project builds from a clean checkout.
- The aggregation API and both mock systems start locally.
- The database connection is verified.
- The API contract and architecture are documented.

## Day 2 - Tuesday, July 14: Behavior and verification

### Outcomes

- [ ] Call Sales and Service systems concurrently.
- [ ] Enforce an independent timeout for each downstream call.
- [ ] Normalize both downstream document formats.
- [ ] Attribute every result to its source system.
- [ ] Apply deterministic sorting and documented deduplication.
- [ ] Preserve partial results when one downstream system fails.
- [ ] Persist search audit records.
- [ ] Add correlation IDs and structured logging.
- [ ] Test success, partial failure, timeout, total failure, invalid VIN, empty results, duplicates, malformed responses, and persistence.
- [ ] Document actual verification evidence.

### Day 2 exit criteria

- All assessment acceptance criteria are implemented.
- Core tests pass.
- Success and partial-failure demonstrations work locally.
- No major functionality is deferred to Day 3.

## Day 3 - Wednesday, July 15: Submission quality

### Outcomes

- [ ] Refactor unclear code and remove unused dependencies.
- [ ] Run the complete formatter, static-analysis, and test suite.
- [ ] Finalize README setup and cURL examples.
- [ ] Finalize the system-design document and diagrams.
- [ ] Finalize the AI collaboration narrative.
- [ ] Verify the documented clean-start workflow.
- [ ] Review the repository for secrets and machine-specific artifacts.
- [ ] Push the repository to GitHub.
- [ ] Rehearse and record the 5-10 minute walkthrough.
- [ ] Verify that repository and video links are accessible.

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

