package com.mumfrey.liteloader.launch;

import java.io.File;

import com.mumfrey.liteloader.api.manager.APIAdapter;
import com.mumfrey.liteloader.api.manager.APIProvider;
import com.mumfrey.liteloader.core.EnabledModsList;
import com.mumfrey.liteloader.core.LiteLoaderVersion;
import com.mumfrey.liteloader.interfaces.LoaderEnumerator;

/**
 * The Loader Environment, contains accessors for getting information about the
 * current Loader session such as the game directories, profile, and API
 * management classes.
 * 
 * Launch namespace, so loaded by the AppClassLoader
 * 
 * @author Adam Mummery-Smith
 */
public interface LoaderEnvironment extends GameEnvironment
{
    public enum EnvironmentType
    {
        CLIENT,
        DEDICATEDSERVER
    }

    public abstract EnvironmentType getType();

    /**
     * Get the API Adapter, the API Adapter provides functionality for working
     * with all loaded APIs.
     */
    public abstract APIAdapter getAPIAdapter();

    /**
     * Get the API Provider, the API Provider contains API instances for the
     * current session.
     */
    public abstract APIProvider getAPIProvider();

    /**
     * The enabled mods list is a serialisable class which contains information
     * about which mods are enabled/disabled.
     */
    public abstract EnabledModsList getEnabledModsList();

    /**
     * The enumerator manages mod container and class discovery
     */
    public abstract LoaderEnumerator getEnumerator();

    /**
     * Get the version-specific mods folder
     */
    public abstract File getVersionedModsFolder();

    /**
     * Get the configuration base folder
     */
    public abstract File getConfigBaseFolder();

    /**
     * Get the version-agnostic mod config folder
     */
    public abstract File getCommonConfigFolder();

    /**
     * Get the version-specific config folder
     */
    public abstract File getVersionedConfigFolder();

    /**
     * Inflect a versioned configuration path for a specific version
     * 
     * @param version
     */
    public abstract File inflectVersionedConfigPath(LiteLoaderVersion version);

    public abstract boolean addCascadedTweaker(String tweakClass, int priority);

    public abstract ClassTransformerManager getTransformerManager();
}
