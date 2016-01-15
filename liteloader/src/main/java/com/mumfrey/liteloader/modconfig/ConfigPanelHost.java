package com.mumfrey.liteloader.modconfig;

import com.mumfrey.liteloader.LiteMod;

/**
 * Interface for object which can host configuration panels
 * 
 * @author Adam Mummery-Smith
 */
public interface ConfigPanelHost
{
    /**
     * Get the mod instance which owns the panel
     */
    public abstract <TModClass extends LiteMod> TModClass getMod();

    /**
     * Get the width of the configuration panel area
     */
    public abstract int getWidth();

    /**
     * Get the height of the configuration panel area
     */
    public abstract int getHeight();

    /**
     * Notify the panel host that the panel wishes to close
     */
    public abstract void close();

    /**
     * Notify the panel host that the panel wishes to advance to the next panel
     */
//    public abstract void next();

    /**
     * Notify the panel host that the panel wishes to go back to the previous
     * panel.
     */
//    public abstract void previous();
}
