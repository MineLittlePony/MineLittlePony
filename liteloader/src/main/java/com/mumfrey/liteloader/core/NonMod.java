package com.mumfrey.liteloader.core;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.interfaces.Loadable;

/**
 * ModInfo for unloaded containers and injected tweaks 
 * 
 * @author Adam Mummery-Smith
 */
public class NonMod extends ModInfo<Loadable<?>>
{
    /**
     * @param container
     * @param active
     */
    public NonMod(Loadable<?> container, boolean active)
    {
        super(container, active);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getMod()
     */
    @Override
    public LiteMod getMod()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getModClass()
     */
    @Override
    public Class<? extends LiteMod> getModClass()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getModClassName()
     */
    @Override
    public String getModClassName()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getModClassSimpleName()
     */
    @Override
    public String getModClassSimpleName()
    {
        return null;
    }
}
