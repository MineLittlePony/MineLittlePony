package com.mumfrey.liteloader.core;

import com.mumfrey.liteloader.core.api.LoadableModFile;
import com.mumfrey.liteloader.interfaces.Loadable;

/**
 * ModInfo for invalid containers
 * 
 * @author Adam Mummery-Smith
 */
public class BadContainerInfo extends NonMod
{
    /**
     * Reason the container could not be loaded 
     */
    private final String reason;

    public BadContainerInfo(Loadable<?> container, String reason)
    {
        super(container, false);
        this.reason = reason;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#isToggleable()
     */
    @Override
    public boolean isToggleable()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#isValid()
     */
    @Override
    public boolean isValid()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getDescription()
     */
    @Override
    public String getDescription()
    {
        return "\247c" + this.reason;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ModInfo#getVersion()
     */
    @Override
    public String getVersion()
    {
        if (this.container instanceof LoadableModFile)
        {
            return "supported: \247c" + ((LoadableModFile)this.container).getTargetVersion() + "\247r"; 
        }

        return "supported: \247cUnknown";
    }
}
