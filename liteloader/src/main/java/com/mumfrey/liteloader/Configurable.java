package com.mumfrey.liteloader;

import com.mumfrey.liteloader.modconfig.ConfigPanel;

/**
 * Interface for mods which want to provide a configuration panel inside the
 * "mod info" screen.
 *
 * @author Adam Mummery-Smith
 */
public interface Configurable
{
    /**
     * Get the class of the configuration panel to use, the returned class must
     * have a default (no-arg) constructor
     * 
     * @return configuration panel class
     */
    public abstract Class<? extends ConfigPanel> getConfigPanelClass();
}
