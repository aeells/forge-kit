package io.forge.kit.common.logging;

import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

final class LogMethodEntryParameterExtractor
{
    private LogMethodEntryParameterExtractor()
    {}

    static Object[] extractParameterValues(final InvocationContext context, final LogMethodEntry annotation)
    {
        return ArrayUtils.isNotEmpty(annotation.argPaths()) ? extractByPaths(context, annotation.argPaths()) : extractByIndices(context,
            new int[]{0});
    }

    private static Object[] extractByIndices(final InvocationContext context, final int[] indices)
    {
        final Object[] args = context.getParameters();
        return Arrays.stream(indices)
            .mapToObj(index -> (index >= 0 && index < args.length) ? args[index] : "<invalid index>")
            .toArray();
    }

    private static Object[] extractByPaths(final InvocationContext context, final String[] paths)
    {
        final Method method = context.getMethod();
        final Object[] args = context.getParameters();
        final Parameter[] parameters = method.getParameters();

        return Arrays.stream(paths)
            .map(path ->
            {
                try
                {
                    return LogMethodEntryReflectionUtils.extractValueByPath(path, args, parameters);
                }
                catch (Exception e)
                {
                    return "<error: " + e.getMessage() + ">";
                }
            }).toArray();
    }
}
