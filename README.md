# Forge Kit

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Quarkus](https://img.shields.io/badge/Quarkus-v3.30.5-blue?logo=quarkus)](https://quarkus.io/)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)

**CI/CD Workflows for Forge Kit**

[![00 🧩 Hygiene checks](https://github.com/aeells/forge-kit/actions/workflows/00-hygiene-check.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/00-hygiene-check.yml)  
[![01 🚧 Build and test](https://github.com/aeells/forge-kit/actions/workflows/01-build-test.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/01-build-test.yml)  
[![02 🔎 Static analysis](https://github.com/aeells/forge-kit/actions/workflows/02-static-analysis.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/02-static-analysis.yml)  
[![03 👊🏽 Auto version bump](https://github.com/aeells/forge-kit/actions/workflows/03-release-bump.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/03-release-bump.yml)  
[![04 📦 Publish packages](https://github.com/aeells/forge-kit/actions/workflows/04-publish-packages.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/04-publish-packages.yml)  
[![51 🔎 Code coverage](https://github.com/aeells/forge-kit/actions/workflows/51-code-coverage.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/51-code-coverage.yml)  
[![codecov](https://codecov.io/github/aeells/forge-kit/graph/badge.svg?token=RP8Z2NWG9L)](https://codecov.io/github/aeells/forge-kit)

---

## Overview

**Forge Kit** is a collection of production-grade infrastructure components extracted directly from the **Forge Platform**—battle-tested building blocks for building secure, observable, and resilient backend services.

Forge Kit focuses on **horizontal concerns**—the infrastructure every serious service needs—without prescribing business logic, workflows, or domain models.

Forge Kit is intentionally opinionated where it matters (security, observability, testability) and deliberately unopinionated where it should be.

---

## What Forge Kit Provides

### 🔒 Security & Protection

- **Rate Limiting & Throttling** (`forge-throttle`)
  - Deterministic rate limiting (Bucket4j-based)
  - Clear separation of authenticated vs unauthenticated capacity
  - Zero-trust–friendly request enforcement patterns
  - [Documentation →](forge-impl/forge-throttle/README.md)

### 📈 Observability

- **Metrics Framework** (`forge-metrics`)
  - Micrometer integration
  - Prometheus-ready metrics
  - Service, circuit breaker, database recorders
  - [Documentation →](forge-impl/forge-metrics/README.md)

- **Health Checks** (`forge-health`)
  - Production-ready health checks 
  - Liveness and readiness probes
  - [Documentation →](forge-impl/forge-health-aws/README.md)

### 🧱 Platform Utilities

- **Common Utilities** (`forge-common`)
  - Validation and error-handling primitives
  - Method entry logging
  - [Documentation →](forge-impl/forge-common/README.md)

Each module is independently usable and documented.

---

## What Forge Kit Is Not

Forge Kit is **not a full platform**. It does not include:

- ❌ Business workflows or domain logic
- ❌ Service orchestration
- ❌ Multi-tenant enforcement
- ❌ Authentication providers or identity flows
- ❌ Data persistence strategies
- ❌ SaaS lifecycle concerns
- ❌ Operational dashboards or SLO tooling

**Forge Kit solves *how* to build reliable services—not *what* those services do.**

---

## Why Forge Kit Exists

Teams repeatedly re-implement foundational infrastructure with small variations, accumulating hidden risk that only surfaces under load or failure.

Forge Kit distills:
- ✅ Real-world operational experience
- ✅ Proven architectural patterns
- ✅ Sensible defaults with explicit escape hatches

The result is **infrastructure code you can trust, understand, and extend**.

---

## Repository Structure

```
forge-kit/
├── forge-api/              # Annotations and contracts
    ├── forge-common-api/
    ├── forge-metrics-api/
    ├── forge-security-api/ 
    ├── forge-throttle-api/ 
├── forge-impl/             # Implementations
    ├── forge-common/           # Common utilities and helpers
    ├── forge-health/           # Health check implementations
    ├── forge-metrics/          # Metrics and observability
    ├── forge-security/         # Metrics and observability
    ├── forge-throttle/         # Rate limiting and throttling
├── examples/               # Usage examples and patterns
├── templates/              # Configuration templates
└── docs/                   # Architecture Decision Records (ADRs)
```

---

## Getting Started

Add the required modules to your project:

```xml
<dependency>
  <groupId>io.forge</groupId>
  <artifactId>forge-throttle</artifactId>
  <version>1.0.0</version>
</dependency>
```

Each module includes focused documentation and examples.

**📚 Documentation**
- [Examples →](examples/)
- [Templates →](templates/)
- [Architecture Decision Records →](docs/architecture/decisions/)

---

## Quality Gates

Forge Kit enforces strict quality gates in CI to ensure production readiness:

- Static analysis (PMD, SpotBugs, OWASP Dependency Check)
- Test coverage via [OpenClover](https://openclover.org/)
- Deterministic [unit and integration tests](https://github.com/aeells/forge-kit/actions/workflows/01-build-test.yml)
- Conventional commits and semantic versioning via [Commitizen](https://commitizen-tools.github.io/commitizen/)

All checks must pass before release artifacts are published.

📄 See [Code Quality & CI Enforcement](docs/CODE_QUALITY.md) for full details.

---

## Relationship to the Forge Platform

Forge Kit is a foundational **subset** of the Forge Platform.

The **Forge Platform** builds on these same components to provide a fully integrated system, including:
- Service orchestration across multiple domains
- Zero-trust service-to-service security
- Multi-tenant SaaS architecture
- Authentication and authorization flows
- API gateways by user persona
- Operational tooling and production workflows

In short:

| Feature | Forge Kit | Forge Platform |
|---------|-----------|----------------|
| Infrastructure primitives | ✅ | ✅ |
| Domain services | ❌ | ✅ |
| Multi-tenant SaaS | ❌ | ✅ |
| Security orchestration | ❌ | ✅ |
| Operational maturity | ❌ | ✅ |
| Commercial support | ❌ | ✅ |

**Forge Kit answers**: *"How should we build this correctly?"*  
**Forge Platform answers**: *"Do we need to build this at all?"*

---

## Architecture Decision Records (ADRs)

📘 [View Architecture Decision Records](docs/architecture/decisions/index.md)

---

## Who Should Use Forge Kit

Forge Kit is a good fit if you:
- Are building backend services from scratch
- Want production-grade infrastructure without vendor lock-in (excluding the [forge-health-aws](forge-impl/forge-health-aws/README.md) module)
- Prefer explicit, readable architecture over hidden framework behavior
- Value testability and operational clarity

If your primary goal is **time-to-market** rather than infrastructure ownership 👉 **[Forge Platform](https://forge-platform.com)**  may be a better fit.

---

## Support & Commercial Offering

**Forge Kit** is open-source and community-supported.

For teams looking to accelerate delivery with a fully integrated platform built on these same foundations, learn more about the 👉 **[Forge Platform](https://forge-platform.com)**.

**Commercial inquiries and architecture discussions:**

**Andrew Eells**  
**Forge Platform**  
[Contact Information]

---

## License

Forge Kit is licensed under the [MIT License](LICENSE).

---

**Built with ❤️ by the Forge Platform team**
