package io.forge.metrics.dto;

/**
 * Synthetic MetricsResultIndicator for Optional that is present (success case).
 */
public final class OptionalPresentResult implements MetricsResultIndicator
{
    public static final OptionalPresentResult INSTANCE = new OptionalPresentResult();

    private OptionalPresentResult()
    {
    }

    @Override
    public boolean success()
    {
        return true;
    }

    @Override
    public String errorMessage()
    {
        return null;
    }
}
