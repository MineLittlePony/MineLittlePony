package com.mumfrey.liteloader;

import net.minecraft.entity.player.EntityPlayerMP;

import com.mojang.authlib.GameProfile;

/**
 * Interface for mods which want to handle players joining and leaving a LAN
 * game (or single player game)
 *
 * @author Adam Mummery-Smith
 */
public interface ServerPlayerListener extends LiteMod
{
    /**
     * Called when a player connects to the server and the EntityPlayerMP
     * instance is created, the player has not logged in at this point and may
     * be disconnected if login fails.
     * 
     * @param player Player attempting to connect
     * @param profile Player's GameProfile from the authentication service
     */
    public abstract void onPlayerConnect(EntityPlayerMP player, GameProfile profile);

    /**
     * Called once the player has successfully logged in and all player
     * variables are initialised and replicated.
     * 
     * @param player Player connected
     */
    public abstract void onPlayerLoggedIn(EntityPlayerMP player);

    /**
     * Called when a player respawns. This event is raised when a player
     * respawns after dying or conquers the end. 
     * 
     * @param player New player instance
     * @param oldPlayer Old player instance being discarded
     * @param newDimension Dimension the player is respawning in
     * @param playerWonTheGame True if the player conquered the end (this
     *      respawn is NOT as the result of a death)
     */
    public abstract void onPlayerRespawn(EntityPlayerMP player, EntityPlayerMP oldPlayer, int newDimension, boolean playerWonTheGame);

    /**
     * Called when a player disconnects from the game
     * 
     * @param player Player disconnecting
     */
    public abstract void onPlayerLogout(EntityPlayerMP player);
}
