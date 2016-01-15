package com.mumfrey.liteloader.core.event;

public class EventCancellationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public EventCancellationException()
    {
    }

    public EventCancellationException(String message)
    {
        super(message);
    }

    public EventCancellationException(Throwable cause)
    {
        super(cause);
    }

    public EventCancellationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
