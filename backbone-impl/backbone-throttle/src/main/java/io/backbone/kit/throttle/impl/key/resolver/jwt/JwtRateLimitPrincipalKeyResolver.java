package io.backbone.kit.throttle.impl.key.resolver.jwt;

import io.backbone.kit.security.api.jwt.JwtPrincipal;
import io.backbone.kit.security.impl.jwt.JwtPrincipalResolver;
import io.backbone.kit.throttle.api.key.resolver.jwt.AuthHeaderRateLimitKeyResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public final class JwtRateLimitPrincipalKeyResolver implements AuthHeaderRateLimitKeyResolver
{
    private final JwtPrincipalResolver principalResolver;

    @Inject
    public JwtRateLimitPrincipalKeyResolver(JwtPrincipalResolver principalResolver)
    { this.principalResolver = principalResolver; }

    @Override
    public String resolve(final String authorizationHeader)
    {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
        {
            return new JwtPrincipal.Anonymous().rateLimitKey();
        }

        final String token = authorizationHeader.substring("Bearer ".length());

        return principalResolver.resolveFromToken(token)
            .orElse(new JwtPrincipal.Anonymous())
            .rateLimitKey();
    }
}
