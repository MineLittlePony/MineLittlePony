package com.mumfrey.liteloader.interfaces;

import com.mumfrey.liteloader.api.PostRenderObserver;
import com.mumfrey.liteloader.api.TickObserver;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.modconfig.ConfigManager;

/**
 * Interface for the liteloader panel manager, abstracted because we don't have
 * the class GuiScreen on the server.
 * 
 * @author Adam Mummery-Smith
 *
 * @param <TParentScreen> GuiScreen class, must be generic because we don't have
 *      GuiScreen on the server side
 */
public interface PanelManager<TParentScreen> extends TickObserver, PostRenderObserver
{
    /**
     * @param mods
     * @param configManager
     */
    public abstract void init(LiteLoaderMods mods, ConfigManager configManager);

    /**
     * 
     */
    public abstract void onStartupComplete();

    /**
     * Hide the LiteLoader tab
     */
    public abstract void hideTab();

    /**
     * Set the LiteLoader tab's visibility
     */
    public abstract void setTabVisible(boolean show);

    /**
     * Get whether the LiteLoader tab is visible
     */
    public abstract boolean isTabVisible();

    /**
     * Set whether the LiteLoader tab should remain expanded
     */
    public abstract void setTabAlwaysExpanded(boolean expand);

    /**
     * Get whether the LiteLoader tab should remain expanded
     */
    public abstract boolean isTabAlwaysExpanded();

    /**
     * Display the LiteLoader panel
     * 
     * @param parentScreen Parent screen to display the panel on top of
     */
    public abstract void displayLiteLoaderPanel(TParentScreen parentScreen);

    /**
     * Get the number of startup errors
     */
    public abstract int getStartupErrorCount();

    /**
     * Get the number of critical startup errors
     */
    public abstract int getCriticalErrorCount();

    /**
     * Set the current notification text
     */
    public abstract void setNotification(String notification);

    /**
     * Set whether "force update" is enabled
     */
    public abstract void setForceUpdateEnabled(boolean forceUpdate);

    /**
     * Get whether "force update" is enabled
     */
    public abstract boolean isForceUpdateEnabled();
}
