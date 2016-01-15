package com.mumfrey.liteloader;

import net.minecraft.command.ServerCommandManager;


/**
 * Interface for mods which provide commands to the local integrated server
 *
 * @author Adam Mummery-Smith
 */
public interface ServerCommandProvider extends LiteMod
{
    /**
     * Allows the mod to provide commands to the server command manager by
     * invoking commandManager.registerCommand() to provide new commands for
     * single player and lan worlds
     * 
     * @param commandManager
     */
    public abstract void provideCommands(ServerCommandManager commandManager);
}
