package com.mumfrey.liteloader.api.exceptions;

public class InvalidAPIException extends APIException
{
    private static final long serialVersionUID = 1L;

    public InvalidAPIException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidAPIException(String message)
    {
        super(message);
    }
}
