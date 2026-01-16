package io.forge.kit.metrics.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.forge.kit.metrics.persistence.recorder.DatabaseMetricsRecorder;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatabaseMetricsInterceptorTest
{
    @Mock
    private DatabaseMetricsRecorder recorder;

    @Mock
    private InvocationContext context;

    @InjectMocks
    private DatabaseMetricsInterceptor interceptor;

    @Test
    @DisplayName("collectDatabaseMetrics proceeds without metrics when no DatabaseMetrics annotation")
    void collectDatabaseMetrics_ProceedsWithoutMetrics_WhenNoDatabaseMetricsAnnotation() throws Exception
    {
        // Given
        final TestTargetWithoutAnnotation target = new TestTargetWithoutAnnotation();
        when(context.getMethod()).thenReturn(TestTargetWithoutAnnotation.class.getMethod("methodWithoutAnnotation"));
        when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenReturn("result");

        // When
        final Object result = interceptor.collectDatabaseMetrics(context);

        // Then
        assertEquals("result", result);
        verify(context).proceed();
        verify(recorder, never()).recordDatabaseOperation(any(), any(), anyLong());
    }

    @Test
    @DisplayName("collectDatabaseMetrics records metrics for successful operation")
    void collectDatabaseMetrics_RecordsMetrics_ForSuccessfulOperation() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithAnnotation");
        final String operation = "methodWithAnnotation";
        final Class<? extends MetricsRecord> entity = TestMetricsRecord.class;

        when(context.getMethod()).thenReturn(method);
        when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenReturn("result");

        // When
        final Object result = interceptor.collectDatabaseMetrics(context);

        // Then
        assertEquals("result", result);
        verify(recorder).recordDatabaseOperation(eq(operation), eq(entity), anyLong());
    }

    @Test
    @DisplayName("collectDatabaseMetrics records metrics even when exception occurs")
    void collectDatabaseMetrics_RecordsMetrics_EvenWhenExceptionOccurs() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithAnnotation");
        final String operation = "methodWithAnnotation";
        final Class<? extends MetricsRecord> entity = TestMetricsRecord.class;
        final RuntimeException exception = new RuntimeException("Test exception");

        when(context.getMethod()).thenReturn(method);
        when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenThrow(exception);

        // When/Then
        try
        {
            interceptor.collectDatabaseMetrics(context);
        }
        catch (final RuntimeException e)
        {
            assertEquals(exception, e);
        }

        verify(recorder).recordDatabaseOperation(eq(operation), eq(entity), anyLong());
    }

    @Test
    @DisplayName("collectDatabaseMetrics finds method-level annotation")
    void collectDatabaseMetrics_FindsMethodLevelAnnotation() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithMethodLevelAnnotation");
        final String operation = "methodWithMethodLevelAnnotation";
        final Class<? extends MetricsRecord> entity = TestMetricsRecord.class;

        when(context.getMethod()).thenReturn(method);
        when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenReturn("result");

        // When
        interceptor.collectDatabaseMetrics(context);

        // Then
        verify(recorder).recordDatabaseOperation(eq(operation), eq(entity), anyLong());
    }

    @Test
    @DisplayName("collectDatabaseMetrics finds class-level annotation when method-level not present")
    void collectDatabaseMetrics_FindsClassLevelAnnotation_WhenMethodLevelNotPresent() throws Exception
    {
        // Given
        final TestTargetWithClassAnnotation target = new TestTargetWithClassAnnotation();
        final Method method = TestTargetWithClassAnnotation.class.getMethod("methodWithoutMethodAnnotation");
        final String operation = "methodWithoutMethodAnnotation";
        final Class<? extends MetricsRecord> entity = TestMetricsRecord.class;

        when(context.getMethod()).thenReturn(method);
        when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenReturn("result");

        // When
        interceptor.collectDatabaseMetrics(context);

        // Then
        verify(recorder).recordDatabaseOperation(eq(operation), eq(entity), anyLong());
    }

    @Test
    @DisplayName("collectDatabaseMetrics measures execution time")
    void collectDatabaseMetrics_MeasuresExecutionTime() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithAnnotation");
        final String operation = "methodWithAnnotation";
        final Class<? extends MetricsRecord> entity = TestMetricsRecord.class;

        when(context.getMethod()).thenReturn(method);
        when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenAnswer(invocation ->
        {
            Thread.sleep(10); // Simulate some work
            return "result";
        });

        // When
        interceptor.collectDatabaseMetrics(context);

        // Then
        verify(recorder).recordDatabaseOperation(eq(operation), eq(entity), anyLong());
    }

    // Test helper classes
    @SuppressWarnings("unused")
    static class TestTargetWithoutAnnotation
    {
        public String methodWithoutAnnotation()
        {
            return "result";
        }
    }

    @DatabaseMetrics(entity = TestMetricsRecord.class)
    @SuppressWarnings("unused")
    static class TestTarget
    {
        public String methodWithAnnotation()
        {
            return "result";
        }

        @DatabaseMetrics(entity = TestMetricsRecord.class)
        public String methodWithMethodLevelAnnotation()
        {
            return "result";
        }
    }

    @DatabaseMetrics(entity = TestMetricsRecord.class)
    @SuppressWarnings("unused")
    static class TestTargetWithClassAnnotation
    {
        public String methodWithoutMethodAnnotation()
        {
            return "result";
        }
    }

    static class TestMetricsRecord implements MetricsRecord
    {
        // Test implementation
    }
}
