# forge-security

## Overview

`forge-security` provides opinionated, reusable security primitives for Java / Quarkus services
operating in a zero-trust environment. It focuses on **request-level authentication and
enforcement**, **identity propagation**, and **defensive defaults** for microservices, without
coupling to a specific identity provider.

---

## Key Features

- Request authentication enforcement (fail-closed by default)
- Dual authentication support: user and service-to-service identities side by side
- Standardised security context propagation across service boundaries
- Clear separation of authentication, authorization, and transport concerns
- Integration points for external identity providers (e.g., Cognito, OAuth2, JWT)

---

## Design Principles

- **Zero trust**: no implicit trust between services or network boundaries
- **Statelessness**: no server-side sessions or affinity assumptions
- **Dual authentication support**: user and service-to-service identities run side by side
- **Explicit enforcement**: security is applied intentionally, not implicitly
- **Provider-agnostic**: identity sources are pluggable, not hard-coded
- **Fail closed**: unauthenticated or malformed requests are rejected early

---

## Typical Use Cases

- Enforcing authentication on all inbound requests
- Propagating identity across service boundaries
- Service-to-service authorization checks
- Defensive request handling in zero-trust environments

---

## Usage

`forge-security` provides annotations (`@Secured` and `@AllowedServices`) that act as interceptor
bindings for authentication and authorization. **Concrete authentication implementations (filters
and interceptors) must be supplied by the consuming application.**

The annotations work as follows:
- `@Secured` - Marks endpoints that require user authentication
- `@AllowedServices` - Restricts endpoints to specific service identities (used alongside `@Secured`)

### Basic Usage

Simply annotate your JAX-RS resource methods or classes:

```java
@Path("/api/data")
public class DataResource {
    
    @GET
    @Secured
    public Response getData() {
        // Requires user authentication
        return Response.ok().build();
    }
    
    @POST
    @Secured
    @AllowedServices({"processing-service"})
    public Response processData(DataRequest request) {
        // Requires user authentication AND service authorization
        // Only "processing-service" can call this endpoint
        return Response.ok().build();
    }
}
```

**Important:** The annotations are interceptor bindings. Your application must implement:
- An authentication filter/interceptor that validates tokens and sets security context
- An authorization interceptor that enforces `@Secured` and `@AllowedServices` annotations

---

## Examples

See: [examples/forge-security](../../examples/forge-security)

Code examples demonstrate:
- Using `@Secured` annotation for user authentication
- Using `@AllowedServices` annotation for service-level authorization
- Combining both annotations for dual authentication
- Applying annotations at method and class levels

**Note:** There is also a `ReferenceTestResource` in the throttle module used for integration
testing, but it uses `@Secured` as a semantic marker only (the annotation is a no-op in that
context). For a complete example of security annotation usage, see `SecuredExample` in the
examples directory.

---

## Relationship to Forge Platform

`forge-security` exposes shared security building blocks used by Forge-based services.

The Forge Platform extends this with:
- Managed identity providers
- Token exchange and lifecycle management
- End-to-end service authentication flows
- Centralized policy and enforcement configuration
