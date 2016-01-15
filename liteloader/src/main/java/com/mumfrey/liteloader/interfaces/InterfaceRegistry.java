package com.mumfrey.liteloader.interfaces;

import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.LiteAPI;

/**
 * 
 * @author Adam Mummery-Smith
 */
public interface InterfaceRegistry
{
    public abstract void registerAPI(LiteAPI api);

    public abstract void registerInterface(InterfaceProvider provider, Class<? extends Listener> interfaceType);

    public abstract void registerInterface(InterfaceProvider provider, Class<? extends Listener> interfaceType, int priority);

    public abstract void registerInterface(InterfaceProvider provider, Class<? extends Listener> interfaceType, int priority, boolean exclusive);
}
