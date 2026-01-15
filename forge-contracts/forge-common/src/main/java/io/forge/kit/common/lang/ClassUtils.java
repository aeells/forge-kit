package io.forge.kit.common.lang;

public final class ClassUtils
{
    private ClassUtils()
    {
    }

    public static boolean exists(final String className)
    {
        try
        {
            Class.forName(className);
            return true;
        }
        catch (final ClassNotFoundException e)
        {
            return false;
        }
    }
}
