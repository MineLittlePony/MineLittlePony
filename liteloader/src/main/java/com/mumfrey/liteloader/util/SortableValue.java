package com.mumfrey.liteloader.util;

/**
 * Struct which contains a mapping of a priority value to another object, used
 * for sorting items using the native functionality in TreeMap and TreeSet.
 * 
 * @author Adam Mummery-Smith
 */
public class SortableValue<T> implements Comparable<SortableValue<?>>
{
    private final int priority;

    private final int order;

    private final T value;

    public SortableValue(int priority, int order, T value)
    {
        this.priority = priority;
        this.order = order;
        this.value = value;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public int getOrder()
    {
        return this.order;
    }

    public T getValue()
    {
        return this.value;
    }

    @Override
    public int compareTo(SortableValue<?> other)
    {
        if (other == null) return 0;
        if (other.priority == this.priority) return this.order - other.order;
        return (this.priority - other.priority);
    }
}
