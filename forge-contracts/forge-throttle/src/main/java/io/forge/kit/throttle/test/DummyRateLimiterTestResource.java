package io.forge.kit.throttle.test;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Quarkus test resource that provides dummy rate-limit configuration properties.
 * This allows services without rate limiting configured to start successfully in tests.
 *
 * <p>These dummy values satisfy {@code @ConfigMapping} requirements at startup.
 * Rate limiting will be active but with very high limits (1000 requests/minute) that
 * won't actually limit requests in practice, making it effectively transparent for tests.</p>
 *
 * <p>Usage: Add {@code @QuarkusTestResource(DummyRateLimiterTestResource.class)} to your test class.
 * Can be combined with other test resources: {@code @QuarkusTestResource({QuarkusPortsEnvTestResource.class,
 * DummyRateLimiterTestResource.class})}</p>
 */
@IfBuildProfile("test")
public final class DummyRateLimiterTestResource implements QuarkusTestResourceLifecycleManager
{
    @Override
    public Map<String, String> start()
    {
        // Provide dummy rate-limit configuration to satisfy @ConfigMapping requirements
        // These high values (1000 requests/minute) ensure rate limiting won't interfere with tests
        return new HashMap<>()
        {
            {
                put("rate-limit.authenticated-capacity-per-minute", "1000");
                put("rate-limit.unauthenticated-capacity-per-minute", "1000");
                put("rate-limit.authenticated-refill-per-second", "100");
                put("rate-limit.unauthenticated-refill-per-second", "100");
            }
        };
    }

    @Override
    public void stop()
    {
        // No cleanup needed
    }
}
