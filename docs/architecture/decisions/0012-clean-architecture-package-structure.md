# **ADR-0012: Clean Architecture Package Structure Standards**

**Date:** 2025-12-10  
**Status:** Accepted  
**Context:** Standardization of Java package structure across all modules using Clean Architecture principles

---

## **Context**

The codebase had inconsistent package structures across modules:
- Some services had REST resources at root level, others in sub-packages
- Inconsistent naming conventions (Resource vs Controller)
- Infrastructure concerns mixed with domain logic
- Duplicate DTOs across modules
- No clear separation between domain, infrastructure, and presentation layers

This inconsistency made the codebase harder to navigate, understand, and maintain. New developers had to learn different patterns for each module.

---

## **Decision**

We will adopt **Clean Architecture package structure standards** across all modules. All API and implementation modules must follow these standards.

### **Core Principles**

1. **Separation of Concerns**: Clear boundaries between domain, infrastructure, and presentation layers
2. **Consistent Structure**: Same package structure across all modules of the same type
3. **Naming Conventions**: Consistent naming patterns for interfaces, implementations, and utilities
4. **API/Implementation Separation**: Clear separation between API contracts (`forge-api`) and implementations (`forge-impl`)

---

## **Repository Structure**

Forge Kit is organized as a library/framework repository with two main module categories:

- **`forge-api/`**: API modules defining contracts, interfaces, annotations, and DTOs
- **`forge-impl/`**: Implementation modules providing concrete implementations

---

## **Package Structure Standards**

### **Base Package Pattern**

All modules follow the pattern: `io.forge.kit.{module}.{api|impl}.{layer}`

- `{module}`: The module name (e.g., `security`, `metrics`, `throttle`, `common`, `health`)
- `{api|impl}`: Either `api` for contracts or `impl` for implementations
- `{layer}`: The architectural layer (e.g., `domain`, `infrastructure`, `rest`, `dto`)

---

### **API Modules** (`forge-api/{module}-api`)

API modules define contracts, interfaces, annotations, and shared DTOs:

```
io.forge.kit.{module}.api/
├── domain/                               # Domain interfaces and contracts
│   ├── exception/                        # Domain exceptions
│   ├── support/                          # Support interfaces/utilities
│   └── [Domain interfaces]
├── dto/                                  # Data transfer objects
│   └── [DTOs]
├── infrastructure/                       # Infrastructure contracts
│   └── [Infrastructure interfaces]
├── rest/                                 # REST layer annotations
│   ├── exception/                        # Exception-related annotations
│   └── [REST annotations]
├── jwt/                                  # JWT-related contracts (if applicable)
│   └── [JWT interfaces]
├── key/                                  # Key resolution contracts (if applicable)
│   └── resolver/
│       └── [Key resolver interfaces]
├── faulttolerance/                       # Circuit breaker contracts (if applicable)
│   └── [Fault tolerance interfaces]
└── persistence/                          # Persistence contracts (if applicable)
    └── [Persistence interfaces]
```

### **Implementation Modules** (`forge-impl/{module}`)

Implementation modules provide concrete implementations of API contracts:

```
io.forge.kit.{module}.impl/
├── domain/                               # Domain implementations
│   ├── exception/                        # Exception mappers/handlers
│   ├── recorder/                         # Metrics recorders (if applicable)
│   ├── support/                          # Support implementations
│   └── [Domain implementations]
├── dto/                                  # Implementation-specific DTOs (if any)
│   └── [DTOs]
├── infrastructure/                       # Infrastructure implementations
│   ├── config/                           # Configuration producers
│   ├── [Infrastructure implementations]
│   └── [Sub-packages for specific concerns]
├── rest/                                 # REST layer implementations
│   ├── exception/                        # Exception mappers
│   └── [REST implementations]
├── jwt/                                  # JWT implementations (if applicable)
│   └── [JWT implementations]
├── key/                                  # Key resolution implementations (if applicable)
│   └── resolver/
│       └── [Key resolver implementations]
├── strategy/                             # Strategy implementations (if applicable)
│   └── [Strategy implementations]
├── faulttolerance/                       # Circuit breaker implementations (if applicable)
│   ├── recorder/
│   └── [Fault tolerance implementations]
├── persistence/                          # Persistence implementations (if applicable)
│   ├── recorder/
│   └── [Persistence implementations]
├── logging/                              # Logging implementations (if applicable)
│   └── [Logging implementations]
├── lang/                                 # Language utilities (if applicable)
│   └── [Language utilities]
├── inject/                               # CDI utilities (if applicable)
│   └── [CDI implementations]
├── reference/                            # Reference implementations/examples
│   └── [Reference code]
└── test/                                 # Test profiles and test utilities
    └── [Test profiles]
```

