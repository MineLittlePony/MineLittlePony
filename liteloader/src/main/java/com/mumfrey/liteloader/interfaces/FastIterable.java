package com.mumfrey.liteloader.interfaces;

/**
 * Interface for objects which can return a baked list view of their list
 * contents.
 * 
 * @author Adam Mummery-Smith
 *
 * @param <T>
 */
public interface FastIterable<T> extends Iterable<T>
{
    /**
     * Add an entry to the iterable
     * 
     * @param entry
     */
    public boolean add(T entry);

    /**
     * Return the baked view of all entries
     */
    public T all();

    /**
     * Invalidate (force rebake of) the baked entry list
     */
    public void invalidate();
}
