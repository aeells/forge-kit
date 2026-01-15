package io.forge.kit.common.auth.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;

public interface JwtPrincipalExtractor
{
    Optional<JwtPrincipal> extract(final JsonNode payload);
}
