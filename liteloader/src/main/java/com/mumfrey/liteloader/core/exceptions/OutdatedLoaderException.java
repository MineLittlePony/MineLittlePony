package com.mumfrey.liteloader.core.exceptions;

/**
 * Exception thrown when a mod class references a liteloader interface which
 * does not exist, which more than likely means that it requires a more
 * up-to-date version of the loader than is currently installed.
 *
 * @author Adam Mummery-Smith
 */
public class OutdatedLoaderException extends Exception
{
    private static final long serialVersionUID = 8770358290208830747L;

    /**
     * @param missingAPI Name of the referenced class which is missing
     */
    public OutdatedLoaderException(String missingAPI)
    {
        super(missingAPI);
    }
}
