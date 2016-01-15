package com.mumfrey.liteloader;

/**
 * Interface for mods which want to filter outbound chat
 *
 * @author Adam Mummery-Smith
 */
public interface OutboundChatFilter extends LiteMod
{
    /**
     * Raised when a chat message is being sent, return false to filter this
     * message or true to allow it to be sent.
     * 
     * @param message
     */
    public abstract boolean onSendChatMessage(String message); 
}
