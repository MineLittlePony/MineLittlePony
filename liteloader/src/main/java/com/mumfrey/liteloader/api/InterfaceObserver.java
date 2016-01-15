package com.mumfrey.liteloader.api;

/**
 * Observer for interface binding events
 * 
 * @author Adam Mummery-Smith
 */
public interface InterfaceObserver extends Observer
{
    public void onRegisterListener(InterfaceProvider provider, Class<? extends Listener> interfaceType, Listener listener);
}
