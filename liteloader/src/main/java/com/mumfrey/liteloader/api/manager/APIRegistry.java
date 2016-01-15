package com.mumfrey.liteloader.api.manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.launchwrapper.Launch;

import com.mumfrey.liteloader.api.LiteAPI;
import com.mumfrey.liteloader.api.exceptions.InvalidAPIStateException;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

/**
 * This is where we register API classes during early startup before baking the
 * registered list into an APIProvider instance
 *
 * @author Adam Mummery-Smith
 */
public final class APIRegistry
{
    private Set<String> registeredAPIClasses = new LinkedHashSet<String>();
    private Map<String, LiteAPI> instances = new LinkedHashMap<String, LiteAPI>();

    private final LoaderEnvironment environment;

    private final LoaderProperties properties;

    private APIProviderBasic baked;

    /**
     * @param environment
     * @param properties
     */
    public APIRegistry(LoaderEnvironment environment, LoaderProperties properties)
    {
        this.environment = environment;
        this.properties = properties;
    }

    /**
     * Register an API class, throws an exception if the API list has already
     * been baked.
     * 
     * @param apiClass
     */
    public void registerAPI(String apiClass) throws InvalidAPIStateException
    {
        if (this.baked != null)
        {
            throw new InvalidAPIStateException("Unable to register API provider '" + apiClass
                    + "' because the API state is now frozen, this probably means you are registering an API too late in the initialisation process");
        }

        if (!this.registeredAPIClasses.contains(apiClass))
        {
            LiteLoaderLogger.info(Verbosity.REDUCED, "Registering API provider class %s", apiClass);
            this.registeredAPIClasses.add(apiClass);
        }
    }

    /**
     * Get all currently registered API classes
     */
    public String[] getRegisteredAPIs()
    {
        return this.registeredAPIClasses.toArray(new String[0]);
    }

    /**
     * @param apiClassName
     */
    private LiteAPI spawnAPI(String apiClassName)
    {
        try
        {
            LiteLoaderLogger.info("Spawning API provider class '%s' ...", apiClassName);

            @SuppressWarnings("unchecked")
            Class<? extends LiteAPI> apiClass = (Class<? extends LiteAPI>)Class.forName(apiClassName, true, Launch.classLoader);

            LiteAPI api = apiClass.newInstance();
            String identifier = api.getIdentifier();

            if (!this.instances.containsKey(identifier))
            {
                LiteLoaderLogger.info(Verbosity.REDUCED, "API provider class '%s' provides API '%s'", apiClassName, identifier);
                this.instances.put(identifier, api);
                return api;
            }

            Class<? extends LiteAPI> conflictingAPIClass = this.instances.get(identifier).getClass();
            LiteLoaderLogger.severe("API identifier clash while registering '%s', identifier '%s' clashes with '%s'", apiClassName,
                    identifier, conflictingAPIClass);
        }
        catch (ClassNotFoundException ex)
        {
            LiteLoaderLogger.severe("API class '%s' could not be created, the specified class could not be loaded", apiClassName);
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.severe(ex, "Error while instancing API class '%s'", apiClassName);
        }

        return null;
    }

    /**
     * Populate and return the API instance array
     */
    private LiteAPI[] getAllAPIs()
    {
        List<LiteAPI> allAPIs = new ArrayList<LiteAPI>();

        for (String apiClass : this.registeredAPIClasses)
        {
            LiteAPI api = this.spawnAPI(apiClass);
            if (api != null)
            {
                allAPIs.add(api);
            }
        }

        for (LiteAPI api : allAPIs)
        {
            LiteLoaderLogger.info(Verbosity.REDUCED, "Initialising API '%s' ...", api.getIdentifier());
            api.init(this.environment, this.properties);
        }

        return allAPIs.toArray(new LiteAPI[allAPIs.size()]);
    }

    /**
     * Bakes all currently registered API classes to a new APIProvider
     * containing the API instances.
     * 
     * @throws InvalidAPIStateException if the API list was already baked
     */
    public APIProvider bake() throws InvalidAPIStateException
    {
        if (this.baked != null)
        {
            throw new InvalidAPIStateException("Unable to bake the API provider list because the API list is already baked");
        }

        LiteAPI[] apis = this.getAllAPIs();
        return this.baked = new APIProviderBasic(apis);
    }

    /**
     * Gets the current APIProvider instance
     * @throws InvalidAPIStateException if the provider list was not yet baked
     */
    public APIProvider getProvider() throws InvalidAPIStateException
    {
        if (this.baked == null)
        {
            throw new InvalidAPIStateException("Call to APIRegistry.getProvider() failed because the provider list has not been baked");
        }

        return this.baked;
    }

    /**
     * Gets the current APIAdapter instance
     * @throws InvalidAPIStateException if the provider list was not yet baked
     */
    public APIAdapter getAdapter() throws InvalidAPIStateException
    {
        if (this.baked == null)
        {
            throw new InvalidAPIStateException("Call to APIRegistry.APIAdapter() failed because the provider list has not been baked");
        }

        return this.baked;
    }
}
