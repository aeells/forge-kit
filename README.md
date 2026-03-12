# Forge Kit

[![Lines of Code](https://tokei.kojix2.net/badge/github/aeells/forge-kit/lines)](https://tokei.kojix2.net/github/aeells/forge-kit)
[![Top Language](https://tokei.kojix2.net/badge/github/aeells/forge-kit/language)](https://tokei.kojix2.net/github/aeells/forge-kit)
[![Code to Comment](https://tokei.kojix2.net/badge/github/aeells/forge-kit/ratio)](https://tokei.kojix2.net/github/aeells/forge-kit)
![License](https://img.shields.io/github/license/aeells/forge-kit)
![Last Commit](https://img.shields.io/github/last-commit/aeells/forge-kit)

[![Quarkus](https://img.shields.io/badge/Quarkus-v3.31.1-blue?logo=quarkus)](https://quarkus.io/)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)

**CI Status**

[![00 🧩 Hygiene checks](https://github.com/aeells/forge-kit/actions/workflows/00-hygiene-check.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/00-hygiene-check.yml) [![01 🚧 Build and test](https://github.com/aeells/forge-kit/actions/workflows/01-build-test.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/01-build-test.yml) [![02 🔎 Static analysis](https://github.com/aeells/forge-kit/actions/workflows/02-static-analysis.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/02-static-analysis.yml)  
[![03 👊🏽 Auto version bump](https://github.com/aeells/forge-kit/actions/workflows/03-release-bump.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/03-release-bump.yml) [![04 📦 Publish packages](https://github.com/aeells/forge-kit/actions/workflows/04-publish-packages.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/04-publish-packages.yml) [![51 🔎 Code coverage](https://github.com/aeells/forge-kit/actions/workflows/51-code-coverage.yml/badge.svg)](https://github.com/aeells/forge-kit/actions/workflows/51-code-coverage.yml)  

[![codecov](https://codecov.io/github/aeells/forge-kit/graph/badge.svg?token=RP8Z2NWG9L)](https://codecov.io/github/aeells/forge-kit)

---

## Overview

**Forge Kit** is a collection of infrastructure components extracted directly from the 👉 **[Forge Platform](https://forgeplatform.software)**.

It provides Quarkus-based components for security, observability, and cross-cutting service concerns — without prescribing domain logic.

Forge Kit demonstrates the following capabilities that continue with the full Forge Platform: 
- architectural composure and separation of concerns
- implementation of microservice cross-cutting concerns
- disciplined coding standards, documentation, and test organisation

---

## What Forge Kit Provides

### 🔒 Security & Protection

- **Rate Limiting & Throttling** (`forge-throttle`)
  - Deterministic rate limiting with clear separation of authenticated vs. unauthenticated capacity.
  - [Documentation →](forge-impl/forge-throttle/README.md)

### 📈 Observability

- **Metrics Framework** (`forge-metrics`)
  - Micrometer integration with Prometheus-ready metrics.
  - Service, circuit breaker, and database performance recorders
  - [Documentation →](forge-impl/forge-metrics/README.md)

- **Health Checks** (`forge-health-aws`)
  - Liveness and readiness probe support for AWS environments.
  - [Documentation →](forge-impl/forge-health-aws/README.md)

### 🧱 Platform Utilities

- **Common Utilities** (`forge-common`)
  - Validation, error handling, and method entry logging primitives.
  - [Documentation →](forge-impl/forge-common/README.md)

Each module is independently usable and documented.

---

## Getting Started

Add the required modules to your project:

```xml
<dependency>
  <groupId>io.forge</groupId>
  <artifactId>forge-throttle</artifactId>
  <version>1.0.5</version>
</dependency>
```

Each module includes focused documentation and examples.

---

**📚 Documentation**
- [Examples →](examples/)
- [Templates →](templates/)
- [Architecture Decision Records →](docs/architecture/decisions/)
- [Code Quality & CI Enforcement](docs/CODE_QUALITY.md)

---

## Relationship to the Forge Platform

Forge Kit contains the cross-cutting infrastructure primitives used by the full Forge Platform.

The Forge Platform extends these foundations with domain services, identity flows, notifications, audit capabilities, and operational tooling.

If your goal is rapid delivery of production-ready microservices, the full platform may be the better starting point.

[![Lines of Code](https://tokei.kojix2.net/badge/github/aeells/forge-core/lines)](https://tokei.kojix2.net/github/aeells/forge-core)
[![Top Language](https://tokei.kojix2.net/badge/github/aeells/forge-core/language)](https://tokei.kojix2.net/github/aeells/forge-core)
[![Code to Comment](https://tokei.kojix2.net/badge/github/aeells/forge-core/ratio)](https://tokei.kojix2.net/github/aeells/forge-core)
![License](https://img.shields.io/github/license/aeells/forge-core)
![Last Commit](https://img.shields.io/github/last-commit/aeells/forge-core)

---

## Support

Forge Kit is open-source and community-supported.

For organisations seeking platform-level support or architectural consulting,
see 👉 [Forge Platform](https://forgeplatform.software).

---

## License

Forge Kit is licensed under the [MIT License](LICENSE).
