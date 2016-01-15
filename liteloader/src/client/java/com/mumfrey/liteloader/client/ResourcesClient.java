package com.mumfrey.liteloader.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;

import com.mumfrey.liteloader.client.overlays.IMinecraft;
import com.mumfrey.liteloader.common.LoadingProgress;
import com.mumfrey.liteloader.common.Resources;

public class ResourcesClient implements Resources<IResourceManager, IResourcePack>
{
    private final Minecraft engine = Minecraft.getMinecraft();

    /**
     * Registered resource packs 
     */
    private final Map<String, IResourcePack> registeredResourcePacks = new HashMap<String, IResourcePack>();

    /**
     * True while initialising mods if we need to do a resource manager reload
     * once the process is completed.
     */
    private boolean pendingResourceReload;

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.common.GameEngine#refreshResources(boolean)
     */
    @Override
    public void refreshResources(boolean force)
    {
        if (this.pendingResourceReload || force)
        {
            LoadingProgress.setMessage("Reloading Resources...");
            this.pendingResourceReload = false;
            this.engine.refreshResources();
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.common.GameEngine#getResourceManager()
     */
    @Override
    public IResourceManager getResourceManager()
    {
        return this.engine.getResourceManager();
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.common.GameEngine#registerResourcePack(
     *      net.minecraft.client.resources.IResourcePack)
     */
    @Override
    public boolean registerResourcePack(IResourcePack resourcePack)
    {
        if (!this.registeredResourcePacks.containsKey(resourcePack.getPackName()))
        {
            this.pendingResourceReload = true;

            List<IResourcePack> defaultResourcePacks = ((IMinecraft)this.engine).getDefaultResourcePacks();
            if (!defaultResourcePacks.contains(resourcePack))
            {
                defaultResourcePacks.add(resourcePack);
                this.registeredResourcePacks.put(resourcePack.getPackName(), resourcePack);
                return true;
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.common.GameEngine#unRegisterResourcePack(
     *      net.minecraft.client.resources.IResourcePack)
     */
    @Override
    public boolean unRegisterResourcePack(IResourcePack resourcePack)
    {
        if (this.registeredResourcePacks.containsValue(resourcePack))
        {
            this.pendingResourceReload = true;

            List<IResourcePack> defaultResourcePacks = ((IMinecraft)this.engine).getDefaultResourcePacks();
            this.registeredResourcePacks.remove(resourcePack.getPackName());
            defaultResourcePacks.remove(resourcePack);
            return true;
        }

        return false;
    }
}
