package com.mumfrey.liteloader.common;

public interface Resources<TResourceManager, TResourcePack>
{
    /**
     * Refresh resource pack list
     * 
     * @param force
     */
    public abstract void refreshResources(boolean force);

    /**
     * Get the resource manager for the current environment, returns the
     * SimpleReloadableResourceManager on client and ModResourceManager on the
     * server.
     */
    public abstract TResourceManager getResourceManager();

    /**
     * @param resourcePack
     */
    public abstract boolean registerResourcePack(TResourcePack resourcePack);

    /**
     * @param resourcePack
     */
    public abstract boolean unRegisterResourcePack(TResourcePack resourcePack);
}
