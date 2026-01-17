package io.forge.kit.common.logging;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class TestReflectionHelper
{
    /**
     * Helper method to create a test method with specified parameter types.
     */
    public static Method createTestMethod(final String methodName, final Class<?>... paramTypes)
    {
        try
        {
            return TestMethodClass.class.getDeclaredMethod(methodName, paramTypes);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException("Failed to create test method", e);
        }
    }

    /**
     * Helper method to create a Parameter array for testing using actual method reflection.
     */
    public static Parameter[] createParameters(final String... names)
    {
        try
        {
            final Class<?>[] paramTypes = new Class[names.length];
            for (int i = 0; i < names.length; i++)
            {
                paramTypes[i] = String.class;
            }

            @SuppressWarnings("JavaReflectionMemberAccess") final Method method = TestMethodClass.class.getDeclaredMethod("testMethod", paramTypes);

            return method.getParameters();
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException("Failed to create parameters - method not found", e);
        }
    }
}
