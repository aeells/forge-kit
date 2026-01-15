package io.forge.kit.throttle.key.resolver.http;

import jakarta.ws.rs.container.ContainerRequestContext;

public interface IpHeaderRateLimitKeyResolver
{
    String resolve(final ContainerRequestContext requestContext);
}
