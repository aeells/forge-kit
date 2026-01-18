package io.forge.kit.metrics.impl.dto;

import io.forge.kit.metrics.api.dto.MetricsResultIndicator;

/**
 * Synthetic MetricsResultIndicator for Optional that is empty (not found case).
 */
public final class OptionalEmptyResult implements MetricsResultIndicator
{
    public static final OptionalEmptyResult INSTANCE = new OptionalEmptyResult();

    private OptionalEmptyResult()
    {
    }

    @Override
    public boolean success()
    {
        return false;
    }

    @Override
    public String errorMessage()
    {
        return "not_found";
    }
}
