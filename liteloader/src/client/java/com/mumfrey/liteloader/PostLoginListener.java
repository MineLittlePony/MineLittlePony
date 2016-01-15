package com.mumfrey.liteloader;

import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.server.S02PacketLoginSuccess;

/**
 *
 * @author Adam Mummery-Smith
 */
public interface PostLoginListener extends LiteMod
{
    /**
     * Called immediately after login, before the player has properly joined the
     * game. Note that this event is raised <b>in the network thread</b> and is
     * not marshalled to the main thread as other packet-generated events are.
     * 
     * @param netHandler
     * @param packet
     */
    public abstract void onPostLogin(INetHandlerLoginClient netHandler, S02PacketLoginSuccess packet);
}
