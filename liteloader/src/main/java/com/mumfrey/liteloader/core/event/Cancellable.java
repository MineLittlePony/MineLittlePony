package com.mumfrey.liteloader.core.event;

/**
 * Interface for (potentially) cancellable things :)
 * 
 * @author Adam Mummery-Smith
 */
public interface Cancellable
{
    /**
     * Get whether this is actually cancellable
     */
    public abstract boolean isCancellable();

    /**
     * Get whether this is cancelled
     */
    public abstract boolean isCancelled();

    /**
     * If the object is cancellable, cancels the object, implementors may throw
     * an EventCancellationException if the object is not actually cancellable. 
     * 
     * @throws EventCancellationException (optional) may be thrown if the object
     *      is not actually cancellable
     */
    public abstract void cancel() throws EventCancellationException;
}