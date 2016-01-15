package com.mumfrey.liteloader.common;

/**
 * @author Adam Mummery-Smith
 */
public abstract class LoadingProgress
{
    private static LoadingProgress instance;

    protected LoadingProgress()
    {
        LoadingProgress.instance = this;
    }

    public static void setEnabled(boolean enabled)
    {
        if (LoadingProgress.instance != null) LoadingProgress.instance._setEnabled(enabled);
    }

    public static void dispose()
    {
        if (LoadingProgress.instance != null) LoadingProgress.instance._dispose();
    }

    public static void incLiteLoaderProgress()
    {
        if (LoadingProgress.instance != null) LoadingProgress.instance._incLiteLoaderProgress();
    }

    public static void setMessage(String format, String... args)
    {
        if (LoadingProgress.instance != null) LoadingProgress.instance._setMessage(String.format(format, (Object[])args));
    }

    public static void setMessage(String message)
    {
        if (LoadingProgress.instance != null) LoadingProgress.instance._setMessage(message);
    }

    public static void incLiteLoaderProgress(String format, String... args)
    {
        if (LoadingProgress.instance != null) LoadingProgress.instance._incLiteLoaderProgress(String.format(format, (Object[])args));
    }

    public static void incLiteLoaderProgress(String message)
    {
        if (LoadingProgress.instance != null) LoadingProgress.instance._incLiteLoaderProgress(message);
    }

    public static void incTotalLiteLoaderProgress(int by)
    {
        if (LoadingProgress.instance != null) LoadingProgress.instance._incTotalLiteLoaderProgress(by);
    }

    protected abstract void _setEnabled(boolean enabled);

    protected abstract void _dispose();

    protected abstract void _incLiteLoaderProgress();

    protected abstract void _setMessage(String message);

    protected abstract void _incLiteLoaderProgress(String message);

    protected abstract void _incTotalLiteLoaderProgress(int by);
}