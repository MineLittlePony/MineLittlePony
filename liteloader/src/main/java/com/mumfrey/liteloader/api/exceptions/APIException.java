package com.mumfrey.liteloader.api.exceptions;

public abstract class APIException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public APIException()
    {
        super();
    }

    public APIException(String message)
    {
        super(message);
    }

    public APIException(Throwable cause)
    {
        super(cause);
    }

    public APIException(String message, Throwable cause)
    {
        super(message, cause);
    }

}