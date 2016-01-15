package com.mumfrey.liteloader.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.api.ModLoadObserver;
import com.mumfrey.liteloader.common.Resources;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.resources.ModResourcePack;
import com.mumfrey.liteloader.resources.ModResourcePackDir;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Observer which handles registering mods on the client as resource packs
 * 
 * @author Adam Mummery-Smith
 */
public class ResourceObserver implements ModLoadObserver
{
    private final Map<String, IResourcePack> resourcePacks = new HashMap<String, IResourcePack>();

    public ResourceObserver()
    {
    }

    @Override
    public void onModLoaded(LiteMod mod)
    {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPostModLoaded(ModInfo<LoadableMod<?>> handle)
    {
        if (!handle.hasContainer()) return;

        LoadableMod<?> container = handle.getContainer();
        String modName = handle.getMod().getName();

        if (modName == null) return;

        if (container.hasResources())
        {
            LiteLoaderLogger.info("Adding \"%s\" to active resource pack set", container.getLocation());
            IResourcePack resourcePack = this.initResourcePack(container, modName);
            Resources<IResourceManager, IResourcePack> resources
                    = (Resources<IResourceManager, IResourcePack>)LiteLoader.getGameEngine().getResources();
            if (resources.registerResourcePack(resourcePack))
            {
                LiteLoaderLogger.info("Successfully added \"%s\" to active resource pack set", container.getLocation());
            }
        }
    }

    public IResourcePack initResourcePack(LoadableMod<?> container, String name)
    {
        IResourcePack resourcePack = this.getResourcePack(container);

        if (resourcePack == null)
        {
            if (container.isDirectory())
            {
                LiteLoaderLogger.info("Setting up \"%s/%s\" as mod resource pack with identifier \"%s\"",
                        container.toFile().getParentFile().getName(), container.getName(), name);
                resourcePack = new ModResourcePackDir(name, container.toFile());
            }
            else
            {
                LiteLoaderLogger.info("Setting up \"%s\" as mod resource pack with identifier \"%s\"", container.getName(), name);
                resourcePack = new ModResourcePack(name, container.toFile());
            }

            this.setResourcePack(container, resourcePack);
        }

        return resourcePack;
    }

    private IResourcePack getResourcePack(LoadableMod<?> container)
    {
        String path = container.getLocation();
        return this.resourcePacks.get(path);
    }

    private void setResourcePack(LoadableMod<?> container, IResourcePack resourcePack)
    {
        String path = container.getLocation();
        this.resourcePacks.put(path, resourcePack);
    }

    @Override
    public void onModLoadFailed(LoadableMod<?> container, String identifier, String reason, Throwable th)
    {
    }

    @Override
    public void onPreInitMod(LiteMod mod)
    {
    }

    @Override
    public void onPostInitMod(LiteMod mod)
    {
    }

    @Override
    public void onMigrateModConfig(LiteMod mod, File newConfigPath, File oldConfigPath)
    {
    }
}
