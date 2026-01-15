package io.forge.kit.throttle.key.resolver.jwt;

public interface AuthHeaderRateLimitKeyResolver
{
    String resolve(final String authorizationHeader);
}
