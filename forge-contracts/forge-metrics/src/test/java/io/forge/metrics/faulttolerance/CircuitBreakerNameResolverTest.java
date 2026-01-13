package io.forge.metrics.faulttolerance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import io.smallrye.faulttolerance.api.CircuitBreakerName;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerNameResolverTest
{
    @Mock
    private InvocationContext context;

    @Test
    @DisplayName("resolve returns method annotation value when present")
    void resolve_ReturnsMethodAnnotationValue_WhenPresent() throws Exception
    {
        // Given
        final Method method = TestTarget.class.getMethod("methodWithCircuitBreakerName");
        when(context.getMethod()).thenReturn(method);

        // When
        final String result = CircuitBreakerNameResolver.resolve(context);

        // Then
        assertEquals("method-level-name", result);
    }

    @Test
    @DisplayName("resolve returns fully qualified class name with method name when no annotation")
    void resolve_ReturnsFullyQualifiedClassNameWithMethodName_WhenNoAnnotation() throws Exception
    {
        // Given
        final Method method = NoAnnotationTarget.class.getMethod("someMethod");
        when(context.getMethod()).thenReturn(method);

        // When
        final String result = CircuitBreakerNameResolver.resolve(context);

        // Then
        assertEquals(NoAnnotationTarget.class.getName() + "#someMethod", result);
    }

    @Test
    @DisplayName("resolve returns fully qualified class name when method annotation is empty")
    void resolve_ReturnsFullyQualifiedClassName_WhenMethodAnnotationIsEmpty() throws Exception
    {
        // Given
        final Method method = TestTarget.class.getMethod("methodWithEmptyCircuitBreakerName");
        when(context.getMethod()).thenReturn(method);

        // When
        final String result = CircuitBreakerNameResolver.resolve(context);

        // Then
        assertEquals(TestTarget.class.getName() + "#methodWithEmptyCircuitBreakerName", result);
    }

    // Test helper classes
    @SuppressWarnings("unused")
    static class TestTarget
    {
        @CircuitBreakerName("method-level-name")
        public void methodWithCircuitBreakerName()
        {
        }

        @CircuitBreakerName("")
        public void methodWithEmptyCircuitBreakerName()
        {
        }

        public void methodWithoutCircuitBreakerName()
        {
        }
    }

    @SuppressWarnings("unused")
    static class NoAnnotationTarget
    {
        public void someMethod()
        {
        }
    }
}
