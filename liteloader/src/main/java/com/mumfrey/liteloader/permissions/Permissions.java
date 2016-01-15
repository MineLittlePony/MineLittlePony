package com.mumfrey.liteloader.permissions;

/**
 * Represents a set of permissions assigned by an authority
 *
 * @author Adam Mummery-Smith
 */
public interface Permissions
{
    /**
     * Returns true if the specified permission is set in this permission
     * container.
     * 
     * @param permission Name of the permission to test for
     * @return True if the permission exists in this set
     */
    public abstract boolean getPermissionSet(String permission);

    /**
     * Returns true if the authority says we have this permission or false if
     * the permission is denied or not set.
     * 
     * @param permission Name of the permission to test for
     */
    public abstract boolean getHasPermission(String permission);

    /**
     * Returns true if the authority says we have this permission or if the
     * permission is not specified by the authority returns the default value.
     * 
     * @param permission Name of the permission to test for
     * @param defaultValue Value to return if the permission is NOT specified by
     *      the authority
     * @return State of the authority permission or default value if not
     *      specified
     */
    public abstract boolean getHasPermission(String permission, boolean defaultValue);
}
