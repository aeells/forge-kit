package io.forge.kit.common.auth.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OidcUserPrincipalExtractorTest
{
    private final OidcUserPrincipalExtractor extractor = new OidcUserPrincipalExtractor();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Extracts user principal from cognito:username field")
    void extractsUserPrincipalFromCognitoUsername()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("cognito:username", "user@example.com");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertInstanceOf(JwtPrincipal.User.class, result.get());
        assertEquals("user@example.com", ((JwtPrincipal.User) result.get()).username());
        assertEquals("user:user@example.com", result.get().rateLimitKey());
    }

    @Test
    @DisplayName("Extracts user principal from email field when cognito:username is missing")
    void extractsUserPrincipalFromEmail()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("email", "user@example.com");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertInstanceOf(JwtPrincipal.User.class, result.get());
        assertEquals("user@example.com", ((JwtPrincipal.User) result.get()).username());
    }

    @Test
    @DisplayName("Extracts user principal from preferred_username field when higher priority fields are missing")
    void extractsUserPrincipalFromPreferredUsername()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("preferred_username", "preferred-user");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertInstanceOf(JwtPrincipal.User.class, result.get());
        assertEquals("preferred-user", ((JwtPrincipal.User) result.get()).username());
    }

    @Test
    @DisplayName("Extracts user principal from username field when higher priority fields are missing")
    void extractsUserPrincipalFromUsername()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("username", "username-value");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertInstanceOf(JwtPrincipal.User.class, result.get());
        assertEquals("username-value", ((JwtPrincipal.User) result.get()).username());
    }

    @Test
    @DisplayName("Extracts user principal from sub field when all other fields are missing")
    void extractsUserPrincipalFromSub()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("sub", "sub-value");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertInstanceOf(JwtPrincipal.User.class, result.get());
        assertEquals("sub-value", ((JwtPrincipal.User) result.get()).username());
    }

    @Test
    @DisplayName("Prefers cognito:username over email when both are present")
    void prefersCognitoUsernameOverEmail()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("cognito:username", "cognito-user")
            .put("email", "email-user");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertEquals("cognito-user", ((JwtPrincipal.User) result.get()).username());
    }

    @Test
    @DisplayName("Prefers email over preferred_username when cognito:username is missing")
    void prefersEmailOverPreferredUsername()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("email", "email-user")
            .put("preferred_username", "preferred-user");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertEquals("email-user", ((JwtPrincipal.User) result.get()).username());
    }

    @Test
    @DisplayName("Returns empty when all username fields are missing")
    void returnsEmptyWhenAllUsernameFieldsMissing()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("other_field", "value");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Returns empty when username field is null")
    void returnsEmptyWhenUsernameFieldIsNull()
    {
        final JsonNode payload = mapper.createObjectNode()
            .putNull("cognito:username");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Returns empty when username field is blank")
    void returnsEmptyWhenUsernameFieldIsBlank()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("cognito:username", "");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Returns empty when username field is whitespace only")
    void returnsEmptyWhenUsernameFieldIsWhitespaceOnly()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("cognito:username", "   ");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Returns empty when username field is not textual")
    void returnsEmptyWhenUsernameFieldIsNotTextual()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("cognito:username", 123);

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Skips null fields and uses next available field")
    void skipsNullFieldsAndUsesNextAvailableField()
    {
        final JsonNode payload = mapper.createObjectNode()
            .putNull("cognito:username")
            .put("email", "email-user");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertEquals("email-user", ((JwtPrincipal.User) result.get()).username());
    }

    @Test
    @DisplayName("Skips blank fields and uses next available field")
    void skipsBlankFieldsAndUsesNextAvailableField()
    {
        final JsonNode payload = mapper.createObjectNode()
            .put("cognito:username", "")
            .put("email", "email-user");

        final Optional<JwtPrincipal> result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertEquals("email-user", ((JwtPrincipal.User) result.get()).username());
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
