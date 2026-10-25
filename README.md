# Agent Reliability & Evaluation Platform

A Java-based platform for evaluating the reliability of AI-powered incident investigation agents.

## Overview

AI agents can investigate incidents and explain system failures, but their conclusions need to be evaluated for correctness, evidence alignment, and reliability.

The **Agent Reliability & Evaluation Platform** provides a deterministic evaluation layer for incident investigation agents. It supplies diagnostic evidence, consumes structured incident reports, evaluates their claims against known evidence, and calculates reliability metrics across evaluation scenarios.

The project uses Java, JUnit 5, Maven, and an injectable LLM client to separate agent behavior from model execution.

## Key Features

- **Diagnostic tools:** Retrieve service health, incident metrics, application logs, and recent deployments.
- **Incident investigation agent:** Collect diagnostic evidence and generate an incident report.
- **Mockable LLM integration:** Use a deterministic fake client for local development and testing, with an OpenAI client implementation available for future live API execution.
- **Structured report parsing:** Convert JSON responses into typed Java objects and reject malformed or invalid reports.
- **Reliability evaluation:** Evaluate incident reports against known evidence and check root-cause claims and confirmation status.
- **Reliability metrics:** Aggregate evaluation results across scenarios to measure agent reliability.
- **Regression checks:** Run repeatable evaluation scenarios to detect regressions in agent behavior.
- **Automated CI:** Use GitHub Actions to run the Maven test suite on pushes and pull requests targeting `master`.

## Architecture

```text
Incident Request
       |
       v
Incident Investigation Agent
       |
       +---- Service Health Tool
       |
       +---- Incident Metrics Tool
       |
       +---- Log Search Tool
       |
       +---- Recent Deployments Tool
       |
       v
Diagnostic Evidence
       |
       v
LLM Client
(Fake Client / OpenAI Client)
       |
       v
Structured JSON Response
       |
       v
Incident Report Parser
       |
       v
Incident Report
       |
       v
Incident Evaluator
       |
       v
Evaluation Results
       |
       v
Reliability Metrics
```

The diagram represents the intended evaluation flow. The deterministic fake client is used for local testing; live model execution has not yet been verified.

## Technology Stack

- **Language:** Java 17
- **Build tool:** Maven
- **Testing:** JUnit 5
- **JSON processing:** Jackson
- **LLM integration:** OpenAI Java SDK with an injectable client interface
- **CI/CD:** GitHub Actions

## Project Structure

The repository contains a nested Maven project.

```text
agent-reliability-platform/
├── .github/
│   └── workflows/
│       └── maven.yml
├── agent-reliability-platform/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/agentreliability/
│       │   ├── IncidentInvestigationAgent.java
│       │   ├── IncidentReport.java
│       │   ├── IncidentReportParser.java
│       │   ├── IncidentEvaluator.java
│       │   ├── IncidentEvidence.java
│       │   ├── LlmClient.java
│       │   ├── FakeLlmClient.java
│       │   ├── OpenAiLlmClient.java
│       │   └── evaluation/
│       └── test/java/com/agentreliability/
└── README.md
```

Diagnostic tools and their supporting models reside in the `com.agentreliability.tools` package.

## Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.8 or later
- Git

### Run the tests

Clone the repository and enter the Maven project directory:

```bash
git clone https://github.com/prakhar-srivastava-2/agent-reliability-platform.git

cd agent-reliability-platform/agent-reliability-platform
```

Run the complete test suite:

```bash
mvn clean test
```

The current local test suite contains **97 passing tests**.

### Continuous Integration

GitHub Actions runs the Maven test suite automatically on pushes and pull requests targeting `master`.

The workflow configures Java 17, caches Maven dependencies, and executes:

```bash
mvn --batch-mode clean test
```

A failing test causes the CI job to fail.

## Structured Incident Reports

The agent consumes structured JSON responses that are parsed into an `IncidentReport`.

Example:

```json
{
  "conclusion": "Database timeouts may be causing elevated errors.",
  "finding": "SUPPORTS_ROOT_CAUSE",
  "claimedRootCause": "DATABASE_TIMEOUT",
  "rootCauseConfirmed": false
}
```

The report includes:

- `conclusion` — the investigation summary.
- `finding` — whether the evidence supports or contradicts a root-cause claim.
- `claimedRootCause` — the root cause asserted by the agent.
- `rootCauseConfirmed` — whether the report claims confirmation.

The parser validates the JSON structure and field types before creating the report. The evaluator then checks the report against the available incident evidence.

A model-generated claim is not automatically proof that a root cause is correct. Evaluation against evidence is a separate step.

## Reliability Evaluation

The evaluation layer is designed to assess agent behavior using known incident scenarios and expected evidence.

The current implementation includes:

- Validation of root-cause claims.
- Checks for agreement between claims and evidence.
- Checks for consistency in root-cause confirmation.
- Evaluation results aggregated into reliability metrics.
- Repeatable scenarios for regression testing.

These checks provide a deterministic basis for evaluating agent outputs rather than relying only on the quality or fluency of generated explanations.

## Testing Strategy

The test suite covers the diagnostic tools, agent behavior, structured response parsing, evaluator validation, and reliability metrics.

The fake LLM client enables repeatable tests without network calls or API credentials. It also allows tests to verify the prompt sent by the agent and control the response returned.

This approach makes local tests deterministic and suitable for automated CI execution.

## Current Limitations

- Live OpenAI API execution has not yet been verified.
- The current diagnostic data and evaluation scenarios are intended for deterministic testing rather than production monitoring.
- The platform evaluates the structured claims it receives; it does not guarantee that an LLM will always produce correct conclusions.
- Production integrations, live telemetry sources, and broader incident datasets would require additional implementation.

## Future Improvements

- Validate the complete agent flow against live LLM responses.
- Expand incident scenarios and evidence coverage.
- Add evaluation reports that make regressions easier to diagnose.
- Integrate additional telemetry providers through the diagnostic tool interface.
- Extend CI with optional evaluation gates for agent reliability regressions.

## Project Goal

The goal is to build a reusable, testable reliability layer for AI agents that investigate incidents—separating evidence collection, model-generated conclusions, structured output validation, and objective evaluation.

The project emphasizes deterministic testing, explicit contracts, and measurable behavior rather than treating a plausible AI response as a correct result.
