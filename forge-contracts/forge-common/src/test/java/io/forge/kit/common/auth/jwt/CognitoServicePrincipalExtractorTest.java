package io.forge.kit.common.auth.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CognitoServicePrincipalExtractorTest
{
    private final CognitoServicePrincipalExtractor extractor = new CognitoServicePrincipalExtractor();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Extracts service principal when custom:service_id field is present")
    void extractsServicePrincipalWhenServiceIdFieldPresent()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("custom:service_id", "service-123");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertInstanceOf(JwtPrincipal.Service.class, result.get());
        assertEquals("service-123", ((JwtPrincipal.Service) result.get()).serviceId());
        assertEquals("service:service-123", result.get().rateLimitKey());
    }

    @Test
    @DisplayName("Returns empty when custom:service_id field is missing")
    void returnsEmptyWhenServiceIdFieldMissing()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("email", "user@example.com");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Returns empty when custom:service_id field is null")
    void returnsEmptyWhenServiceIdFieldIsNull()
    {
        final JsonNode payload = mapper.createObjectNode()
            .putNull("custom:service_id");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Returns empty when custom:service_id field is blank")
    void returnsEmptyWhenServiceIdFieldIsBlank()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("custom:service_id", "");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Returns empty when custom:service_id field is whitespace only")
    void returnsEmptyWhenServiceIdFieldIsWhitespaceOnly()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("custom:service_id", "   ");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Returns empty when custom:service_id field is not textual")
    void returnsEmptyWhenServiceIdFieldIsNotTextual()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("custom:service_id", 123);

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Extracts service principal with complex service ID")
    void extractsServicePrincipalWithComplexServiceId()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("custom:service_id", "my-service-v2.1.0");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertInstanceOf(JwtPrincipal.Service.class, result.get());
        assertEquals("my-service-v2.1.0", ((JwtPrincipal.Service) result.get()).serviceId());
        assertEquals("service:my-service-v2.1.0", result.get().rateLimitKey());
    }

    @Test
    @DisplayName("Returns empty when payload is empty object")
    void returnsEmptyWhenPayloadIsEmptyObject()
    {
        final JsonNode payload = mapper.createObjectNode();

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }
}
