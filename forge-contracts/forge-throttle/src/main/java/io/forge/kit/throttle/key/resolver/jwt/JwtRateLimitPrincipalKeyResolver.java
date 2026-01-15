package io.forge.kit.throttle.key.resolver.jwt;

import io.forge.kit.common.auth.jwt.JwtPayloadParser;
import io.forge.kit.common.auth.jwt.JwtPrincipal;
import io.forge.kit.common.auth.jwt.JwtPrincipalExtractor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.stream.StreamSupport;

@ApplicationScoped
public final class JwtRateLimitPrincipalKeyResolver implements AuthHeaderRateLimitKeyResolver
{
    private final Instance<JwtPrincipalExtractor> extractors;

    @Inject
    public JwtRateLimitPrincipalKeyResolver(final Instance<JwtPrincipalExtractor> extractors)
    {
        this.extractors = extractors;
    }

    public String resolve(final String authorizationHeader)
    {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
        {
            return new JwtPrincipal.Anonymous().rateLimitKey();
        }

        final String token = authorizationHeader.substring("Bearer ".length());

        return JwtPayloadParser.parsePayload(token)
            .flatMap(payload -> StreamSupport.stream(extractors.spliterator(), false)
                .map(e -> e.extract(payload))
                .flatMap(Optional::stream)
                .findFirst()
            )
            .orElse(new JwtPrincipal.Anonymous())
            .rateLimitKey();
    }
}
