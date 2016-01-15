package com.mumfrey.liteloader;

import net.minecraft.network.PacketBuffer;

import com.mumfrey.liteloader.core.CommonPluginChannelListener;

/**
 * Interface for mods which want to use plugin channels
 *
 * @author Adam Mummery-Smith
 */
public interface PluginChannelListener extends LiteMod, CommonPluginChannelListener
{
    /**
     * Called when a custom payload packet arrives on a channel this mod has
     * registered.
     * 
     * @param channel Channel on which the custom payload was received
     * @param data Custom payload data
     */
    public abstract void onCustomPayload(String channel, PacketBuffer data);
}
