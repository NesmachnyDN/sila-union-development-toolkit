# SILA Union Development Toolkit

[![CI](https://github.com/NesmachnyDN/sila-union-development-toolkit/actions/workflows/ci.yml/badge.svg)](https://github.com/NesmachnyDN/sila-union-development-toolkit/actions/workflows/ci.yml)

A small, **mock-first** Java toolkit that demonstrates how enterprise-architecture repository automation can be designed, tested, and evolved without requiring access to a live SILA Union installation.

The repository is a portfolio implementation. Its focus is architecture and testability: repository-facing logic is isolated behind a port, while all examples and unit tests use an in-memory stub with synthetic data.

## What problem it demonstrates

Repository automation scripts are often difficult to develop when business logic is coupled directly to a vendor runtime, credentials, a specific repository, or server-side execution environment. This project separates automation use cases from repository connectivity so that most behavior can be developed and verified locally.

```text
Automation use cases
        |
        v
SilaRepositoryClient  <-- application port
        |
        +--------------------------+
        |                          |
        v                          v
InMemorySilaRepositoryStub   Future real adapter
(current demo/tests)         (not included)
```

## Included use cases

### Idempotent glossary import

`GlossaryImportService` reads synthetic semicolon-separated glossary data and upserts normalized glossary elements. Re-running the same input is idempotent; changed definitions are reported as updates and malformed rows are rejected.

### Architecture snapshot report

`ArchitectureReportService` reads elements and relations through the repository port and produces a deterministic Markdown architecture report containing inventory, application components, and relations.

## Technology stack

- Java 21
- Maven 3.9+
- JUnit 6
- JaCoCo coverage report
- GitHub Actions
- Dependabot for Maven and GitHub Actions dependencies

The production source set has no third-party runtime dependencies.

## Run locally

Requirements: JDK 21 and Maven 3.9+.

```bash
mvn clean verify
java -jar target/sila-union-development-toolkit-1.0.0-SNAPSHOT.jar
```

If Maven is not available, a dependency-free smoke test can still compile and run the main source set:

```bash
./scripts/smoke-test.sh
```

No SILA Union endpoint, credentials, repository ID, VPN, or corporate network is required.

## Tests and CI

Every push to `main` and every pull request runs `.github/workflows/ci.yml`. The workflow:

1. checks the repository for a small deny-list of accidental internal markers, secret-like tokens, private IP addresses, and local credential files;
2. installs Eclipse Temurin JDK 21;
3. runs `mvn verify`, including unit tests and JaCoCo report generation.

The public-safety script is an additional guardrail, not a substitute for GitHub secret scanning or manual review.

## Repository structure

```text
src/main/java/io/github/nesmachnydn/sila/
  app/       dependency-free demo application
  domain/    small vendor-neutral domain model
  port/      repository boundary used by application services
  service/   automation use cases
  stub/      deterministic in-memory SILA repository test double

src/test/    unit tests; all repository interaction is stubbed
scripts/     local smoke and public-safety checks
docs/        architecture and origin notes
```

## My role

Architecture, problem decomposition, acceptance criteria, public-safety constraints, technical review, and repository positioning are human-directed. Implementation and documentation were produced with AI-assisted development and reviewed against those constraints.

## Origin and data notice

This repository is an **independent portfolio implementation** based on a generalized problem: making enterprise-architecture repository automation locally testable. It does not contain source history, source files, credentials, endpoint addresses, repository identifiers, corporate documents, configuration, screenshots, or business data from an internal corporate prototype.

All examples, identifiers, architecture elements, glossary terms, and relations in this repository are synthetic.

See [ORIGIN.md](ORIGIN.md) and [SECURITY.md](SECURITY.md).

## SILA Union integration status

A real SILA Union network adapter is intentionally **not included**. Adding one would require relying only on publicly distributable vendor API contracts and dependencies. The current project demonstrates the application boundary and automation behavior using a deterministic stub.

SILA Union is referenced only to describe the intended integration context. This repository is independent and is not an official vendor project.

## License

No open-source license is granted at this stage. The repository is published as a portfolio and technical demonstration. A license can be added later if redistribution is desired.
