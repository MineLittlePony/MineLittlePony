package com.mumfrey.liteloader;

import net.minecraft.entity.player.EntityPlayerMP;

import com.mumfrey.liteloader.core.LiteLoaderEventBroker.ReturnValue;
import com.mumfrey.liteloader.util.Position;

/**
 * Interface for mods which want to monitor or control player movements
 * 
 * @author Adam Mummery-Smith
 */
public interface PlayerMoveListener extends LiteMod
{
    /**
     * Called when a movement/look packet is received from the client. 
     * 
     * @param playerMP Player moving
     * @param from Player's previous recorded position
     * @param to Position the player is attempting to move to
     * @param newPos Set this position to teleport the player to newPos instead
     *      of processing the original move
     * @return false to cancel the event or true to allow the movement to be
     *      processed as normal or newPos to be applied
     */
    public abstract boolean onPlayerMove(EntityPlayerMP playerMP, Position from, Position to, ReturnValue<Position> newPos);
}
