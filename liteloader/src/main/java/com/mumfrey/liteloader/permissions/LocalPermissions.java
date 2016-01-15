package com.mumfrey.liteloader.permissions;


public class LocalPermissions implements Permissions
{
    @Override
    public boolean getPermissionSet(String permission)
    {
        return true;
    }

    @Override
    public boolean getHasPermission(String permission)
    {
        return true;
    }

    @Override
    public boolean getHasPermission(String permission, boolean defaultValue)
    {
        return defaultValue;
    }

}
