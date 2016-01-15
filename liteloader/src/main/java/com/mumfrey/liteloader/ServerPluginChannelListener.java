package com.mumfrey.liteloader;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

import com.mumfrey.liteloader.core.CommonPluginChannelListener;

/**
 * Interface for mods which want to use plugin channels on the (integrated)
 * server side.
 *
 * @author Adam Mummery-Smith
 */
public interface ServerPluginChannelListener extends CommonPluginChannelListener
{
    /**
     * Called when a custom payload packet arrives on a channel this mod has
     * registered.
     *
     * @param sender Player object which is the source of this message
     * @param channel Channel on which the custom payload was received
     * @param data Custom payload data
     */
    public abstract void onCustomPayload(EntityPlayerMP sender, String channel, PacketBuffer data);
}
