package com.mumfrey.liteloader.interfaces;

import java.util.Collection;
import java.util.Map;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.core.ModInfo;

/**
 * Interface for the enumerator
 * 
 * @author Adam Mummery-Smith
 */
public interface LoaderEnumerator extends ModularEnumerator
{
    /**
     * Perform pre-init tasks (container discovery)
     */
    public abstract void onPreInit();

    /**
     * Perform init tasks (injection and mod discovery)
     */
    public abstract void onInit();

    /**
     * Check API requirements for the supplied container
     * 
     * @param container
     */
    public abstract boolean checkAPIRequirements(LoadableMod<?> container);

    /**
     * Check intra-mod dependencies for the supplied container
     * 
     * @param base
     */
    public abstract boolean checkDependencies(LoadableMod<?> base);

    /**
     * Inflect mod identifier from the supplied mod class
     * 
     * @param modClass
     */
    public abstract String getIdentifier(Class<? extends LiteMod> modClass);

    /**
     * Get the container which the specified mod is loaded from
     * 
     * @param modClass
     */
    public abstract LoadableMod<?> getContainer(Class<? extends LiteMod> modClass);

    /**
     * Get the container for the specified mod identifier
     * 
     * @param identifier
     */
    public abstract LoadableMod<?> getContainer(String identifier);

    /**
     * Get all containers identified at discover-time as disabled
     */
    public abstract Collection<? extends ModInfo<Loadable<?>>> getDisabledContainers();

    /**
     * Get all bad containers
     */
    public abstract Collection<? extends ModInfo<Loadable<?>>> getBadContainers();

    /**
     * @param modClass
     * @param metaDataKey
     * @param defaultValue
     */
    public abstract String getModMetaData(Class<? extends LiteMod> modClass, String metaDataKey, String defaultValue);

    /**
     * Get the total number of mods to load
     */
    public abstract int modsToLoadCount();

    /**
     * Get all mods to load
     */
    public abstract Collection<? extends ModInfo<LoadableMod<?>>> getModsToLoad();

    /**
     * Get all tweakers which were injected
     */
    public abstract Collection<? extends ModInfo<Loadable<?>>> getInjectedTweaks();

    /**
     * Get the shared modlist data
     */
    public abstract Map<String, Map<String, String>> getSharedModList();
}
