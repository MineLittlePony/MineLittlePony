package com.mumfrey.liteloader.api;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;

import com.mumfrey.liteloader.common.GameEngine;
import com.mumfrey.liteloader.core.LiteLoaderMods;

/**
 * LiteLoader Extensible API - API Core Provider
 * 
 * Core Providers are objects whose lifecycle is equivalent to the run time of
 * game and thus the entire lifecycle of your API, they are instanced early in
 * the loader startup process. CoreProviders can implement any Observer
 * interface as appropriate and are automatically considered when allocating
 * Observers to callback lists.
 * 
 * @author Adam Mummery-Smith
 */
public interface CoreProvider extends TickObserver, WorldObserver, ShutdownObserver, PostRenderObserver
{
    public abstract void onInit();

    /**
     * During the postInit phase, the mods which were discovered during preInit
     * phase are initialised and the interfaces are allocated. This callback is
     * invoked at the very start of the postInit phase, before mods are
     * initialised but after the point at which it is safe to assume it's ok to
     * access game classes. This is the first point at which the Minecraft game
     * instance should be referenced. Be aware that certain game classes (such
     * as the EntityRenderer) are NOT initialised at this point.
     * 
     * @param engine
     */
    public abstract void onPostInit(GameEngine<?, ?> engine);

    /**
     * Once the mods are initialised and the interfaces have been allocated,
     * this callback is invoked to allow the CoreProvider to perform any tasks
     * which should be performed in the postInit phase but after mods have been
     * initialised.
     *  
     * @param mods
     */
    public abstract void onPostInitComplete(LiteLoaderMods mods);

    /**
     * Called once startup is complete and the game loop begins running. This
     * callback is invoked immediately prior to the first "tick" event and
     * immediately <b>after</b> the the "late init" phase for mods
     * (InitCompleteListener).
     */
    public abstract void onStartupComplete();

    /**
     * Called immediately on joining a single or multi-player world when the
     * JoinGame packet is received. Only called on the client.
     * 
     * @param netHandler
     * @param loginPacket
     */
    public abstract void onJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket);
}
