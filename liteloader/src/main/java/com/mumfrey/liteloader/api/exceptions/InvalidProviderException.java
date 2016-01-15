package com.mumfrey.liteloader.api.exceptions;

public class InvalidProviderException extends APIException
{
    private static final long serialVersionUID = 1L;

    public InvalidProviderException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidProviderException(String message)
    {
        super(message);
    }
}
