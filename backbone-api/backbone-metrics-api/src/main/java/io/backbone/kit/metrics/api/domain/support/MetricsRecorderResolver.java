package io.backbone.kit.metrics.api.domain.support;

import io.backbone.kit.metrics.api.domain.MetricsRecorder;
import java.util.Optional;

public interface MetricsRecorderResolver
{
    Optional<MetricsRecorder> resolve(Class<? extends MetricsRecorder> recorderClass);
}
