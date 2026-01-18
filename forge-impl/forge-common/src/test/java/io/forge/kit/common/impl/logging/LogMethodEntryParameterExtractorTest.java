package io.forge.kit.common.impl.logging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.forge.kit.common.api.logging.LogMethodEntry;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LogMethodEntryParameterExtractor Tests")
class LogMethodEntryParameterExtractorTest
{
    private final InvocationContext context = mock(InvocationContext.class);

    private final LogMethodEntry annotation = mock(LogMethodEntry.class);

    @Test
    @DisplayName("extractParameterValues with empty argPaths uses first parameter by default")
    void extractParameterValues_WithEmptyArgPaths_UsesFirstParameter() throws Exception
    {
        final String actorId = "actor-123";
        final Object[] args = {actorId};

        when(annotation.argPaths()).thenReturn(new String[]{});
        when(context.getParameters()).thenReturn(args);

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.length, "Should return one value");
        assertEquals(actorId, result[0], "Should return the first parameter");
    }

    @Test
    @DisplayName("extractParameterValues with null argPaths uses first parameter by default")
    void extractParameterValues_WithNullArgPaths_UsesFirstParameter() throws Exception
    {
        final String actorId = "actor-123";
        final Object[] args = {actorId};

        when(annotation.argPaths()).thenReturn(null);
        when(context.getParameters()).thenReturn(args);

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.length, "Should return one value");
        assertEquals(actorId, result[0], "Should return the first parameter");
    }

    @Test
    @DisplayName("extractParameterValues with argPaths extracts values by path")
    void extractParameterValues_WithArgPaths_ExtractsByPath() throws Exception
    {
        final TestUser user = new TestUser("john.doe", "john@example.com");
        final Object[] args = {user};
        final Method method = TestReflectionHelper.createTestMethod("testMethod", TestUser.class);

        when(annotation.argPaths()).thenReturn(new String[]{"#username"});
        when(context.getMethod()).thenReturn(method);
        when(context.getParameters()).thenReturn(args);

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.length, "Should return one value");
        assertEquals("john.doe", result[0], "Should extract username property");
    }

    @Test
    @DisplayName("extractParameterValues with multiple argPaths extracts all values")
    void extractParameterValues_WithMultipleArgPaths_ExtractsAllValues() throws Exception
    {
        final String actorId = "actor-123";
        final int limit = 10;
        final Object[] args = {actorId, limit};
        final Method method = TestReflectionHelper.createTestMethod("testMethod", String.class, int.class);

        when(annotation.argPaths()).thenReturn(new String[]{"0", "1"});
        when(context.getMethod()).thenReturn(method);
        when(context.getParameters()).thenReturn(args);

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.length, "Should return two values");
        assertEquals(actorId, result[0], "Should return first parameter");
        assertEquals(limit, result[1], "Should return second parameter");
    }

    @Test
    @DisplayName("extractParameterValues with nested property path extracts nested value")
    void extractParameterValues_WithNestedPropertyPath_ExtractsNestedValue() throws Exception
    {
        final TestUser user = new TestUser("john.doe", "john@example.com");
        final TestRequest request = new TestRequest(user, true);
        final Object[] args = {request};
        final Method method = TestReflectionHelper.createTestMethod("testMethod", TestRequest.class);

        when(annotation.argPaths()).thenReturn(new String[]{"#user#username"});
        when(context.getMethod()).thenReturn(method);
        when(context.getParameters()).thenReturn(args);

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.length, "Should return one value");
        assertEquals("john.doe", result[0], "Should extract nested username property");
    }

    @Test
    @DisplayName("extractParameterValues with invalid index returns error message")
    void extractParameterValues_WithInvalidIndex_ReturnsErrorMessage() throws Exception
    {
        final String actorId = "actor-123";
        final Method method = TestReflectionHelper.createTestMethod("testMethod", String.class);

        when(annotation.argPaths()).thenReturn(new String[]{"5"});
        when(context.getMethod()).thenReturn(method);
        when(context.getParameters()).thenReturn(new Object[]{actorId});

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.length, "Should return one value");
        assertTrue(result[0].toString().contains("error"), "Should return error message for invalid index");
    }

    @Test
    @DisplayName("extractParameterValues with invalid property path returns error message")
    void extractParameterValues_WithInvalidPropertyPath_ReturnsErrorMessage() throws Exception
    {
        final TestUser user = new TestUser("john.doe", "john@example.com");
        final Object[] args = {user};
        final Method method = TestReflectionHelper.createTestMethod("testMethod", TestUser.class);

        when(annotation.argPaths()).thenReturn(new String[]{"#nonexistent"});
        when(context.getMethod()).thenReturn(method);
        when(context.getParameters()).thenReturn(args);

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.length, "Should return one value");
        assertTrue(result[0].toString().contains("error"), "Should return error message for invalid property");
        assertTrue(result[0].toString().contains("nonexistent"), "Error message should mention the property");
    }

    @Test
    @DisplayName("extractParameterValues with empty args array handles gracefully")
    void extractParameterValues_WithEmptyArgs_HandlesGracefully() throws Exception
    {
        final Object[] args = {};

        when(annotation.argPaths()).thenReturn(new String[]{});
        when(context.getParameters()).thenReturn(args);

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.length, "Should return one value");
        assertEquals("<invalid index>", result[0], "Should return invalid index message");
    }

    @Test
    @DisplayName("extractParameterValues with multiple parameters and mixed paths works correctly")
    void extractParameterValues_WithMultipleParametersAndMixedPaths_WorksCorrectly() throws Exception
    {
        final TestUser user = new TestUser("john.doe", "john@example.com");
        final int limit = 10;
        final Object[] args = {user, limit};
        final Method method = TestReflectionHelper.createTestMethod("testMethod", TestUser.class, int.class);

        when(annotation.argPaths()).thenReturn(new String[]{"#username", "1"});
        when(context.getMethod()).thenReturn(method);
        when(context.getParameters()).thenReturn(args);

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.length, "Should return two values");
        assertEquals("john.doe", result[0], "Should extract username from first parameter");
        assertEquals(limit, result[1], "Should return second parameter directly");
    }

    @Test
    @DisplayName("extractParameterValues with null intermediate property returns null")
    void extractParameterValues_WithNullIntermediateProperty_ReturnsNull() throws Exception
    {
        final TestRequest request = new TestRequest(null, true);
        final Object[] args = {request};
        final Method method = TestReflectionHelper.createTestMethod("testMethod", TestRequest.class);

        when(annotation.argPaths()).thenReturn(new String[]{"#user#username"});
        when(context.getMethod()).thenReturn(method);
        when(context.getParameters()).thenReturn(args);

        final Object[] result = LogMethodEntryParameterExtractor.extractParameterValues(context, annotation);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.length, "Should return one value");
        assertNull(result[0], "Should return null when intermediate property is null");
    }
}
