package com.mumfrey.liteloader.api.manager;

import java.util.regex.Pattern;

import com.mumfrey.liteloader.api.LiteAPI;

/**
 * Interface for the API Provider, which manages API instances and lifecycle
 * 
 * @author Adam Mummery-Smith
 */
public interface APIProvider
{
    public static final Pattern idAndRevisionPattern = Pattern.compile("^([^@]+)@([0-9]{1,5})$");

    /**
     * Get all available API instances in an array
     */
    public abstract LiteAPI[] getAPIs();

    /**
     * Returns true if the specified API is available
     * 
     * @param identifier API identifier (case sensitive) or API
     * identifier-plus-minrevision in the form "identifier@minver"
     */
    public abstract boolean isAPIAvailable(String identifier);

    /**
     * Returns true if the specified API is available
     * 
     * @param identifier API identifier (case sensitive)
     * @param minRevision minimum required revision
     */
    public abstract boolean isAPIAvailable(String identifier, int minRevision);

    /**
     * Gets a specific API by identifier
     * 
     * @param identifier API identifier (case sensitive)
     */
    public abstract LiteAPI getAPI(String identifier);

    /**
     * Gets a specific API by class
     * 
     * @param apiClass
     */
    public abstract <T extends LiteAPI> T getAPI(Class<T> apiClass);
}