package com.mumfrey.liteloader.api;

import com.mumfrey.liteloader.core.InterfaceRegistrationDelegate;

/**
 * LiteLoader Extensible API - Interface Provider
 * 
 * InterfaceProviders are able to advertise and provide Listener interfaces
 * which can be implemented by mods or other Listener-derived classes.
 * 
 * @author Adam Mummery-Smith
 */
public interface InterfaceProvider
{
    /**
     * Base type of Listeners which can consume events provided by this provider
     */
    public abstract Class<? extends Listener> getListenerBaseType();

    /**
     * The provider should call back against the supplied delegate in order to
     * advertise the interfaces it provides.
     * 
     * @param delegate
     */
    public abstract void registerInterfaces(InterfaceRegistrationDelegate delegate);

    /**
     * Initialise this provider, called AFTER enumeration but before binding
     */
    public abstract void initProvider();
}
