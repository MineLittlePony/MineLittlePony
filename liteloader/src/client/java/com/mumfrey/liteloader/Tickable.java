package com.mumfrey.liteloader;

import net.minecraft.client.Minecraft;

/**
 * Interface for mods which want tick events
 *
 * @author Adam Mummery-Smith
 */
public interface Tickable extends LiteMod
{
    /**
     * Called every frame
     * 
     * @param minecraft Minecraft instance
     * @param partialTicks Partial tick value 
     * @param inGame True if in-game, false if in the menu
     * @param clock True if this is a new tick, otherwise false if it's a
     *      regular frame
     */
    public abstract void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock);
}
