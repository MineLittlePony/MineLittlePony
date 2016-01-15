package com.mumfrey.liteloader.api;

import java.io.File;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.interfaces.LoadableMod;

/**
 * LiteLoader Extensible API - Mod Load Observer
 * 
 * ModLoadObservers receive callbacks when mod loading events are occurring,
 * prior to init and other loader-managed processes.
 * 
 * @author Adam Mummery-Smith
 */
public interface ModLoadObserver extends Observer
{
    /**
     * Called immediately after a mod instance is created, throw an exception
     * from this method in order to prevent further initialisation.
     */
    public abstract void onModLoaded(LiteMod mod);

    /**
     * Called after a mod is instanced and has been successfully added to the
     * active mods list. 
     * 
     * @param handle Mod handle
     */
    public abstract void onPostModLoaded(ModInfo<LoadableMod<?>> handle);

    /**
     * Called if mod loading fails
     * 
     * @param container
     * @param identifier
     * @param reason
     * @param th
     */
    public abstract void onModLoadFailed(LoadableMod<?> container, String identifier, String reason, Throwable th);

    /**
     * Called before a mod's init() method is called
     * 
     * @param mod
     */
    public abstract void onPreInitMod(LiteMod mod);

    /**
     * Called after a mod's init() method is called
     * 
     * @param mod
     */
    public abstract void onPostInitMod(LiteMod mod);

    /**
     * Called when migrating mod config from version to version
     * 
     * @param mod
     * @param newConfigPath
     * @param oldConfigPath
     */
    public abstract void onMigrateModConfig(LiteMod mod, File newConfigPath, File oldConfigPath);
}
