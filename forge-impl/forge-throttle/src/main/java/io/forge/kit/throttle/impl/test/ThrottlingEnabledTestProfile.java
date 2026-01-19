package io.forge.kit.throttle.impl.test;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Map;

/**
 * Test profile for rate limiting integration tests.
 * <p>
 * This profile ensures that the {@code forge.rate-limit.reference.enabled} build-time property is set to {@code true} so that
 * {@link io.forge.kit.throttle.impl.reference.ReferenceRateLimitingFilter} is included in the build when running tests.
 * <p>
 * This avoids needing to configure the property in Maven POM files, keeping test configuration in the test code where it belongs.
 */
public sealed class ThrottlingEnabledTestProfile implements QuarkusTestProfile permits ThrottlingDisabledTestProfile
{
    private final boolean enabled;

    @SuppressWarnings("unused")
    public ThrottlingEnabledTestProfile()
    {
        this(true);
    }

    public ThrottlingEnabledTestProfile(final boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public Map<String, String> getConfigOverrides()
    {
        if (enabled)
        {
            // override any test/application configuration to enable throttling
            // all other configuration from test/resources/application.properties etc. will be ignored
            return Map.of(
                "forge.rate-limit.reference.enabled", "true",
                "rate-limit.authenticated-capacity-per-minute", "10",
                "rate-limit.unauthenticated-capacity-per-minute", "10",
                "rate-limit.authenticated-refill-per-second", "100",
                "rate-limit.unauthenticated-refill-per-second", "100"
            );
        }
        else
        {
            // override any test/application configuration to effectively
            // disable rate limiting by setting high rate-limit thresholds
            return Map.of(
                "forge.rate-limit.reference.enabled", "false",
                "rate-limit.authenticated-capacity-per-minute", "1000",
                "rate-limit.unauthenticated-capacity-per-minute", "1000",
                "rate-limit.authenticated-refill-per-second", "100",
                "rate-limit.unauthenticated-refill-per-second", "100"
            );
        }
    }
}
