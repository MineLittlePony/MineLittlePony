package com.mumfrey.liteloader;

import java.io.File;

import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.modconfig.Exposable;

/**
 * Base interface for mods
 *
 * @author Adam Mummery-Smith
 */
public interface LiteMod extends Exposable, Listener
{
    /**
     * Get the mod version string
     * 
     * @return the mod version as a string
     */
    public abstract String getVersion();

    /**
     * Do startup stuff here, minecraft is not fully initialised when this
     * function is called so mods <b>must not</b> interact with minecraft in any
     * way here.
     * 
     * @param configPath Configuration path to use
     */
    public abstract void init(File configPath);

    /**
     * Called when the loader detects that a version change has happened since
     * this mod was last loaded.
     * 
     * @param version new version
     * @param configPath Path for the new version-specific config
     * @param oldConfigPath Path for the old version-specific config
     */
    public abstract void upgradeSettings(String version, File configPath, File oldConfigPath);
}
