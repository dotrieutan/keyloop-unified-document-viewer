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

## Verification ledger

| Date | Artifact or behavior | Verification | Result |
|---|---|---|---|
| 2026-07-13 | Assessment extraction | Text extraction plus visual review of all four PDF pages | Complete |
| 2026-07-13 | Documentation structure | File listing and internal-reference search | Complete |
| 2026-07-13 | Repository initialization | `git init -b main` and Git status inspection | Complete |
| 2026-07-13 | Scenario D UI interpretation | Re-extracted assessment pages 2-4 and mapped scenario wording to the backend-choice clause | Complete |
| 2026-07-13 | Technology versions | Checked current stable releases in official project documentation | Complete |

## AI mistakes and corrections

Record every material AI error here, including how it was detected and corrected. None recorded yet.

## Final narrative prompts

Before submission, this document must answer:

1. What work was delegated to AI?
2. What important decisions remained human-owned?
3. Which AI suggestions were rejected or changed, and why?
4. How was generated code verified?
5. Which defect or design weakness did verification uncover?
6. What would be done differently in a production implementation?
