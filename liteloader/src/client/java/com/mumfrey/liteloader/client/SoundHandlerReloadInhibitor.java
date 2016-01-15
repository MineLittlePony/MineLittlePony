package com.mumfrey.liteloader.client;

import java.util.List;

import com.mumfrey.liteloader.client.ducks.IReloadable;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;

/**
 * Manager object which handles inhibiting the sound handler's reload
 * notification at startup.
 *
 * @author Adam Mummery-Smith
 */
public class SoundHandlerReloadInhibitor
{
    /**
     * Resource Manager
     */
    private SimpleReloadableResourceManager resourceManager;

    /**
     * Sound manager
     */
    private SoundHandler soundHandler;

    /**
     * True if inhibition is currently active
     */
    private boolean inhibited;

    /**
     * So that we can re-insert the sound manager at the same index, we store
     * the index we remove it from.
     */
    private int storedIndex;

    SoundHandlerReloadInhibitor(SimpleReloadableResourceManager resourceManager, SoundHandler soundHandler)
    {
        this.resourceManager = resourceManager;
        this.soundHandler = soundHandler;
    }

    /**
     * Inhibit the sound manager reload notification
     * 
     * @return true if inhibit was applied
     */
    public boolean inhibit()
    {
        try
        {
            if (!this.inhibited)
            {
                List<IResourceManagerReloadListener> reloadListeners = ((IReloadable)this.resourceManager).getReloadListeners();
                if (reloadListeners != null)
                {
                    this.storedIndex = reloadListeners.indexOf(this.soundHandler);
                    if (this.storedIndex > -1)
                    {
                        LiteLoaderLogger.info("Inhibiting sound handler reload");
                        reloadListeners.remove(this.soundHandler);
                        this.inhibited = true;
                        return true;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.warning("Error inhibiting sound handler reload");
        }

        return false;
    }

    /**
     * Remove the sound manager reload inhibit
     * 
     * @param reload True to reload the sound manager now
     * @return true if the sound manager was successfully restored
     */
    public boolean unInhibit(boolean reload)
    {
        try
        {
            if (this.inhibited)
            {
                List<IResourceManagerReloadListener> reloadListeners = ((IReloadable)this.resourceManager).getReloadListeners();
                if (reloadListeners != null)
                {
                    if (this.storedIndex > -1)
                    {
                        reloadListeners.add(this.storedIndex, this.soundHandler);
                    }
                    else
                    {
                        reloadListeners.add(this.soundHandler);
                    }

                    LiteLoaderLogger.info("Sound handler reload inhibit removed");

                    if (reload)
                    {
                        LiteLoaderLogger.info("Reloading sound handler");
                        this.soundHandler.onResourceManagerReload(this.resourceManager);
                    }

                    this.inhibited = false;
                    return true;
                }
            }
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.warning("Error removing sound handler reload inhibit");
        }

        return false;
    }

    public boolean isInhibited()
    {
        return this.inhibited;
    }
}
