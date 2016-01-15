package com.mumfrey.liteloader;

import net.minecraft.client.Minecraft;

import com.mumfrey.liteloader.core.LiteLoader;

/**
 * Interface for mods which need to initialise stuff once the game
 * initialisation is completed, for example mods which need to register new
 * renderers.
 *
 * @author Adam Mummery-Smith
 */
public interface InitCompleteListener extends Tickable
{
    /**
     * Called as soon as the game is initialised and the main game loop is
     * running.
     * 
     * @param minecraft Minecraft instance
     * @param loader LiteLoader instance
     */
    public abstract void onInitCompleted(Minecraft minecraft, LiteLoader loader);
}
