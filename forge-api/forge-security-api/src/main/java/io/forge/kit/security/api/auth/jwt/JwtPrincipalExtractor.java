package io.forge.kit.security.api.auth.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;

public interface JwtPrincipalExtractor
{
    Optional<JwtPrincipal> extract(final JsonNode payload);
}
