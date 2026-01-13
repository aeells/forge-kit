package io.forge.metrics.faulttolerance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import io.forge.metrics.faulttolerance.recorder.CircuitBreakerMetricsRecorder;
import io.smallrye.faulttolerance.api.CircuitBreakerState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerStateTrackerTest
{
    @Mock
    private CircuitBreakerMetricsRecorder recorder;

    @Mock
    private CircuitBreakerStateFetcher stateFetcher;

    @InjectMocks
    private CircuitBreakerStateTracker stateTracker;

    @Test
    @DisplayName("recordStateTransition does nothing when state is null")
    void recordStateTransition_DoesNothing_WhenStateIsNull()
    {
        // Given
        final String circuitName = "test-circuit";
        when(stateFetcher.getCurrentState(circuitName)).thenReturn(null);

        // When
        stateTracker.recordStateTransition(circuitName, CircuitBreakerState.CLOSED);

        // Then
        verify(recorder, never()).recordCircuitBreakerStateChange(any(), any());
    }

    @Test
    @DisplayName("recordStateTransition does not record when state unchanged")
    void recordStateTransition_DoesNotRecord_WhenStateUnchanged()
    {
        // Given
        final String circuitName = "test-circuit";
        final CircuitBreakerState state = CircuitBreakerState.CLOSED;
        when(stateFetcher.getCurrentState(circuitName)).thenReturn(state);

        // When
        stateTracker.recordStateTransition(circuitName, state);

        // Then
        verify(recorder, never()).recordCircuitBreakerStateChange(any(), any());
    }

    @Test
    @DisplayName("recordStateTransition records when state changes from CLOSED to OPEN")
    void recordStateTransition_Records_WhenStateChangesFromClosedToOpen()
    {
        // Given
        final String circuitName = "test-circuit";
        when(stateFetcher.getCurrentState(circuitName))
            .thenReturn(CircuitBreakerState.OPEN);

        // When
        stateTracker.recordStateTransition(circuitName, CircuitBreakerState.CLOSED);

        // Then
        verify(recorder).recordCircuitBreakerStateChange(circuitName, "open");
    }

    @Test
    @DisplayName("recordStateTransition records when state changes from OPEN to HALF_OPEN")
    void recordStateTransition_Records_WhenStateChangesFromOpenToHalfOpen()
    {
        // Given
        final String circuitName = "test-circuit";
        when(stateFetcher.getCurrentState(circuitName))
            .thenReturn(CircuitBreakerState.HALF_OPEN);

        // When
        stateTracker.recordStateTransition(circuitName, CircuitBreakerState.OPEN);

        // Then
        verify(recorder).recordCircuitBreakerStateChange(circuitName, "half_open");
    }

    @Test
    @DisplayName("recordStateTransition records when state changes from HALF_OPEN to CLOSED")
    void recordStateTransition_Records_WhenStateChangesFromHalfOpenToClosed()
    {
        // Given
        final String circuitName = "test-circuit";
        when(stateFetcher.getCurrentState(circuitName))
            .thenReturn(CircuitBreakerState.CLOSED);

        // When
        stateTracker.recordStateTransition(circuitName, CircuitBreakerState.HALF_OPEN);

        // Then
        verify(recorder).recordCircuitBreakerStateChange(circuitName, "closed");
    }

    @Test
    @DisplayName("recordStateTransition handles first invocation with null previous state")
    void recordStateTransition_HandlesFirstInvocation_WithNullPreviousState()
    {
        // Given
        final String circuitName = "test-circuit";
        when(stateFetcher.getCurrentState(circuitName))
            .thenReturn(CircuitBreakerState.CLOSED);

        // When
        stateTracker.recordStateTransition(circuitName, null);

        // Then
        verify(recorder, never()).recordCircuitBreakerStateChange(any(), any());
    }

    @Test
    @DisplayName("getPreviousState returns null for first invocation")
    void getPreviousState_ReturnsNull_ForFirstInvocation()
    {
        // Given
        final String circuitName = "test-circuit";

        // When
        final CircuitBreakerState result = stateTracker.getPreviousState(circuitName);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("getPreviousState returns previous state after recording")
    void getPreviousState_ReturnsPreviousState_AfterRecording()
    {
        // Given
        final String circuitName = "test-circuit";
        when(stateFetcher.getCurrentState(circuitName))
            .thenReturn(CircuitBreakerState.OPEN);

        // When
        stateTracker.recordStateTransition(circuitName, CircuitBreakerState.CLOSED);
        final CircuitBreakerState result = stateTracker.getPreviousState(circuitName);

        // Then
        assertEquals(CircuitBreakerState.OPEN, result);
    }

    @Test
    @DisplayName("recordStateTransition handles multiple circuit breakers independently")
    void recordStateTransition_HandlesMultipleCircuitBreakers_Independently()
    {
        // Given
        final String circuit1 = "circuit-1";
        final String circuit2 = "circuit-2";
        when(stateFetcher.getCurrentState(circuit1))
            .thenReturn(CircuitBreakerState.OPEN);
        when(stateFetcher.getCurrentState(circuit2))
            .thenReturn(CircuitBreakerState.CLOSED);

        // When
        stateTracker.recordStateTransition(circuit1, CircuitBreakerState.CLOSED);
        stateTracker.recordStateTransition(circuit2, CircuitBreakerState.CLOSED);

        // Then
        verify(recorder).recordCircuitBreakerStateChange(eq(circuit1), eq("open"));
        verify(recorder, never()).recordCircuitBreakerStateChange(eq(circuit2), any());
    }
}
