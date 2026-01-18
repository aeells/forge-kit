package io.forge.kit.throttle.impl.infrastructure;

import io.forge.kit.throttle.api.infrastructure.RateLimiterProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import org.jboss.logging.Logger;

/**
 * Conditional producer for {@link Bucket4jRateLimiter}.
 *
 * <p>This producer only creates the {@link Bucket4jRateLimiter} bean if {@link RateLimiterProperties}
 * is available. This ensures the bean is not created when rate limiting is not configured.</p>
 */
@ApplicationScoped
public class Bucket4jRateLimiterProducer
{
    private static final Logger LOGGER = Logger.getLogger(Bucket4jRateLimiterProducer.class);

    @Produces
    @ApplicationScoped
    public Bucket4jRateLimiter produceRateLimiter(final Instance<RateLimiterProperties> propertiesInstance)
    {
        if (propertiesInstance.isResolvable())
        {
            return new Bucket4jRateLimiter(propertiesInstance.get());
        }

        LOGGER.debug("RateLimiterProperties not available - Bucket4jRateLimiter will not be created");
        return null; // Don't produce the bean if properties are not available
    }
}
