package io.forge.metrics.domain.support;

import io.forge.metrics.domain.MetricsRecorder;
import java.util.Optional;

public interface MetricsRecorderResolver
{
    Optional<MetricsRecorder> resolve(Class<? extends MetricsRecorder> recorderClass);
}
