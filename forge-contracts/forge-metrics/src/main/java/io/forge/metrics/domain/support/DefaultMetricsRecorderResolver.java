package io.forge.metrics.domain.support;

import io.forge.metrics.domain.MetricsRecorder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.logging.Logger;

@ApplicationScoped
public final class DefaultMetricsRecorderResolver implements MetricsRecorderResolver
{
    private static final Logger LOGGER = Logger.getLogger(DefaultMetricsRecorderResolver.class);

    @Inject
    Instance<MetricsRecorder> recorders;

    private final Map<Class<? extends MetricsRecorder>, Optional<MetricsRecorder>> cache = new ConcurrentHashMap<>();

    @Override
    public Optional<MetricsRecorder> resolve(final Class<? extends MetricsRecorder> recorderClass)
    {
        if (recorderClass == null)
        {
            return Optional.empty();
        }

        return cache.computeIfAbsent(recorderClass, this::resolveUncached);
    }

    private Optional<MetricsRecorder> resolveUncached(final Class<? extends MetricsRecorder> recorderClass)
    {
        // 1. CDI-managed recorder
        for (final MetricsRecorder recorder : recorders)
        {
            if (recorderClass.isInstance(recorder))
            {
                LOGGER.debugf("Resolved metrics recorder %s via CDI", recorderClass.getName());
                return Optional.of(recorder);
            }
        }

        // 2. Reflection fallback
        try
        {
            final MetricsRecorder recorder = recorderClass.getDeclaredConstructor().newInstance();

            LOGGER.infof("Resolved metrics recorder %s via reflection", recorderClass.getName());
            return Optional.of(recorder);
        }
        catch (final Exception e)
        {
            LOGGER.warnf("Could not resolve metrics recorder %s: %s", recorderClass.getName(), e.getMessage());
            return Optional.empty();
        }
    }
}
