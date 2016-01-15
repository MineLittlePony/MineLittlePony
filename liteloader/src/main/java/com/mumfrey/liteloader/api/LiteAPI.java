package com.mumfrey.liteloader.api;

import java.util.List;

import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;

/**
 * LiteLoader Extensible API - main Mod API
 * 
 * <p>Implementors of this class don't really do anything except provide
 * instances of other classes which make up the API proper. Where possible,
 * instance things as <em>late</em> as possible (eg. do not instance your
 * CoreProviders in the constructor or init()) because it's possible to screw up
 * the game startup if things get loaded out of order, in general it's best to
 * instance things only at the earliest point in time at which they are needed.
 * </p>
 * 
 * @author Adam Mummery-Smith
 */
public interface LiteAPI
{
    /**
     * Initialise this API, the API should do as little processing as possible
     * here, but should also cache the supplied environment and properties
     * instances for later use.
     * 
     * @param environment
     * @param properties
     */
    public abstract void init(LoaderEnvironment environment, LoaderProperties properties);

    /**
     * Get the identifier for this API, the identifier is used to retrieve the
     * API and match it against specified mod API dependencies.
     */
    public abstract String getIdentifier();

    /**
     * Get the friendly name of this API
     */
    public abstract String getName();

    /**
     * Get the human-readable version of the API, can be any value
     */
    public abstract String getVersion();

    /**
     * Get the revision number of this API. Unlike the version number, the
     * revision number should only change when an incompatible change is made to
     * the APIs interfaces, it is also used when a mod specifies an API
     * dependency using the api@revision syntax.
     */
    public abstract int getRevision();
    
    /**
     * Get mixin environment configuration provider for this API, can return
     * null.
     */
    public abstract MixinConfigProvider getMixins();
    
    /**
     * Should return an array of required transformer names, these transformers
     * will be injected UPSTREAM. Can return null.
     */
    public abstract String[] getRequiredTransformers();

    /**
     * Should return an array of required transformer names, these transformers
     * will be injected DOWNSTREAM. Can return null.
     */
    public abstract String[] getRequiredDownstreamTransformers();

    /**
     * Return a mod class prefix supported by this API, can return null if an
     * API just wants to use "LiteMod" as a standard class name prefix
     */
    public abstract String getModClassPrefix();

    /**
     * Should return a list of Enumerator modules to be injected, can return
     * null if the API doesn't want to inject any additonal modules 
     */
    public abstract List<EnumeratorModule> getEnumeratorModules();

    /**
     * Should return a list of CoreProviders for this API, can return null if
     * the API doesn't have any CoreProviders, (almost) guaranteed to only be
     * called once.
     */
    public abstract List<CoreProvider> getCoreProviders();

    /**
     * Should return a list of InterfaceProviders for this API, can return null
     * if the API doesn't have any InterfaceProviders, (almost) guaranteed to
     * only be called once
     */
    public abstract List<InterfaceProvider> getInterfaceProviders();

    /**
     * Should return a list of Observers which are safe to instantiate during
     * pre-init, for example EnumerationObservers. Can return null if the API
     * doesn't have any Observers.
     */
    public abstract List<Observer> getPreInitObservers();

    /**
     * Should return a list of Observers for this API, can return null if the
     * API doesn't have any Observers, (almost) guaranteed to only be called
     * once. This list may include Observers returned by getPreInitObservers if
     * the observers are still required.
     */
    public abstract List<Observer> getObservers();

    /**
     * Get the customisation providers for this API, can return null 
     */
    public abstract List<CustomisationProvider> getCustomisationProviders();
}
