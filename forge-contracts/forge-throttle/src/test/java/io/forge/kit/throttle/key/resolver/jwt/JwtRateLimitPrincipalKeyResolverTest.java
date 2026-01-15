package io.forge.kit.throttle.key.resolver.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import io.forge.kit.common.auth.jwt.JwtPrincipal;
import io.forge.kit.common.auth.jwt.JwtPrincipalExtractor;
import jakarta.enterprise.inject.Instance;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtRateLimitPrincipalKeyResolverTest
{
    @Test
    @DisplayName("Returns anonymous key when authorization header is null")
    void returnsAnonymousKeyWhenHeaderIsNull()
    {
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of()));
        final String result = resolver.resolve(null);

        assertEquals("auth:unidentified", result);
    }

    @Test
    @DisplayName("Returns anonymous key when authorization header does not start with Bearer")
    void returnsAnonymousKeyWhenHeaderDoesNotStartWithBearer()
    {
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of()));
        final String result = resolver.resolve("Basic dXNlcm5hbWU6cGFzc3dvcmQ=");

        assertEquals("auth:unidentified", result);
    }

    @Test
    @DisplayName("Returns anonymous key when authorization header is empty")
    void returnsAnonymousKeyWhenHeaderIsEmpty()
    {
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of()));
        final String result = resolver.resolve("");

        assertEquals("auth:unidentified", result);
    }

    @Test
    @DisplayName("Returns anonymous key when JWT token is invalid")
    void returnsAnonymousKeyWhenTokenIsInvalid()
    {
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of()));
        final String result = resolver.resolve("Bearer invalid.token");

        assertEquals("auth:unidentified", result);
    }

    @Test
    @DisplayName("Returns anonymous key when no extractor matches")
    void returnsAnonymousKeyWhenNoExtractorMatches()
    {
        final JwtPrincipalExtractor extractor = mock(JwtPrincipalExtractor.class);
        when(extractor.extract(any(JsonNode.class))).thenReturn(Optional.empty());

        final String jwtToken = buildTestJwtToken(Map.of("someField", "someValue"));
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of(extractor)));
        final String result = resolver.resolve("Bearer " + jwtToken);

        assertEquals("auth:unidentified", result);
    }

    @Test
    @DisplayName("Returns service key when service extractor matches")
    void returnsServiceKeyWhenServiceExtractorMatches()
    {
        final JwtPrincipalExtractor extractor = mock(JwtPrincipalExtractor.class);
        when(extractor.extract(any(JsonNode.class))).thenReturn(Optional.of(new JwtPrincipal.Service("service-123")));

        final String jwtToken = buildTestJwtToken(Map.of("custom:service_id", "service-123"));
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of(extractor)));
        final String result = resolver.resolve("Bearer " + jwtToken);

        assertEquals("service:service-123", result);
    }

    @Test
    @DisplayName("Returns user key when user extractor matches")
    void returnsUserKeyWhenUserExtractorMatches()
    {
        final JwtPrincipalExtractor extractor = mock(JwtPrincipalExtractor.class);
        when(extractor.extract(any(JsonNode.class))).thenReturn(Optional.of(new JwtPrincipal.User("user@example.com")));

        final String jwtToken = buildTestJwtToken(Map.of("cognito:username", "user@example.com"));
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of(extractor)));
        final String result = resolver.resolve("Bearer " + jwtToken);

        assertEquals("user:user@example.com", result);
    }

    @Test
    @DisplayName("Uses first matching extractor when multiple extractors are provided")
    void usesFirstMatchingExtractorWhenMultipleExtractorsProvided()
    {
        final JwtPrincipalExtractor firstExtractor = mock(JwtPrincipalExtractor.class);
        final JwtPrincipalExtractor secondExtractor = mock(JwtPrincipalExtractor.class);
        when(firstExtractor.extract(any(JsonNode.class))).thenReturn(Optional.of(new JwtPrincipal.Service("service-123")));
        when(secondExtractor.extract(any(JsonNode.class))).thenReturn(Optional.of(new JwtPrincipal.User("user@example.com")));

        final String jwtToken = buildTestJwtToken(Map.of("custom:service_id", "service-123", "cognito:username", "user@example.com"));
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of(firstExtractor,
            secondExtractor)));
        final String result = resolver.resolve("Bearer " + jwtToken);

        assertEquals("service:service-123", result);
    }

    @Test
    @DisplayName("Skips empty extractors and uses first non-empty one")
    void skipsEmptyExtractorsAndUsesFirstNonEmptyOne()
    {
        final JwtPrincipalExtractor emptyExtractor = mock(JwtPrincipalExtractor.class);
        final JwtPrincipalExtractor matchingExtractor = mock(JwtPrincipalExtractor.class);
        when(emptyExtractor.extract(any(JsonNode.class))).thenReturn(Optional.empty());
        when(matchingExtractor.extract(any(JsonNode.class))).thenReturn(Optional.of(new JwtPrincipal.User("user@example.com")));

        final String jwtToken = buildTestJwtToken(Map.of("cognito:username", "user@example.com"));
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of(emptyExtractor,
            matchingExtractor)));
        final String result = resolver.resolve("Bearer " + jwtToken);

        assertEquals("user:user@example.com", result);
    }

    @Test
    @DisplayName("Returns user key when user extractor matches with email claim")
    void returnsUserKeyWhenUserExtractorMatchesWithEmailClaim()
    {
        final JwtPrincipalExtractor extractor = mock(JwtPrincipalExtractor.class);
        when(extractor.extract(any(JsonNode.class))).thenReturn(Optional.of(new JwtPrincipal.User("user@example.com")));

        final String jwtToken = buildTestJwtToken(Map.of("email", "user@example.com"));
        final JwtRateLimitPrincipalKeyResolver resolver = new JwtRateLimitPrincipalKeyResolver(createInstance(List.of(extractor)));
        final String result = resolver.resolve("Bearer " + jwtToken);

        assertEquals("user:user@example.com", result);
    }

    private String buildTestJwtToken(final Map<String, String> claims)
    {
        final String headerJson = "{\"alg\":\"none\"}";

        final StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("{");

        boolean first = true;
        for (Map.Entry<String, String> entry : claims.entrySet())
        {
            if (!first)
            {
                payloadBuilder.append(",");
            }
            first = false;
            payloadBuilder.append("\"")
                .append(entry.getKey())
                .append("\":\"")
                .append(entry.getValue())
                .append("\"");
        }

        payloadBuilder.append("}");

        final String header = base64UrlEncode(headerJson);
        final String payload = base64UrlEncode(payloadBuilder.toString());

        return header + "." + payload + ".signature";
    }

    private String base64UrlEncode(final String value)
    {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    @SuppressWarnings("unchecked")
    private Instance<JwtPrincipalExtractor> createInstance(final List<JwtPrincipalExtractor> extractors)
    {
        final Instance<JwtPrincipalExtractor> instance = mock(Instance.class);
        final Iterator<JwtPrincipalExtractor> iterator = extractors.isEmpty() ? Collections.emptyIterator() : extractors.iterator();
        when(instance.iterator()).thenReturn(iterator);
        when(instance.spliterator()).thenReturn(extractors.spliterator());
        return instance;
    }
}
