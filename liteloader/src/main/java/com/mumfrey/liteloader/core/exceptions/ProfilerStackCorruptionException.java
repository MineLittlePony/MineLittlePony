package com.mumfrey.liteloader.core.exceptions;

/**
 * Exception to throw when a mod corrupts the profiler stack, this avoids
 * throwing a (somewhat cryptic) NoSuchElementException inside HookProfiler
 *
 * @author Adam Mummery-Smith
 */
public class ProfilerStackCorruptionException extends RuntimeException
{
    private static final long serialVersionUID = -7745831270297368169L;

    public ProfilerStackCorruptionException(String message)
    {
        super(message);
    }
}
