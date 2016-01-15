package com.mumfrey.liteloader.permissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.eq2online.permissions.ReplicatedPermissionsContainer;
import net.minecraft.network.PacketBuffer;


/**
 * Replicated permissions implementation
 *
 * @author Adam Mummery-Smith
 */
public class ServerPermissions implements ReplicatedPermissions
{
    /**
     * Pattern for recognising valid permissions in the server feed
     */
    private static final Pattern permissionPattern = Pattern.compile("^([\\+\\-])(([a-z0-9]+\\.)*[a-z0-9\\*]+)$", Pattern.CASE_INSENSITIVE);

    protected String modName;

    /**
     * Root permission node
     */
    protected Permission permissions = new Permission();

    /**
     * Time the permissions were updated
     */
    protected long createdTime = 0L;

    /**
     * Expiry time of the current data cache
     */
    protected long validUntil = 0L;

    /**
     * Time to cache server responses by default
     */
    protected long cacheTime = 10L * 60L * 1000L;   // 10 minutes

    /**
     * Time to wait when refreshing server permissions before trying again
     */
    protected long refreshTime = 15L * 1000L;       // 15 seconds

    /**
     * @param data
     */
    public ServerPermissions(PacketBuffer data)
    {
        this.createdTime = System.currentTimeMillis();
        this.validUntil = this.createdTime + this.cacheTime;

        ReplicatedPermissionsContainer response = ReplicatedPermissionsContainer.fromPacketBuffer(data);

        if (response != null)
        {
            response.sanitise();

            this.modName = response.modName;
            this.validUntil = System.currentTimeMillis() + response.remoteCacheTimeSeconds * 1000L; 

            for (String permissionString : response.permissions)
            {
                Matcher permissionMatcher = permissionPattern.matcher(permissionString);

                if (permissionMatcher.matches())
                {
                    String name = permissionMatcher.group(2);
                    boolean value = permissionMatcher.group(1).equals("+");

                    this.permissions.setPermissionAndValue(name, value);
                }
            }
        }
    }

    /**
     * Get the permissible mod name
     */
    public String getModName()
    {
        return this.modName;
    }

    /* (non-Javadoc)
     * @see net.eq2online.permissions.Permissions#getPermissionSet(
     *      java.lang.String)
     */
    @Override
    public boolean getPermissionSet(String permission)
    {
        return this.permissions.getPermission(permission) != null;
    }

    /* (non-Javadoc)
     * @see net.eq2online.permissions.Permissions#getHasPermission(
     *      java.lang.String)
     */
    @Override
    public boolean getHasPermission(String permission)
    {
        Permission perm = this.permissions.getPermission(permission);
        return perm != null && perm.getValue();
    }

    /* (non-Javadoc)
     * @see net.eq2online.permissions.Permissions#getHasPermission(
     *      java.lang.String, boolean)
     */
    @Override
    public boolean getHasPermission(String permission, boolean defaultValue)
    {
        Permission perm = this.permissions.getPermission(permission);

        return perm != null ? perm.getValue() : defaultValue;  
    }

    /* (non-Javadoc)
     * @see net.eq2online.permissions.ReplicatedPermissions#getReplicationTime()
     */
    @Override
    public long getReplicationTime()
    {
        return this.createdTime;
    }

    /* (non-Javadoc)
     * @see net.eq2online.permissions.ReplicatedPermissions#isValid()
     */
    @Override
    public boolean isValid()
    {
        return System.currentTimeMillis() < this.validUntil;
    }

    /* (non-Javadoc)
     * @see net.eq2online.permissions.ReplicatedPermissions#invalidate()
     */
    @Override
    public void invalidate()
    {
        this.validUntil = 0L;
    }

    @Override
    public void notifyRefreshPending()
    {
        this.validUntil = System.currentTimeMillis() + this.refreshTime;
    }
}
