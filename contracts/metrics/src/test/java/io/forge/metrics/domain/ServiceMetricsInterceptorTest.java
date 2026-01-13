package io.forge.metrics.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.forge.metrics.domain.dto.MetricsResultIndicator;
import io.forge.metrics.domain.dto.OptionalEmptyResult;
import io.forge.metrics.domain.dto.OptionalPresentResult;
import io.forge.metrics.domain.support.MetricsRecorderResolver;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceMetricsInterceptorTest
{
    @Mock
    private MetricsRecorderResolver recorderResolver;

    @Mock
    private MetricsRecorder recorder;

    @Mock
    private InvocationContext context;

    @Mock
    private java.util.Map<String, Object> contextData;

    @InjectMocks
    private ServiceMetricsInterceptor interceptor;

    @Test
    @DisplayName("collectMetrics proceeds without metrics when no ServiceMetrics annotation")
    void collectMetrics_ProceedsWithoutMetrics_WhenNoServiceMetricsAnnotation() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        when(context.getMethod()).thenReturn(TestTarget.class.getMethod("methodWithoutAnnotation"));
        when(context.getTarget()).thenReturn(target);
        when(context.proceed()).thenReturn("result");

        // When
        final Object result = interceptor.collectMetrics(context);

        // Then
        assertEquals("result", result);
        verify(context).proceed();
        verify(recorderResolver, never()).resolve(any());
    }

    @Test
    @DisplayName("collectMetrics records metrics for MetricsResultIndicator result")
    void collectMetrics_RecordsMetrics_ForMetricsResultIndicatorResult() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithAnnotation");
        final MetricsResultIndicator result = new TestMetricsResult(true, null);

        when(context.getMethod()).thenReturn(method);
        // getTarget() not needed for method-level annotations, but lenient in case interceptor checks it
        lenient().when(context.getTarget()).thenReturn(target);
        when(recorderResolver.resolve(any())).thenReturn(Optional.of(recorder));
        when(context.proceed()).thenReturn(result);

        // When
        final Object actualResult = interceptor.collectMetrics(context);

        // Then
        assertEquals(result, actualResult);
        verify(recorder).recordMetrics(context, result);
        verify(recorder, never()).recordException(any(), any());
    }

    @Test
    @DisplayName("collectMetrics records exception when method throws")
    void collectMetrics_RecordsException_WhenMethodThrows() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithAnnotation");
        final RuntimeException exception = new RuntimeException("Test exception");

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(recorderResolver.resolve(any())).thenReturn(Optional.of(recorder));
        when(context.proceed()).thenThrow(exception);

        // When/Then
        try
        {
            interceptor.collectMetrics(context);
        }
        catch (final RuntimeException e)
        {
            assertEquals(exception, e);
        }

        verify(recorder).recordException(context, exception);
        verify(recorder, never()).recordMetrics(any(), any());
    }

    @Test
    @DisplayName("collectMetrics converts Optional<MetricsResultIndicator> to indicator")
    void collectMetrics_ConvertsOptional_ToIndicator() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithAnnotation");
        final MetricsResultIndicator indicator = new TestMetricsResult(true, null);
        final Optional<MetricsResultIndicator> optionalResult = Optional.of(indicator);

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(recorderResolver.resolve(any())).thenReturn(Optional.of(recorder));
        when(context.proceed()).thenReturn(optionalResult);

        // When
        final Object result = interceptor.collectMetrics(context);

        // Then
        assertEquals(optionalResult, result);
        verify(recorder).recordMetrics(context, indicator);
    }

    @Test
    @DisplayName("collectMetrics converts Optional.empty() to OptionalEmptyResult")
    void collectMetrics_ConvertsOptionalEmpty_ToOptionalEmptyResult() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithAnnotation");
        final Optional<MetricsResultIndicator> emptyOptional = Optional.empty();

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(recorderResolver.resolve(any())).thenReturn(Optional.of(recorder));
        when(context.proceed()).thenReturn(emptyOptional);

        // When
        final Object result = interceptor.collectMetrics(context);

        // Then
        assertEquals(emptyOptional, result);
        verify(recorder).recordMetrics(context, OptionalEmptyResult.INSTANCE);
    }

    @Test
    @DisplayName("collectMetrics converts Optional<NonIndicator> to OptionalPresentResult")
    void collectMetrics_ConvertsOptionalNonIndicator_ToOptionalPresentResult() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("methodWithAnnotation");
        final Optional<String> optionalString = Optional.of("test");

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(recorderResolver.resolve(any())).thenReturn(Optional.of(recorder));
        when(context.proceed()).thenReturn(optionalString);

        // When
        final Object result = interceptor.collectMetrics(context);

        // Then
        assertEquals(optionalString, result);
        verify(recorder).recordMetrics(context, OptionalPresentResult.INSTANCE);
    }

    @Test
    @DisplayName("collectMetrics records for void methods")
    void collectMetrics_Records_ForVoidMethods() throws Exception
    {
        // Given
        final TestTarget target = new TestTarget();
        final Method method = TestTarget.class.getMethod("voidMethodWithAnnotation");

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(recorderResolver.resolve(any())).thenReturn(Optional.of(recorder));
        when(context.proceed()).thenReturn(null);

        // When
        interceptor.collectMetrics(context);

        // Then
        verify(recorder).recordMetrics(any(), eq(null));
    }

    @Test
    @DisplayName("collectMetrics puts type in context when annotation has type")
    void collectMetrics_PutsTypeInContext_WhenAnnotationHasType() throws Exception
    {
        // Given - need a method with type annotation
        final TestTargetWithType target = new TestTargetWithType();
        final Method method = TestTargetWithType.class.getMethod("methodWithType");
        final MetricsResultIndicator result = new TestMetricsResult(true, null);

        when(context.getMethod()).thenReturn(method);
        lenient().when(context.getTarget()).thenReturn(target);
        when(context.getContextData()).thenReturn(contextData);
        when(recorderResolver.resolve(any())).thenReturn(Optional.of(recorder));
        when(context.proceed()).thenReturn(result);

        // When
        interceptor.collectMetrics(context);

        // Then
        verify(contextData).put("metrics.type", "user");
        verify(recorder).recordMetrics(context, result);
    }

    @Test
    @DisplayName("collectMetrics finds class-level annotation when method-level not present")
    void collectMetrics_FindsClassLevelAnnotation_WhenMethodLevelNotPresent() throws Exception
    {
        // Given
        final TestTargetWithClassAnnotation target = new TestTargetWithClassAnnotation();
        final Method method = TestTargetWithClassAnnotation.class.getMethod("methodWithoutMethodAnnotation");
        final MetricsResultIndicator result = new TestMetricsResult(true, null);

        when(context.getMethod()).thenReturn(method);
        when(context.getTarget()).thenReturn(target);
        when(recorderResolver.resolve(any())).thenReturn(Optional.of(recorder));
        when(context.proceed()).thenReturn(result);

        // When
        interceptor.collectMetrics(context);

        // Then
        verify(recorder).recordMetrics(context, result);
    }

    // Test helper classes
    @SuppressWarnings("unused")
    static class TestTarget
    {
        public String methodWithoutAnnotation()
        {
            return "result";
        }

        @ServiceMetrics(TestMetricsRecorder.class)
        public MetricsResultIndicator methodWithAnnotation()
        {
            return new TestMetricsResult(true, null);
        }

        @ServiceMetrics(TestMetricsRecorder.class)
        public void voidMethodWithAnnotation()
        {
            // void method
        }
    }

    @ServiceMetrics(TestMetricsRecorder.class)
    @SuppressWarnings("unused")
    static class TestTargetWithClassAnnotation
    {
        public MetricsResultIndicator methodWithoutMethodAnnotation()
        {
            return new TestMetricsResult(true, null);
        }
    }

    @SuppressWarnings("unused")
    static class TestTargetWithType
    {
        @ServiceMetrics(value = TestMetricsRecorder.class, type = "user")
        public MetricsResultIndicator methodWithType()
        {
            return new TestMetricsResult(true, null);
        }
    }

    record TestMetricsResult(boolean success, String errorMessage) implements MetricsResultIndicator
    {
    }

    static class TestMetricsRecorder implements MetricsRecorder
    {
        @Override
        public void recordMetrics(final InvocationContext context, final MetricsResultIndicator metricsResultIndicator)
        {
            // Test implementation
        }

        @Override
        public void recordException(final InvocationContext context, final Exception exception)
        {
            // Test implementation
        }
    }
}
