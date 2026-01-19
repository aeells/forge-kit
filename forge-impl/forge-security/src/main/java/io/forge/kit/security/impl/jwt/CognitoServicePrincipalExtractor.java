package io.forge.kit.security.impl.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import io.forge.kit.security.api.jwt.JwtPrincipal;
import io.forge.kit.security.api.jwt.JwtPrincipalExtractor;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public final class CognitoServicePrincipalExtractor implements JwtPrincipalExtractor
{
    @Override
    public Optional<JwtPrincipal> extract(final JsonNode payload)
    {
        final JsonNode value = payload.get("custom:service_id");
        if (value != null && value.isTextual() && !value.asText().isBlank())
        {
            return Optional.of(new JwtPrincipal.Service(value.asText()));
        }

        return Optional.empty();
    }
}
