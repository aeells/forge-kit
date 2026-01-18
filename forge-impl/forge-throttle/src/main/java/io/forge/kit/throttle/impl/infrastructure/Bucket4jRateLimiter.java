package io.forge.kit.throttle.impl.infrastructure;

import io.forge.kit.throttle.api.infrastructure.RateLimitStatus;
import io.forge.kit.throttle.api.infrastructure.RateLimiter;
import io.forge.kit.throttle.api.infrastructure.RateLimiterProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiter implementation using Bucket4j library.
 *
 * <p>This bean is only created if {@link RateLimiterProperties} is available, allowing
 * services without rate-limit configuration to start successfully.</p>
 *
 * <p>The class is only created via the producer method in {@link Bucket4jRateLimiterProducer}
 * when properties are available. It has no CDI annotations, so it won't be auto-discovered as a bean.</p>
 */
public class Bucket4jRateLimiter implements RateLimiter
{
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final RateLimiterProperties properties;

    // Package-private constructor for producer and testing
    Bucket4jRateLimiter(final RateLimiterProperties properties)
    {
        this.properties = properties;
    }

    @Override
    public RateLimitStatus tryConsume(final String key)
    {
        final Bucket bucket = buckets.computeIfAbsent(key, this::createBucket);
        final ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        final long capacity = properties.resolveCapacityForKey(key);

        if (probe.isConsumed())
        {
            return new RateLimitStatus(true, capacity, probe.getRemainingTokens(), 0L);
        }
        else
        {
            final long retryAfterNanos = probe.getNanosToWaitForRefill();
            final long retryAfterSeconds = retryAfterNanos > 0L ? (retryAfterNanos / 1_000_000_000L) + 1L : 1L;
            return new RateLimitStatus(false, capacity, probe.getRemainingTokens(), retryAfterSeconds);
        }
    }

    /**
     * Clears all rate limit buckets. Intended for testing only.
     * This allows tests to start with a clean state.
     */
    public void clearBuckets()
    {
        buckets.clear();
    }

    private Bucket createBucket(final String key)
    {
        final long capacity = properties.resolveCapacityForKey(key);
        final long refillPerSecond = properties.resolveRefillPerSecondForKey(key);

        // Create bandwidth with explicit initialTokens to ensure bucket starts full
        final Bandwidth bandwidth = Bandwidth.builder()
            .capacity(capacity)
            .refillIntervally(refillPerSecond, Duration.ofSeconds(1L))
            .build();

        return Bucket.builder()
            .addLimit(bandwidth)
            .build();
    }
}
