# forge-health-aws

## Overview

`forge-health-aws` provides **AWS health checks** for use with AWS services such as S3, DynamoDb.

It distinguishes between **liveness** and **readiness** and supports dependency-aware checks.

This is the only module that is not vendor-agnostic and relies on AWS SDK.

---

## Key Features

- Liveness and readiness probes
- Dependency health checks
- Quarkus-native integration
- Clear separation between critical and non-critical dependencies

---

## Design Principles

- Health checks must be fast and reliable
- Readiness reflects real operational capability
- Failing dependencies should be explicit and observable

---

## Typical Use Cases

- Kubernetes liveness/readiness probes
- Dependency monitoring
- Controlled service startup and shutdown

---

## Usage

`forge-health-aws` provides abstract base classes for health checks. To use them, create `@Produces` methods that return health check instances:

### PostgreSQL Health Check

```java
@Produces
@Readiness
@ApplicationScoped
public HealthCheck databaseHealthCheck(EntityManager entityManager) {
    return new PostgresHealthCheck(
        entityManager,
        "mydb",  // Database name
        List.of("users", "orders")  // Tables to check
    ) {};
}
```

### DynamoDB Health Check

```java
@Produces
@Readiness
@ApplicationScoped
public HealthCheck dynamoDbHealthCheck(DynamoDbClient dynamoDbClient) {
    return new DynamoDbHealthCheck(
        dynamoDbClient,
        List.of("Users", "Orders")  // Tables to check
    ) {};
}
```

### S3 Health Check

```java
@Produces
@Readiness
@ApplicationScoped
public HealthCheck s3HealthCheck(S3Client s3Client) {
    return new S3HealthCheck(
        s3Client,
        List.of("my-bucket", "backup-bucket")  // Buckets to check
    ) {};
}
```

### Cognito Health Check

```java
@Produces
@Readiness
@ApplicationScoped
public HealthCheck cognitoHealthCheck(CognitoIdentityProviderClient cognitoClient) {
    return new CognitoHealthCheck(
        cognitoClient,
        "us-east-1_ABC123",  // User Pool ID
        "MyUserPool"  // User Pool name
    ) {};
}
```

Once registered, health checks are automatically available at:
- `/q/health/ready` - Readiness checks (includes all `@Readiness` checks)
- `/q/health/live` - Liveness checks

---

## Examples

See: [examples/forge-health-aws](../../examples/forge-health-aws)

Code examples demonstrate:
- Creating health check producers for PostgreSQL, DynamoDB, S3, and Cognito
- Using `@Produces` and `@Readiness` annotations
- Configuring health checks for multiple resources
- Health check response format

---
