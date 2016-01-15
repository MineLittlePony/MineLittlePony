package com.mumfrey.liteloader.client.overlays;

import java.util.List;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.Timer;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.access.Accessor;
import com.mumfrey.liteloader.transformers.access.Invoker;
import com.mumfrey.liteloader.transformers.access.ObfTableClass;

/**
 * Interface containing injected accessors for Minecraft
 *
 * @author Adam Mummery-Smith
 */
@ObfTableClass(Obf.class)
@Accessor("Minecraft")
public interface IMinecraft
{
    /**
     * Get the timer instance
     */
    @Accessor("timer")
    public abstract Timer getTimer();

    /**
     * Get the "running" flag
     */
    @Accessor("running")
    public abstract boolean isRunning();

    /**
     * Get the default resource packs set
     */
    @Accessor("defaultResourcePacks")
    public abstract List<IResourcePack> getDefaultResourcePacks();

    /**
     * Get the current server address (from connection)
     */
    @Accessor("serverName")
    public abstract String getServerName();

    /**
     * Get the current server port (from connection)
     */
    @Accessor("serverPort")
    public abstract int getServerPort();

    /**
     * Notify the client that the window was resized
     * 
     * @param width
     * @param height
     */
    @Invoker("resize")
    public abstract void onResizeWindow(int width, int height);
}
