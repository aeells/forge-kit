package io.forge.kit.throttle.reference;

import io.forge.kit.throttle.infrastructure.RateLimitStatus;
import io.forge.kit.throttle.infrastructure.RateLimiter;
import io.forge.kit.throttle.key.strategy.HttpHeaderRateLimitKeyStrategy;
import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.spi.ResteasyReactiveContainerRequestContext;

/**
 * Reference rate-limiting filter for RESTEasy Reactive.
 *
 * <p>This filter enforces request throttling using the {@link RateLimiter}
 * abstraction. It is intended as a lightweight, production-safe reference
 * implementation with no built-in observability or metrics.</p>
 *
 * <p>The filter runs <strong>before authentication</strong> to ensure both
 * authenticated and unauthenticated endpoints are protected.</p>
 *
 * <p>Activation is controlled at <strong>build time</strong> via the
 * {@code forge.rate-limit.reference.enabled} property.</p>
 *
 * <p>This module deliberately avoids coupling to logging, metrics, or tracing
 * frameworks. Consumers may layer those concerns externally if desired.</p>
 */
@ApplicationScoped
@IfBuildProperty(
    name = "forge.rate-limit.reference.enabled", stringValue = "true"
)
public class ReferenceRateLimitingFilter
{
    private static final Logger LOGGER = Logger.getLogger(ReferenceRateLimitingFilter.class);

    @Inject
    Instance<RateLimiter> rateLimiter;

    @Inject
    HttpHeaderRateLimitKeyStrategy keyResolver;

    /**
     * RESTEasy Reactive request filter that enforces rate limiting.
     *
     * <p>Runs with {@code preMatching=true} and high priority to ensure
     * throttling is applied before authentication and routing.</p>
     */
    @ServerRequestFilter(preMatching = true, priority = 10)
    public void filter(final ResteasyReactiveContainerRequestContext ctx)
    {
        // Defensive: should never happen in a correctly configured service
        if (rateLimiter.isResolvable())
        {
            final String rateLimitKey = keyResolver.resolve(ctx);
            final RateLimitStatus status = rateLimiter.get().tryConsume(rateLimitKey);

            if (!status.allowed())
            {
                LOGGER.warnf("Rate limit exceeded for key [%s] (limit=%d, remaining=%d)", rateLimitKey, status.limit(), status.remaining());

                final Response.ResponseBuilder response = Response.status(429)
                    .entity(Map.of("error", "Rate limit exceeded"))
                    .header("X-RateLimit-Limit", status.limit())
                    .header("X-RateLimit-Remaining", status.remaining());

                if (status.retryAfterSeconds() > 0)
                {
                    response.header("Retry-After", status.retryAfterSeconds());
                }

                ctx.abortWith(response.build());
            }
        }
    }
}