---

## **Naming Conventions**

### **Interfaces (API Modules)**

- Domain interfaces: Use descriptive names: `{Purpose}Provider.java`, `{Purpose}Validator.java`
  - Example: `TokenValidator`, `UserAuthenticationProvider`, `ServiceAuthenticationProvider`
- Infrastructure interfaces: Use `{Purpose}Interface.java` or descriptive names
  - Example: `RateLimiter`, `MetricsRecorder`

### **Implementations (Implementation Modules)**

- Use descriptive names matching the interface they implement
  - Example: `Bucket4jRateLimiter` (implements `RateLimiter`)
- Producers: Use `{Service}Producer.java`
  - Example: `Bucket4jRateLimiterProducer`, `RateLimiterPropertiesProducer`
- Resolvers: Use `{Purpose}Resolver.java`
  - Example: `AllowedServicesResolver`, `JwtPrincipalResolver`, `MetricsRecorderResolver`
- Interceptors: Use `{Purpose}Interceptor.java`
  - Example: `ServiceMetricsInterceptor`, `DatabaseMetricsInterceptor`
- Mappers: Use `{Entity}Mapper.java`
  - Example: `AuthenticationExceptionMapper`, `ValidationExceptionMapper`

### **Annotations (API Modules)**

- Use descriptive names without suffix
  - Example: `Secured`, `AllowedServices`, `LogMethodEntry`

### **DTOs**

- Use descriptive names: `{Purpose}Request.java`, `{Purpose}Response.java`, `{Entity}.java`
  - Example: `AuthResponse`, `TokenValidation`, `AuthIdentity`, `MetricsResultIndicator`

---

## **Layer Guidelines**

### **Domain Layer**

- **Purpose**: Business logic interfaces, domain models, and domain exceptions
- **Location**: `domain/` in both API and implementation modules
- **API modules**: Define interfaces and contracts
- **Implementation modules**: Provide concrete implementations

### **Infrastructure Layer**

- **Purpose**: External concerns (databases, external services, configuration)
- **Location**: `infrastructure/` in both API and implementation modules
- **API modules**: Define infrastructure contracts/interfaces
- **Implementation modules**: Provide concrete implementations (repositories, clients, health checks)

### **REST/Presentation Layer**

- **Purpose**: HTTP/REST concerns (annotations, exception mappers)
- **Location**: `rest/` in both API and implementation modules
- **API modules**: Define REST annotations
- **Implementation modules**: Provide exception mappers and REST utilities

### **DTO Layer**

- **Purpose**: Data transfer objects
- **Location**: `dto/` in API modules (shared DTOs) or implementation modules (implementation-specific DTOs)
- **Rule**: If a DTO is used across modules or exposed via API, it should be in the API module

---

## **Module-Specific Patterns**

### **Security Module**

- API: Domain interfaces (`TokenValidator`, `UserAuthenticationProvider`), DTOs, JWT contracts, REST annotations
- Implementation: JWT implementations, infrastructure resolvers, REST exception mappers

### **Metrics Module**

- API: Domain interfaces (`MetricsRecorder`), DTOs, fault tolerance contracts, persistence contracts
- Implementation: Interceptors, recorders, fault tolerance implementations, persistence implementations

