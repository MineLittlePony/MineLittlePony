package com.mumfrey.liteloader.permissions;

/**
 * Represents a set of permissions assigned by a remote authority such as a
 * server.
 *
 * @author Adam Mummery-Smith
 */
public interface ReplicatedPermissions extends Permissions
{
    /**
     * Get the time that this object was received from the remote authority
     */
    public abstract long getReplicationTime();

    /**
     * Return true if this permissions object is valid (within cache period)
     */
    public abstract boolean isValid();

    /**
     * Forcibly invalidate this permission container, forces update at the next
     * opportunity.
     */
    public abstract void invalidate();

    /**
     * Temporarily forces the permissions object to be valid to prevent repeated
     * revalidation.
     */
    public abstract void notifyRefreshPending();
}
