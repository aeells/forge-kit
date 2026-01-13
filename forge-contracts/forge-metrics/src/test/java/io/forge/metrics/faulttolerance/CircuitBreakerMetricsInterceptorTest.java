package io.forge.metrics.faulttolerance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.smallrye.faulttolerance.api.CircuitBreakerState;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerMetricsInterceptorTest
{
    @Mock
    private CircuitBreakerStateTracker stateTracker;

    @Mock
    private InvocationContext context;

    @InjectMocks
    private CircuitBreakerMetricsInterceptor interceptor;

    @Test
    @DisplayName("collectCircuitBreakerMetrics proceeds without metrics when no CircuitBreaker annotation")
    void collectCircuitBreakerMetrics_ProceedsWithoutMetrics_WhenNoCircuitBreakerAnnotation() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        when(context.getMethod()).thenReturn(TestTarget.class.getMethod("methodWithoutCircuitBreaker"));
        lenient().when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenReturn("result");

        // When
        final Object result = interceptor.collectCircuitBreakerMetrics(context);

        // Then
        assertEquals("result", result);
        verify(context).proceed();
        verify(stateTracker, never()).recordStateTransition(any(), any());
    }

    @Test
    @DisplayName("collectCircuitBreakerMetrics records state transition when state changes")
    void collectCircuitBreakerMetrics_RecordsStateTransition_WhenStateChanges() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithCircuitBreaker");
        final String circuitName = TestTarget.class.getName() + "#methodWithCircuitBreaker";

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenReturn("result");
        when(stateTracker.getPreviousState(circuitName))
            .thenReturn(CircuitBreakerState.CLOSED);

        // When
        final Object result = interceptor.collectCircuitBreakerMetrics(context);

        // Then
        assertEquals("result", result);
        verify(stateTracker).recordStateTransition(circuitName, CircuitBreakerState.CLOSED);
    }

    @Test
    @DisplayName("collectCircuitBreakerMetrics records state transition even when state unchanged")
    void collectCircuitBreakerMetrics_RecordsStateTransition_EvenWhenStateUnchanged() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithCircuitBreaker");
        final String circuitName = TestTarget.class.getName() + "#methodWithCircuitBreaker";

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenReturn("result");
        when(stateTracker.getPreviousState(circuitName))
            .thenReturn(CircuitBreakerState.CLOSED);

        // When
        final Object result = interceptor.collectCircuitBreakerMetrics(context);

        // Then
        assertEquals("result", result);
        verify(stateTracker).recordStateTransition(circuitName, CircuitBreakerState.CLOSED);
    }

    @Test
    @DisplayName("collectCircuitBreakerMetrics handles exceptions and still records state")
    void collectCircuitBreakerMetrics_HandlesExceptions_AndStillRecordsState() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithCircuitBreaker");
        final String circuitName = TestTarget.class.getName() + "#methodWithCircuitBreaker";
        final RuntimeException exception = new RuntimeException("Test exception");

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenThrow(exception);
        when(stateTracker.getPreviousState(circuitName))
            .thenReturn(CircuitBreakerState.CLOSED);

        // When/Then
        try
        {
            interceptor.collectCircuitBreakerMetrics(context);
        }
        catch (final RuntimeException e)
        {
            assertEquals(exception, e);
        }

        // Then
        verify(stateTracker).recordStateTransition(circuitName, CircuitBreakerState.CLOSED);
    }

    @Test
    @DisplayName("collectCircuitBreakerMetrics handles null previous state")
    void collectCircuitBreakerMetrics_HandlesNullPreviousState() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithCircuitBreaker");
        final String circuitName = TestTarget.class.getName() + "#methodWithCircuitBreaker";

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenReturn("result");
        when(stateTracker.getPreviousState(circuitName))
            .thenReturn(null);

        // When
        final Object result = interceptor.collectCircuitBreakerMetrics(context);

        // Then
        assertEquals("result", result);
        verify(stateTracker).recordStateTransition(circuitName, null);
    }

    // Test helper class
    @SuppressWarnings("unused")
    static class TestTarget
    {
        public String methodWithoutCircuitBreaker()
        {
            return "result";
        }

        @CircuitBreaker(delay = 1000)
        public String methodWithCircuitBreaker()
        {
            return "result";
        }
    }
}
