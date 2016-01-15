package com.mumfrey.liteloader.core.exceptions;

public class UnregisteredChannelException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public UnregisteredChannelException(String message)
    {
        super(message);
    }
}
