package com.mumfrey.liteloader;

import com.mumfrey.liteloader.permissions.PermissionsManager;
import com.mumfrey.liteloader.permissions.PermissionsManagerClient;

/**
 * Interface for mods which use the ClientPermissions system
 *
 * @author Adam Mummery-Smith
 */
public interface Permissible extends LiteMod
{
    /**
     * Returns the node name of the mod, replicated permissions will be of the
     * form mod.<name>.permission.node so this method must return a valid name
     * for use in permission nodes. This method must also return the same value
     * every time it is called since permissible names are not necessarily
     * cached.
     * 
     * @return Permissible name
     */
    public abstract String getPermissibleModName();

    /**
     * The mod version to replicate to the server
     * 
     * @return Mod version as a float
     */
    public abstract float getPermissibleModVersion();

    /**
     * Called by the permissions manager at initialisation to instruct the mod
     * to populate the list of permissions it supports. This method should call
     * back against the supplied permissions manager to register the permissions
     * to be sent to the server when connecting.
     * 
     * @param permissionsManager Client permissions manager
     */
    public abstract void registerPermissions(PermissionsManagerClient permissionsManager);

    /**
     * Called when the permissions set is cleared
     * 
     * @param manager
     */
    public abstract void onPermissionsCleared(PermissionsManager manager);

    /**
     * Called when the permissions are changed (eg. when new permissions are
     * received from the server)
     * 
     * @param manager
     */
    public abstract void onPermissionsChanged(PermissionsManager manager);
}
