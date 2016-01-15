package com.mumfrey.liteloader.core;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.interfaces.LoadableMod;

/**
 * ModInfo for an active mod instance
 * 
 * @author Adam Mummery-Smith
 */
class Mod extends ModInfo<LoadableMod<?>>
{
    /**
     * Mod class
     */
    private final Class<? extends LiteMod> modClass;

    /**
     * Mod's key identifier, usually the class simplename
     */
    private final String key;

    /**
     * Mod's identifier (from metadata)
     */
    private final String identifier;

    /**
     * Mod instance
     */
    private LiteMod instance;

    /**
     * Mod display name, initially read from metadata then replaced with real
     * name once instanced.
     */
    private String name;

    /**
     * Mod display name, initially read from version then replaced with real
     * version once instanced.
     */
    private String version;

    /**
     * @param container
     * @param modClass
     */
    public Mod(LoadableMod<?> container, Class<? extends LiteMod> modClass)
    {
        this(container, modClass, container != null ? container.getIdentifier() : LiteLoaderEnumerator.getModClassName(modClass));
    }

    /**
     * @param container
     * @param modClass
     * @param identifier
     */
    public Mod(LoadableMod<?> container, Class<? extends LiteMod> modClass, String identifier)
    {
        super(container != null ? container : LoadableMod.NONE, true);

        this.modClass   = modClass;
        this.key        = modClass.getSimpleName();
        this.identifier = identifier.toLowerCase();
        this.name       = this.container.getDisplayName();
        this.version    = this.container.getVersion();
    }

    /**
     * Called by the mod manager to instance the mod
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    LiteMod newInstance() throws InstantiationException, IllegalAccessException
    {
        if (this.instance != null)
        {
            throw new InstantiationException("Attempted to create an instance of " + this.key + " but the instance was already created");
        }

        this.instance = this.modClass.newInstance();

        String name = this.instance.getName();
        if (name != null) this.name = name;

        String version = this.instance.getVersion();
        if (version != null) this.version = version;

        return this.instance;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#isToggleable()
     */
    @Override
    public boolean isToggleable()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getMod()
     */
    @Override
    public LiteMod getMod()
    {
        return this.instance;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getModClass()
     */
    @Override
    public Class<? extends LiteMod> getModClass()
    {
        return this.modClass;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getDisplayName()
     */
    @Override
    public String getDisplayName()
    {
        return this.name;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getVersion()
     */
    @Override
    public String getVersion()
    {
        return this.version;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getModClassName()
     */
    @Override
    public String getModClassName()
    {
        return this.modClass.getName();
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getModClassSimpleName()
     */
    @Override
    public String getModClassSimpleName()
    {
        return this.key;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getIdentifier()
     */
    @Override
    public String getIdentifier()
    {
        return this.identifier;
    }

    /**
     * Get whether any of the valid identifiers match the supplied name
     * 
     * @param name
     */
    public boolean matchesName(String name)
    {
        return (name.equalsIgnoreCase(this.instance.getName()) || name.equalsIgnoreCase(this.identifier) || name.equalsIgnoreCase(this.key));
    }

    /**
     * Get whether ths mod identifier matches the supplied identifier 
     * 
     * @param identifier
     */
    public boolean matchesIdentifier(String identifier)
    {
        return identifier.equalsIgnoreCase(this.identifier);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other)
    {
        if (other == null) return false;
        if (!(other instanceof Mod)) return false;
        return ((Mod)other).key.equals(this.key);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return this.key.hashCode();
    }
}
