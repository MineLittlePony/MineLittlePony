package com.mumfrey.liteloader;

/**
 * Interface for mods that want to receive an event when the game is shutting
 * down due to a user request. They do not receive the callback when the VM is
 * terminating for other reasons, use a regular VM shutdownhook for that.
 * 
 * @author Adam Mummery-Smith
 */
public interface ShutdownListener extends LiteMod
{
    public abstract void onShutDown();
}
