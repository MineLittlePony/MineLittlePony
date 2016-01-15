package com.mumfrey.liteloader;

import net.minecraft.network.play.client.C01PacketChatMessage;

/**
 * Interface for mods which want to monitor outbound chat
 *
 * @author Adam Mummery-Smith
 */
public interface OutboundChatListener extends LiteMod
{
    /**
     * Raised when a new chat packet is created (not necessarily transmitted,
     * something could be trolling us).
     * 
     * @param packet
     * @param message
     */
    public abstract void onSendChatMessage(C01PacketChatMessage packet, String message);
}
