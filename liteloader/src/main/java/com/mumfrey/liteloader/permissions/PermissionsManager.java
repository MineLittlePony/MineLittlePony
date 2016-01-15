package com.mumfrey.liteloader.permissions;

import com.mumfrey.liteloader.Permissible;
import com.mumfrey.liteloader.common.GameEngine;

/**
 * Interface for permissions manager implementations
 * 
 * @author Adam Mummery-Smith
 */
public interface PermissionsManager
{
    /**
     * Get the underlying permissions node for this manager for the specified
     * mod
     * 
     * @param mod Mod to fetch permissions for
     */
    public abstract Permissions getPermissions(Permissible mod);

    /**
     * Get the time the permissions for the specified mod were last updated
     * 
     * @param mod Mod to check for
     * @return Timestamp when the permissions were last updated
     */
    public abstract Long getPermissionUpdateTime(Permissible mod);

    /**
     * Handler for tick event
     * 
     * @param engine
     * @param partialTicks
     * @param inGame
     */
    public abstract void onTick(GameEngine<?, ?> engine, float partialTicks, boolean inGame);

    /**
     * Register a new event listener, the registered object will receive
     * callbacks for permissions events
     * 
     * @param permissible
     */
    public abstract void registerPermissible(Permissible permissible);

    /**
     * Perform any necessary validation to check for a tamper condition, can and
     * should be called from as many places as possible 
     */
    public abstract void tamperCheck();
}
