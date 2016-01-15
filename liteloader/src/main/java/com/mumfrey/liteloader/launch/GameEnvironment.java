package com.mumfrey.liteloader.launch;

import java.io.File;

public interface GameEnvironment
{
    /**
     * Get the game directory, this is the root directory of the game profile
     * specified by the user in the launcher.
     */
    public abstract File getGameDirectory();

    /**
     * Get the assets directory
     */
    public abstract File getAssetsDirectory();

    /**
     * Get the active profile name
     */
    public abstract String getProfile();

    /**
     * Get the "mods" folder, used to get the base path for enumerators and
     * config for legacy mods.
     */
    public abstract File getModsFolder();
}
