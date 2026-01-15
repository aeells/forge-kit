package io.forge.kit.throttle.infrastructure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Bucket4jRateLimiterProducerTest
{
    @Test
    @DisplayName("Produces rate limiter when properties are available")
    void producesRateLimiterWhenPropertiesAvailable()
    {
        final RateLimiterProperties properties = mock(RateLimiterProperties.class);
        final Instance<RateLimiterProperties> propertiesInstance = mock(Instance.class);

        when(propertiesInstance.isResolvable()).thenReturn(true);
        when(propertiesInstance.get()).thenReturn(properties);

        final Bucket4jRateLimiterProducer producer = new Bucket4jRateLimiterProducer();
        final Bucket4jRateLimiter result = producer.produceRateLimiter(propertiesInstance);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Returns null when properties are not available")
    void returnsNullWhenPropertiesNotAvailable()
    {
        final Instance<RateLimiterProperties> propertiesInstance = mock(Instance.class);

        when(propertiesInstance.isResolvable()).thenReturn(false);

        final Bucket4jRateLimiterProducer producer = new Bucket4jRateLimiterProducer();
        final Bucket4jRateLimiter result = producer.produceRateLimiter(propertiesInstance);

        assertNull(result);
    }
}