### **Throttle Module**

- API: Infrastructure contracts (`RateLimiter`), key resolver interfaces
- Implementation: Rate limiter implementations, key resolver implementations, strategy implementations

### **Common Module**

- API: Common annotations (e.g., `LogMethodEntry`)
- Implementation: Utilities (logging, language, CDI, REST)

### **Health Module**

- Implementation: Health check implementations (no API module needed for simple health checks)

---

## **Examples**

### **Good Structure: Security Module**

**API** (`forge-api/forge-security-api`):
```
io.forge.kit.security.api/
├── domain/
│   ├── exception/
│   │   └── AuthenticationException.java
│   ├── TokenValidator.java
│   ├── ServiceAuthenticationProvider.java
│   └── UserAuthenticationProvider.java
├── dto/
│   ├── AuthIdentity.java
│   ├── AuthResponse.java
│   └── TokenValidation.java
├── jwt/
│   ├── JwtPrincipal.java
│   └── JwtPrincipalExtractor.java
└── rest/
    ├── AllowedServices.java
    └── Secured.java
```

**Implementation** (`forge-impl/forge-security`):
```
io.forge.kit.security.impl/
├── infrastructure/
│   └── AllowedServicesResolver.java
├── jwt/
│   ├── JwtPayloadParser.java
│   ├── JwtPrincipalResolver.java
│   ├── JwtServicePrincipalAccessor.java
│   ├── JwtTokenExtractor.java
│   └── JwtUserPrincipalAccessor.java
└── rest/
    └── exception/
        └── AuthenticationExceptionMapper.java
```

---

## **Consequences**

**Positive:**

- **Consistency**: Same structure across all modules makes codebase predictable
- **Maintainability**: Clear separation of concerns reduces cognitive load
- **Onboarding**: New developers learn one pattern, not many
- **Testability**: Clean separation enables easier unit and integration testing
- **Scalability**: Structure supports growth without refactoring
- **API/Implementation Clarity**: Clear separation between contracts and implementations

---

## **Implementation**

### **New Modules**

All new modules must follow these standards from creation.

### **Existing Modules**

Existing modules should be refactored to follow these standards when:
- Major feature work is being done
- Significant refactoring is already planned
- Code review identifies structure issues

**Note**: Not all modules need immediate refactoring. Standards apply to new code and major changes.

---

## **Validation**

Code reviews should verify:
- API contracts are in `forge-api/{module}-api`
- Implementations are in `forge-impl/{module}`
- Domain logic is in `domain/`
- Infrastructure concerns are in `infrastructure/`
- REST concerns are in `rest/`
- DTOs are in `dto/` and placed appropriately (API vs implementation)
- Naming conventions are followed
- Package structure follows the pattern: `io.forge.kit.{module}.{api|impl}.{layer}`

---

## **Deviations and Notes**

The current structure generally follows these standards. Key observations:

1. ✅ **API/Implementation Separation**: Clear separation between `forge-api` and `forge-impl` modules
2. ✅ **Layer Organization**: Consistent use of `domain/`, `infrastructure/`, `rest/`, `dto/` layers
3. ✅ **Package Naming**: Consistent `io.forge.kit.{module}.{api|impl}.{layer}` pattern
4. ✅ **Module-Specific Layers**: Appropriate use of specialized layers (`jwt/`, `key/`, `faulttolerance/`, `persistence/`)

**Minor Observations:**

- Some modules use `reference/` for reference implementations (e.g., `forge-throttle`) - this is acceptable for examples
- Some modules use `test/` for test profiles (e.g., `forge-throttle`) - this is acceptable for test utilities
- The `forge-common-api` module is minimal (only `logging/LogMethodEntry.java`) - this is acceptable for simple annotation-only modules

---

**Decision Owner:** Architecture Team

**Review Cycle:** Review annually or when new module types are introduced

---
