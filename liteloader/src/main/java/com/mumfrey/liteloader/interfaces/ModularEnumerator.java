package com.mumfrey.liteloader.interfaces;

import java.io.File;

import com.mumfrey.liteloader.api.EnumeratorModule;
import com.mumfrey.liteloader.api.EnumeratorPlugin;
import com.mumfrey.liteloader.core.ModInfo;

/**
 * Interface for the mod enumerator
 *
 * @author Adam Mummery-Smith
 */
public interface ModularEnumerator
{
    /**
     * Register a pluggable module into the enumerator
     * 
     * @param module
     */
    public abstract void registerModule(EnumeratorModule module);

    /**
     * Register a plugin into the enumerator
     * 
     * @param plugin
     */
    public abstract void registerPlugin(EnumeratorPlugin plugin);

    /**
     * @param container
     */
    public abstract boolean registerModContainer(LoadableMod<?> container);

    /**
     * @param container
     * @param reason
     */
    public abstract void registerBadContainer(Loadable<?> container, String reason);

    /**
     * @param container
     */
    public abstract boolean registerTweakContainer(TweakContainer<File> container);

    /**
     * @param container
     * @param registerContainer
     */
    public abstract void registerModsFrom(LoadableMod<?> container, boolean registerContainer);

    /**
     * @param mod
     */
    public abstract void registerMod(ModInfo<LoadableMod<?>> mod);
}