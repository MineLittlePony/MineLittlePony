package com.mumfrey.liteloader.transformers.event;

public class EventAlreadyInjectedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public EventAlreadyInjectedException(String message)
    {
        super(message);
    }

    public EventAlreadyInjectedException(Throwable cause)
    {
        super(cause);
    }

    public EventAlreadyInjectedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
