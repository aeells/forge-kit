package io.forge.kit.security.impl.auth.jwt;

import io.forge.kit.security.api.auth.jwt.JwtPrincipal;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Accessor for user principal claims from JWT tokens.
 * <p>
 * This class provides convenient methods for extracting username claims from JWT tokens.
 * It uses {@link JwtPrincipalResolver} to resolve principals and filters for user principals.
 * The resolver uses {@link OidcUserPrincipalExtractor} which checks claims in the following priority:
 * <ol>
 * <li>{@code cognito:username} - Cognito-specific</li>
 * <li>{@code email} - Standard OIDC field</li>
 * <li>{@code preferred_username} - Standard OIDC field</li>
 * <li>{@code username} - Common field</li>
 * <li>{@code sub} - Standard OIDC subject</li>
 * </ol>
 * </p>
 */
@ApplicationScoped
public final class JwtUserPrincipalAccessor
{
    private final JwtPrincipalResolver principalResolver;

    @Inject
    public JwtUserPrincipalAccessor(final JwtPrincipalResolver principalResolver)
    {
        this.principalResolver = principalResolver;
    }

    /**
     * Extracts username from a JWT token (ID token or refresh token).
     *
     * @param jwtToken the JWT token
     * @return username if found, null otherwise
     */
    public String extractUsername(final String jwtToken)
    {
        return principalResolver.resolveFromToken(jwtToken)
            .filter(JwtPrincipal.User.class::isInstance)
            .map(JwtPrincipal.User.class::cast)
            .map(JwtPrincipal.User::username)
            .orElse(null);
    }
}
