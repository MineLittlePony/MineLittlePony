package com.mumfrey.liteloader.launch;

import java.util.List;

import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * Interface for the loader bootstrap, this is loaded in the parent classloader
 * for convenience otherwise it would be necessary to call the initialisation
 * functions using reflection which just gets boring very quickly.
 * 
 * @author Adam Mummery-Smith
 */
public interface LoaderBootstrap
{
    /**
     * Pre-init, perform mod file discovery and initial setup (eg. logger,
     * properties)
     * 
     * @param classLoader
     * @param loadTweaks
     * @param modsToLoad
     */
    public abstract void preInit(LaunchClassLoader classLoader, boolean loadTweaks, List<String> modsToLoad);

    /**
     * 
     */
    public abstract void preBeginGame();

    /**
     * Init, create the loader instance and load mods
     */
    public abstract void init();

    /**
     * Post-init, initialise loaded mods
     */
    public abstract void postInit();

    public abstract List<String> getRequiredTransformers();

    public abstract List<String> getRequiredDownstreamTransformers();

    public abstract LoaderEnvironment getEnvironment();

    public abstract LoaderProperties getProperties();
}
