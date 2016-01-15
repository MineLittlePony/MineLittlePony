package com.mumfrey.liteloader;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;

import com.mojang.realmsclient.dto.RealmsServer;


/**
 * Interface for mods which wish to be notified when the player connects to a
 * server (or local game).
 *
 * @author Adam Mummery-Smith
 */
public interface JoinGameListener extends LiteMod
{
    /**
     * Called on join game
     * 
     * @param netHandler Net handler
     * @param joinGamePacket Join game packet
     * @param serverData ServerData object representing the server being
     *      connected to
     * @param realmsServer If connecting to a realm, a reference to the
     *      RealmsServer object
     */
    public abstract void onJoinGame(INetHandler netHandler, S01PacketJoinGame joinGamePacket, ServerData serverData, RealmsServer realmsServer);
}
