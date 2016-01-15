package com.mumfrey.liteloader.transformers.event;

import org.objectweb.asm.Type;

import com.mumfrey.liteloader.core.event.Cancellable;
import com.mumfrey.liteloader.core.event.EventCancellationException;

/**
 * Contains information about an injected event, including the source object and
 * whether the event is cancellable and/or cancelled.
 * 
 * @author Adam Mummery-Smith
 *
 * @param <S> Source object type. For non-static methods this will be the
 *      containing object instance.
 */
public class EventInfo<S> implements Cancellable
{
    protected static final String STRING = "Ljava/lang/String;";
    protected static final String OBJECT = "Ljava/lang/Object;";

    private final String name;

    private final S source;

    private final boolean cancellable;

    private boolean cancelled;

    public EventInfo(String name, S source, boolean cancellable)
    {
        this.name = name;
        this.source = source;
        this.cancellable = cancellable;
    }

    public S getSource()
    {
        return this.source;
    }

    public String getName()
    {
        return this.name;
    }

    protected String getSourceClass()
    {
        return this.source != null ? this.source.getClass().getSimpleName() : null;
    }

    @Override
    public String toString()
    {
        return String.format("EventInfo(TYPE=%s,NAME=%s,SOURCE=%s,CANCELLABLE=%s)", this.getClass().getSimpleName(),
                this.name, this.getSourceClass(), this.cancellable);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.event.Cancellable#isCancellable()
     */
    @Override
    public final boolean isCancellable()
    {
        return this.cancellable;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.transformers.event.Cancellable#isCancelled()
     */
    @Override
    public final boolean isCancelled()
    {
        return this.cancelled;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.transformers.event.Cancellable#cancel()
     */
    @Override
    public void cancel() throws EventCancellationException
    {
        if (!this.cancellable)
        {
            throw new EventCancellationException(String.format("The event %s is not cancellable.", this.name));
        }

        this.cancelled = true;
    }

    protected static String getEventInfoClassName()
    {
        return EventInfo.class.getName();
    }

    /**
     * @param returnType
     */
    protected static String getEventInfoClassName(Type returnType)
    {
        return returnType.equals(Type.VOID_TYPE) ? EventInfo.class.getName() : ReturnEventInfo.class.getName();
    }

    public static String getConstructorDescriptor(Type returnType)
    {
        if (returnType.equals(Type.VOID_TYPE))
        {
            return EventInfo.getConstructorDescriptor();
        }

        if (returnType.getSort() == Type.OBJECT)
        {
            return String.format("(%s%sZ%s)V", EventInfo.STRING, EventInfo.OBJECT, EventInfo.OBJECT);
        }

        return String.format("(%s%sZ%s)V", EventInfo.STRING, EventInfo.OBJECT, returnType.getDescriptor());
    }

    public static String getConstructorDescriptor()
    {
        return String.format("(%s%sZ)V", EventInfo.STRING, EventInfo.OBJECT);
    }

    public static String getIsCancelledMethodName()
    {
        return "isCancelled";
    }

    public static String getIsCancelledMethodSig()
    {
        return "()Z";
    }
}
