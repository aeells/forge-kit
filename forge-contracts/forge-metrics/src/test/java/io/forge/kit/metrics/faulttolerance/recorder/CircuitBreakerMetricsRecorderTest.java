package io.forge.kit.metrics.faulttolerance.recorder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerMetricsRecorderTest
{
    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter.Builder counterBuilder;

    @Mock
    private Counter counter;

    @InjectMocks
    private CircuitBreakerMetricsRecorder recorder;

    @Test
    @DisplayName("recordCircuitBreakerStateChange records metric with correct name and tags")
    void recordCircuitBreakerStateChange_RecordsMetric_WithCorrectNameAndTags()
    {
        // Given
        final String circuitName = "CandidateRepository.save";
        final String state = "open";

        try (MockedStatic<Counter> counterMock = mockStatic(Counter.class))
        {
            counterMock.when(() -> Counter.builder("circuit.breaker.state.changes")).thenReturn(counterBuilder);
            when(counterBuilder.tag(eq("circuit"), eq(circuitName))).thenReturn(counterBuilder);
            when(counterBuilder.tag(eq("state"), eq(state))).thenReturn(counterBuilder);
            when(counterBuilder.description(any())).thenReturn(counterBuilder);
            when(counterBuilder.register(eq(meterRegistry))).thenReturn(counter);

            // When
            recorder.recordCircuitBreakerStateChange(circuitName, state);

            // Then
            counterMock.verify(() -> Counter.builder("circuit.breaker.state.changes"));
            verify(counterBuilder).tag("circuit", circuitName);
            verify(counterBuilder).tag("state", state);
            verify(counterBuilder).description("Circuit breaker state changes");
            verify(counterBuilder).register(meterRegistry);
            verify(counter).increment();
        }
    }

    @Test
    @DisplayName("recordCircuitBreakerStateChange handles all circuit breaker states")
    void recordCircuitBreakerStateChange_HandlesAllStates()
    {
        // Given
        final String circuitName = "TestRepository.method";
        final String[] states = {"closed", "open", "half_open"};

        try (MockedStatic<Counter> counterMock = mockStatic(Counter.class))
        {
            counterMock.when(() -> Counter.builder(any())).thenReturn(counterBuilder);
            when(counterBuilder.tag(any(), any())).thenReturn(counterBuilder);
            when(counterBuilder.description(any())).thenReturn(counterBuilder);
            when(counterBuilder.register(any())).thenReturn(counter);

            // When
            for (final String state : states)
            {
                recorder.recordCircuitBreakerStateChange(circuitName, state);
            }

            // Then
            verify(counter, org.mockito.Mockito.times(3)).increment();
        }
    }

    @Test
    @DisplayName("recordCircuitBreakerStateChange handles different circuit names")
    void recordCircuitBreakerStateChange_HandlesDifferentCircuitNames()
    {
        // Given
        final String[] circuitNames = {"CandidateRepository.save", "SourceDocumentUploadRepository.uploadResume"};
        final String state = "closed";

        try (MockedStatic<Counter> counterMock = mockStatic(Counter.class))
        {
            counterMock.when(() -> Counter.builder(any())).thenReturn(counterBuilder);
            when(counterBuilder.tag(any(), any())).thenReturn(counterBuilder);
            when(counterBuilder.description(any())).thenReturn(counterBuilder);
            when(counterBuilder.register(any())).thenReturn(counter);

            // When
            for (final String circuitName : circuitNames)
            {
                recorder.recordCircuitBreakerStateChange(circuitName, state);
            }

            // Then
            verify(counter, org.mockito.Mockito.times(2)).increment();
        }
    }

    @Test
    @DisplayName("recordCircuitBreakerStateChange uses correct metric name")
    void recordCircuitBreakerStateChange_UsesCorrectMetricName()
    {
        // Given
        final String circuitName = "Test.method";
        final String state = "open";

        try (MockedStatic<Counter> counterMock = mockStatic(Counter.class))
        {
            counterMock.when(() -> Counter.builder("circuit.breaker.state.changes")).thenReturn(counterBuilder);
            when(counterBuilder.tag(any(), any())).thenReturn(counterBuilder);
            when(counterBuilder.description(any())).thenReturn(counterBuilder);
            when(counterBuilder.register(any())).thenReturn(counter);

            // When
            recorder.recordCircuitBreakerStateChange(circuitName, state);

            // Then
            counterMock.verify(() -> Counter.builder("circuit.breaker.state.changes"));
            verify(counter).increment();
        }
    }

}
