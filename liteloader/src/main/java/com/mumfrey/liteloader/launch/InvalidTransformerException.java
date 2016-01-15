package com.mumfrey.liteloader.launch;

/**
 * Exception thrown from the NonDelegatingClassLoader if a transformer tries to
 * access a class outside of the classes that are allowed for that transformer.
 *
 * @author Adam Mummery-Smith
 */
public class InvalidTransformerException extends ClassNotFoundException
{
    private static final long serialVersionUID = 6723030540814568734L;

    private final String accessedClass;

    public InvalidTransformerException(String accessedClass)
    {
        super("Tried to access " + accessedClass);
        this.accessedClass = accessedClass;
    }

    public String getAccessedClass()
    {
        return this.accessedClass;
    }
}
