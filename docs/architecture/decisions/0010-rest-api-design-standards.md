# **ADR-0010: REST API Design Standards**

**Date:** 2025-12-10
**Status:** Accepted
**Context:** Standardization of REST API endpoint design across all microservices

---

## **Context**

The codebase had inconsistent REST API endpoint patterns across services:

* **Action-based URLs** (e.g., `/parse/document`) instead of resource-based
* **Inconsistent naming** (singular vs plural, verbs vs nouns)
* **Inconsistent ID usage** (email vs UUID, path params vs query params)
* **Security issues** (sensitive data in GET query parameters)
* **Non-standard HTTP methods** (using POST for actions that should be GET with query params)
* **Inconsistent status codes** (not using 201 CREATED for resource creation)

This inconsistency made the API harder to understand, maintain, and extend. It also violated REST principles and industry best practices.

---

## **Decision**

We will adopt **RESTful API design standards** based on RFC 7231, RESTful Web Services principles, and
industry best practices. All endpoints across all services must follow these standards:

### **Core Principles**

1. **Resource-Based URLs**: Use nouns, not verbs. Resources represent entities, not actions.
   - ✅ `/entities`, `/documents`, `/resources`
   - ❌ `/parse/document`, `/admin/toggle`

2. **Plural Nouns**: Use plural nouns for resource collections.
   - ✅ `/entities`, `/documents`
   - ❌ `/entity`, `/document`

3. **HTTP Methods**: Use HTTP methods to indicate actions.
   - **GET**: Retrieve resources (idempotent, safe)
   - **POST**: Create resources or perform actions (not idempotent)
   - **PUT**: Replace entire resource (idempotent)
   - **PATCH**: Partial update (idempotent)
   - **DELETE**: Remove resource (idempotent)

4. **Query Parameters for Filtering**: Use query parameters for filtering, searching, and pagination.
   - ✅ `GET /entities?entityId={id}`
   - ❌ `GET /entity/{email}/entities`

5. **Path Parameters for Identification**: Use path parameters for resource identification.
   - ✅ `GET /entities/{entityId}`
   - ❌ `GET /entities?entityId={id}` (for single resource)

6. **Consistent ID Usage**: Use UUIDs consistently in path parameters. Use query parameters for filtering by other attributes.
   - ✅ `GET /entities?entityId={uuid}`
   - ❌ `GET /entity/{email}/entities`

7. **Proper Status Codes**:
   - `200 OK` - Success
   - `201 Created` - Resource created successfully
   - `204 No Content` - Success with no response body
   - `400 Bad Request` - Validation errors
   - `401 Unauthorized` - Authentication required
   - `404 Not Found` - Resource not found
   - `422 Unprocessable Entity` - Validation errors (more specific than 400)
   - `500 Internal Server Error` - Server errors

8. **Security Best Practices**:
   - Never put sensitive data in URL query parameters (use POST with body)
   - Use POST for token exchange and validation operations
   - ✅ `POST /auth/tokens/exchange` with token in body
   - ❌ `GET /auth/token/exchange?token=xxx`

### **Endpoint Structure Examples**

#### **Resource Creation**
```java
@POST
@Path("/documents")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public Response createDocument(@RestForm String entityId, @RestForm FileUpload document) {
    // ... create logic
    return Response.status(Response.Status.CREATED).entity(result).build();
}
```

#### **Resource Retrieval with Filtering**
```java
@GET
@Path("/entities")
public Response getEntities(
    @QueryParam("entityId") String entityId) {
    // ... query logic
    return Response.ok(entities).build();
}
```

#### **Resource Update**
```java
@PUT
@Path("/admin/features/{featureName}")
@Consumes(MediaType.APPLICATION_JSON)
public Response updateFeature(
    @PathParam("featureName") String featureName,
    Map<String, Object> request) {
    // ... update logic
    return Response.ok(result).build();
}
```

### **Special Cases**

**Authentication Endpoints**: Authentication endpoints are an exception where action-based URLs are
acceptable, as they represent authentication operations, not resource operations.
- ✅ `POST /auth/login`
- ✅ `POST /auth/register`
- ✅ `POST /auth/tokens/refresh`

---

## **Consequences**

**Positive:**

* **Consistency**: All services follow the same patterns, making the API predictable and easier to learn
* **Maintainability**: Standard patterns reduce cognitive load and make code reviews easier
* **Scalability**: Resource-based design supports future HATEOAS implementation
* **Security**: Proper use of HTTP methods and status codes improves security posture
* **Developer Experience**: Clear patterns reduce decision fatigue and speed up development
* **Industry Alignment**: Follows widely-accepted REST principles and best practices


---

## **References**

* RFC 7231: Hypertext Transfer Protocol (HTTP/1.1): Semantics and Content
* RESTful Web Services by Leonard Richardson and Sam Ruby
* REST API Tutorial: https://restfulapi.net/
* Microsoft REST API Guidelines: https://github.com/microsoft/api-guidelines
* Google Cloud API Design Guide: https://cloud.google.com/apis/design

---

**Decision Owner:** Architecture Team

**Review Cycle:** Review annually or when significant new requirements emerge (e.g., HATEOAS implementation)

---
