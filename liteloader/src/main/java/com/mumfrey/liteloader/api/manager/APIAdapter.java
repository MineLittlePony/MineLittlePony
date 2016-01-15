package com.mumfrey.liteloader.api.manager;

import java.util.List;

import com.mumfrey.liteloader.api.CoreProvider;
import com.mumfrey.liteloader.api.LiteAPI;
import com.mumfrey.liteloader.api.Observer;
import com.mumfrey.liteloader.interfaces.InterfaceRegistry;

/**
 * API Adapter provides convenience methods for invoking actions on ALL
 * registered APIs
 * 
 * @author Adam Mummery-Smith
 */
public interface APIAdapter
{
    /**
     * APIs should register their mixin configs and set up the mixin environment
     * here. 
     */
    public abstract void initMixins();
    
    /**
     * Aggregate and return required transformers from all registered APIs
     */
    public abstract List<String> getRequiredTransformers();

    /**
     * Aggregate and return required downstream transformers from all registered
     * APIs
     */
    public abstract List<String> getRequiredDownstreamTransformers();

    /**
     * Register interfaces from all registered APIs with the specified registry 
     */
    public abstract void registerInterfaces(InterfaceRegistry interfaceManager);

    /**
     * Get the CoreProviders for the specified API. Consuming classes should
     * call this method instead of calling API::getCoreProviders() directly
     * since getCoreProviders() should only be invoked once and the resulting
     * collection is cached by the API Adapter
     */
    public abstract List<CoreProvider> getCoreProviders();

    /**
     * Get the observers for the specified API. Consuming classes should call
     * this method instead of calling API::getObservers() directly since
     * getObservers() should only be invoked once and the resulting list is
     * cached by the API Adapter
     * 
     * @param api API to get observers for
     */
    public abstract List<? extends Observer> getObservers(LiteAPI api);

    /**
     * Get the observers for the specified API which implement the specified
     * Observer interface. Always returns a valid list (even if empty) and
     * doesn't return null.
     * 
     * @param api API to get observers for
     * @param observerType type of observer to search for
     */
    public abstract <T extends Observer> List<T> getObservers(LiteAPI api, Class<T> observerType);

    /**
     * Get the observers for all registered APIs which implement the specified
     * Observer interface. Always returns a valid list (even if empty) and
     * doesn't return null. Also includes core providers
     * 
     * @param observerType type of observer to search for
     */
    public abstract <T extends Observer> List<T> getAllObservers(Class<T> observerType);

    /**
     * @param api
     */
    public abstract List<? extends Observer> getPreInitObservers(LiteAPI api);

    /**
     * @param observerType
     */
    public abstract <T extends Observer> List<T> getPreInitObservers(Class<T> observerType);
}
