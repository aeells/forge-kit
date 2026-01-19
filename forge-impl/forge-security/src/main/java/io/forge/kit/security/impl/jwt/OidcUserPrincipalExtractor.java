package io.forge.kit.security.impl.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import io.forge.kit.security.api.jwt.JwtPrincipal;
import io.forge.kit.security.api.jwt.JwtPrincipalExtractor;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public final class OidcUserPrincipalExtractor implements JwtPrincipalExtractor
{
    private static final List<String> USERNAME_FIELDS = List.of(
        "cognito:username",
        "email",
        "preferred_username",
        "username",
        "sub"
    );

    @Override
    public Optional<JwtPrincipal> extract(final JsonNode payload)
    {
        for (final String field : USERNAME_FIELDS)
        {
            final JsonNode value = payload.get(field);
            if (value != null && value.isTextual() && !value.asText().isBlank())
            {
                return Optional.of(new JwtPrincipal.User(value.asText()));
            }
        }

        return Optional.empty();
    }
}
