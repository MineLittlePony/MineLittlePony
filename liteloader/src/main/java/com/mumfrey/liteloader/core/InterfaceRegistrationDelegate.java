package com.mumfrey.liteloader.core;

import java.util.ArrayList;
import java.util.List;

import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.core.LiteLoaderInterfaceManager.InterfaceHandler;
import com.mumfrey.liteloader.interfaces.InterfaceRegistry;

/**
 * Delegate passed in to an InterfaceProvider's registerInterfaces method

 * @author Adam Mummery-Smith
 */
public class InterfaceRegistrationDelegate
{
    /**
     * Registry which this delegate is delegating for
     */
    private final InterfaceRegistry registry;

    /**
     * InterfaceProvider being queried 
     */
    private final InterfaceProvider provider;

    /**
     * The registry temporarily stores the list of handlers here
     */
    private final List<InterfaceHandler> handlers = new ArrayList<InterfaceHandler>();

    /**
     * @param registry
     * @param provider
     */
    InterfaceRegistrationDelegate(InterfaceRegistry registry, InterfaceProvider provider)
    {
        this.registry = registry;
        this.provider = provider;
    }

    /**
     * @param handler
     */
    void addHandler(InterfaceHandler handler)
    {
        this.handlers.add(handler);
    }

    /**
     * 
     */
    List<InterfaceHandler> getHandlers()
    {
        return this.handlers;
    }

    /**
     * 
     */
    void registerInterfaces()
    {
        this.provider.registerInterfaces(this);
    }

    /**
     * @param interfaceType
     */
    public void registerInterface(Class<? extends Listener> interfaceType)
    {
        this.registry.registerInterface(this.provider, interfaceType);
    }

    /**
     * @param interfaceType
     * @param priority
     */
    public void registerInterface(Class<? extends Listener> interfaceType, int priority)
    {
        this.registry.registerInterface(this.provider, interfaceType, priority);
    }

    /**
     * @param interfaceType
     * @param priority
     * @param exclusive
     */
    public void registerInterface(Class<? extends Listener> interfaceType, int priority, boolean exclusive)
    {
        this.registry.registerInterface(this.provider, interfaceType, priority, exclusive);
    }
}
