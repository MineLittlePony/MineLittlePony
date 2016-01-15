package com.mumfrey.liteloader;

import com.mumfrey.liteloader.core.LiteLoaderEventBroker.ReturnValue;

import net.minecraft.util.IChatComponent;


/**
 * Interface for mods which can filter inbound chat
 *
 * @author Adam Mummery-Smith
 */
public interface ChatFilter extends LiteMod
{
    /**
     * Chat filter function, return false to filter this packet, true to pass
     * the packet.
     * 
     * @param chat ChatMessageComponent parsed from the chat packet
     * @param message Chat message parsed from the chat message component
     * @param newMessage If you wish to mutate the message, set the value using
     *      newMessage.set()
     * 
     * @return True to keep the packet, false to discard
     */
    public abstract boolean onChat(IChatComponent chat, String message, ReturnValue<IChatComponent> newMessage);
}
