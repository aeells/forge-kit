package io.forge.kit.metrics.domain.support;

import io.forge.kit.metrics.domain.MetricsRecorder;
import java.util.Optional;

public interface MetricsRecorderResolver
{
    Optional<MetricsRecorder> resolve(Class<? extends MetricsRecorder> recorderClass);
}
