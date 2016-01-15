package com.mumfrey.liteloader.transformers;

/**
 *
 * @author Adam Mummery-Smith
 */
public class InvalidOverlayException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public InvalidOverlayException(String message)
    {
        super(message);
    }

    public InvalidOverlayException(Throwable cause)
    {
        super(cause);
    }

    public InvalidOverlayException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
