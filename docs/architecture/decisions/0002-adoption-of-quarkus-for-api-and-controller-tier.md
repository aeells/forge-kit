# **ADR-0002: Adoption of Quarkus for API and Controller Tier**

**Date:** 2025-10-08
**Status:** Accepted
**Context:** Early-stage architecture decision for core application backend

---

## **Context**

We require a performant, scalable, and cloud-native framework for building our application's API and controller tiers. The solution should:

* Support both RESTful APIs and server-rendered web controllers.
* Integrate cleanly with cloud-managed services (compute, storage, secrets).
* Allow for efficient local development with minimal friction.
* Scale efficiently and cost-effectively in production, ideally using containerized workloads.
* Offer a modern developer experience while allowing Java-based reliability and ecosystem maturity.

Previous experience with **Node.js frameworks** highlighted challenges with dependency management, runtime
performance, and long-term maintainability. The team also has prior expertise in **Java and microservice frameworks**,
prompting an evaluation of modern JVM-based microservice frameworks.

---

## **Decision**

We will adopt **Quarkus** as the primary framework for implementing both the **API layer** and the **controller (web) tier**.

Quarkus is selected for the following reasons:

* **Performance:** Native-image support via **GraalVM** enables sub-second startup and low memory use
  — ideal for containerized, on-demand environments.
* **Developer Experience:** "Dev mode" offers hot reload and fast feedback during development, while container builds maintain production parity.
* **Cloud-Native Alignment:** Quarkus is designed as *Kubernetes-native* and integrates smoothly with
  container orchestration and observability tooling.
* **Flexibility:** Supports both RESTEasy (JAX-RS) and templated MVC endpoints (Qute), covering both API and lightweight web controller needs.
* **Ecosystem:** Excellent support for Jakarta EE, MicroProfile, Hibernate ORM, and reactive extensions.

---

## **Database Choice**

Quarkus supports any JDBC-compatible database. PostgreSQL is the default in most examples and aligns well with cloud offerings.

### **Production Environment**

* **Primary Option:** **Managed PostgreSQL (Serverless)**

  * Managed, scalable, PostgreSQL-compatible engine.
  * Integrates seamlessly with Quarkus via standard JDBC driver.
  * Optionally fronted by **connection proxy** for improved connection management.
* **Alternative:** **Managed PostgreSQL** (simpler, single-tenant managed option).

### **Local Development**

* **Local Option:** **PostgreSQL in Docker**

  * Mirrors production schema and configuration.
  * Supports simple connection via environment variables.
  * Managed via Docker Compose or container tooling.

---

## **Cloud Deployment Considerations**

* **Compute:** Deploy Quarkus applications as containers to **container orchestration platforms**.

  * Container orchestration selected for simplicity, reduced operational overhead, and native cloud integration.
* **Containerization:**

  * JVM-based builds for local and test environments.
  * **GraalVM native-image** builds for production (smaller, faster, lower cost).
* **Configuration & Secrets:** Managed via **secrets management services**, injected as environment variables.
* **Observability:** Metrics and logs exposed via **Micrometer/OpenTelemetry**, integrated with **monitoring services**.

---

## **Local Environment Considerations**

* **Development Mode:**

  * Run `./mvnw quarkus:dev` for rapid iteration and hot reload.
  * No container overhead during active development.
* **Container Testing:**

  * Use **Docker** for local container builds and integration testing.
  * Configuration parity maintained with production deployment environment.
* **Database:**

  * Local PostgreSQL container mimics production setup.
  * Environment variables managed via `.env` or Compose.

---

## **Consequences**

**Positive:**

* High performance and fast startup enable efficient scaling in containerized environments.
* Strong local developer experience and minimal divergence between dev/test/prod.
* Modern, well-supported Java ecosystem with strong community and documentation.

**Negative / Tradeoffs:**

* GraalVM native-image builds increase build complexity and duration.
* Smaller developer community than Spring Boot, particularly for advanced enterprise integrations.
* Some enterprise features (e.g. full Spring ecosystem integrations, legacy connectors) may require
  additional libraries or manual configuration later.

---

**Decision Owner:** Architecture Team

**Review Cycle:** Reassess in 6–12 months, particularly if enterprise or multi-service orchestration requirements evolve.

---
