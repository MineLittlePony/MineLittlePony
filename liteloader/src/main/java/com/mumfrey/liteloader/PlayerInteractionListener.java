package com.mumfrey.liteloader;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

/**
 * Interface for mods which want to observe the player's "interaction" status
 * (player mouse clicks), allows block interaction events to be cancelled.
 * 
 * @author Adam Mummery-Smith
 */
public interface PlayerInteractionListener extends LiteMod
{
    /**
     * Mouse buttons
     */
    public static enum MouseButton
    {
        LEFT,
        RIGHT
    }

    /**
     * Called when the player clicks but does not "hit" a block, the trace
     * position is raytraced to the player's current view distance and
     * represents the block which the player is "looking at". This method is
     * <b>not</b> called when the player right clicks with an empty hand.
     * 
     * @param player Player
     * @param button Mouse button the user clicked
     * @param tracePos Raytraced location of the block which was hit
     * @param traceSideHit Raytraced side hit
     * @param traceHitType Type of hit, will be MISS if the trace expired
     *      without hitting anything (eg. the player clicked the sky)
     */
    public abstract void onPlayerClickedAir(EntityPlayerMP player, MouseButton button, BlockPos tracePos, EnumFacing traceSideHit,
            MovingObjectType traceHitType);

    /**
     * Calls when the player clicks and hits a block, usually indicates that the
     * player is digging or placing a block, although a block placement does not
     * necessarily succeed. Return true from this callback to allow the action
     * to proceed, or false to cancel the action. Cancelling the action does not
     * prevent further handlers from receiving the event.
     * 
     * @param player Player
     * @param button Mouse button that was pressed, left = dig, right = interact
     * @param hitPos Block which was *hit*. Note that block placement will
     *      normally be at hitPos.offset(sideHit) 
     * @param sideHit Side of the block which was hit
     * @return true to allow the action to be processed (another listener may
     *      still inhibit the action), return false to cancel the action (other
     *      listeners will still be notified)
     */
    public abstract boolean onPlayerClickedBlock(EntityPlayerMP player, MouseButton button, BlockPos hitPos, EnumFacing sideHit);
}
