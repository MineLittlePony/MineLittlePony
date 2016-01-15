package com.mumfrey.liteloader;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C01PacketChatMessage;

/**
 * Interface for mods which can filter inbound chat
 *
 * @author Adam Mummery-Smith
 */
public interface ServerChatFilter extends LiteMod
{
    /**
     * Chat filter function, return false to filter this packet, true to pass
     * the packet.
     * 
     * @param chatPacket Chat packet to examine
     * @param message Chat message
     * @return True to keep the packet, false to discard
     */
    public abstract boolean onChat(EntityPlayerMP player, C01PacketChatMessage chatPacket, String message);
}
