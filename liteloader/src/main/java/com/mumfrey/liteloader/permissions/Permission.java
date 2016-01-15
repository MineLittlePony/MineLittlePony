package com.mumfrey.liteloader.permissions;

import java.util.Hashtable;
import java.util.Map;

/**
 * Class which represents a permission node
 *
 * @author Adam Mummery-Smith
 */
public class Permission
{
    /**
     * True if this node is the root of the permission tree 
     */
    private final boolean isRootNode;

    /**
     * True if this node is a wildcard node (can match any query)
     */
    private final boolean isWildcardNode;

    /**
     * Node name
     */
    private final String nodeName;

    /**
     * Nodes which are children of this node
     */
    private final Map<String, Permission> childNodes = new Hashtable<String, Permission>();

    /**
     * Node value 
     */
    private boolean value;

    /**
     * Create a new root node 
     */
    public Permission()
    {
        this.isRootNode     = true;
        this.isWildcardNode = false;
        this.value          = true;
        this.nodeName       = "root";
    }

    /**
     * Create a new child node with the specified name and value
     * 
     * @param permissionName Name of the permission node
     * @param value Initial value for the permission node
     */
    public Permission(String permissionName, boolean value)
    {
        this.isRootNode     = false;
        this.isWildcardNode = "*".equals(permissionName);
        this.value          = value;
        this.nodeName       = permissionName;
    }

    /**
     * Create a new child node with the specified name
     * 
     * @param permissionName Name of the permission node
     */
    public Permission(String permissionName)
    {
        this(permissionName, true);
    }

    /**
     * Get whether this node is a root node (read only)
     */
    public boolean isRoot()
    {
        return this.isRootNode;
    }

    /**
     * Get whether this node is a wildcard node 
     */
    public boolean isWildcard()
    {
        return this.isWildcardNode;
    }

    /**
     * Get the name of this permission node
     */
    public String getName()
    {
        return this.nodeName;
    }

    /**
     * Get the value of this permission node
     */
    public boolean getValue()
    {
        return this.value;
    }

    /**
     * Set the value of this permission node
     * 
     * @param newValue
     */
    public void setValue(boolean newValue)
    {
        this.value = newValue;
    }

    /**
     * Get the specified node name
     * 
     * @param name
     */
    public Permission getPermission(String name)
    {
        Permission fallback = (this.isWildcardNode) ? this : null;

        if (name.indexOf('.') > -1)
        {
            String head = name.substring(0, name.indexOf('.'));
            String tail = name.substring(name.indexOf('.') + 1);

            Permission child = this.getPermission(head);

            if (child != null)
            {
                return child.getPermission(tail);
            }
        }
        else if (this.childNodes.containsKey(name))
        {
            return this.childNodes.get(name);
        }

        for (Permission childPermission : this.childNodes.values())
        {
            if (childPermission.isWildcard())
            {
                return childPermission;
            }
        }

        return fallback;
    }

    /**
     * Set the specified node name
     * 
     * @param name
     */
    public Permission setPermission(String name)
    {
        return this.setPermission(name, true);
    }

    /**
     * Set the specified permission to the specified value
     * 
     * @param name
     * @param value
     */
    public Permission setPermission(String name, boolean value)
    {
        if (name.indexOf('.') > -1)
        {
            String head = name.substring(0, name.indexOf('.'));
            String tail = name.substring(name.indexOf('.') + 1);

            Permission child = this.setPermission(head, false);
            return child.setPermission(tail, value);
        }

        Permission child = this.getPermission(name);

        if (child == null || child.isWildcard())
        {
            child = new Permission(name, value);
            this.childNodes.put(child.getName(), child);
        }
        else
        {
            child.setValue(value | child.value);
        }

        return child;
    }

    /**
     * Sets a permission and also explicitly sets the permission value, this
     * allows negated permissions to be set.
     * 
     * @param name
     * @param value
     */
    public Permission setPermissionAndValue(String name, boolean value)
    {
        Permission permission = this.setPermission(name, value);
        permission.setValue(value);
        return permission;
    }

    /**
     * Check whether the specified permission is set
     * 
     * @param name
     * @param value
     */
    public boolean isSet(String name, boolean value)
    {
        Permission child = this.getPermission(name);
        return child == null ? value : child.getValue();
    }
}
