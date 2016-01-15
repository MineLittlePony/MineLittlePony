package com.mumfrey.liteloader.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.interfaces.Loadable;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.interfaces.MixinContainer;
import com.mumfrey.liteloader.interfaces.TweakContainer;

/**
 * ModInfo is used to keep runtime information about a mod (or other injectable)
 * together with relevant environmental information (such as startup errors) and
 * its container.
 * 
 * @author Adam Mummery-Smith
 *
 * @param <TContainer> type of container
 */
public abstract class ModInfo<TContainer extends Loadable<?>>
{
    /**
     * List of built-in APIs, used to filter for 3rd-party APIs
     */
    protected static final Set<String> BUILT_IN_APIS = ImmutableSet.of("liteloader");

    /**
     * Container instance
     */
    protected final TContainer container;

    /**
     * True if this mod is active/injected or not active/errored
     */
    protected final boolean active;

    /**
     * Startup errors encountered whilst loading this mod
     */
    private final List<Throwable> startupErrors = new ArrayList<Throwable>();

    /**
     * @param container
     * @param active
     */
    protected ModInfo(TContainer container, boolean active)
    {
        this.container = container;
        this.active = active;
    }

    /**
     * Get whether this mod is currently active
     */
    public final boolean isActive()
    {
        return this.active;
    }

    /**
     * Get whether this mod is valid
     */
    public boolean isValid()
    {
        return true;
    }

    /**
     * Get whether this mod can be toggled
     */
    public boolean isToggleable()
    {
        return this.container.isToggleable();
    }

    /**
     * Get whether this mod has a container
     */
    public final boolean hasContainer()
    {
        return this.container != LoadableMod.NONE;
    }

    /**
     * Get the container for this mod
     */
    public final TContainer getContainer()
    {
        return this.container;
    }

    /**
     * Callback to allow the mod manager to register a startup error
     */
    void registerStartupError(Throwable th)
    {
        this.startupErrors.add(th);
    }

    /**
     * Get startup errors for this instance
     */
    public List<Throwable> getStartupErrors()
    {
        return Collections.unmodifiableList(this.startupErrors);
    }

    /**
     * Get the display name for this mod
     */
    public String getDisplayName()
    {
        return this.container.getDisplayName();
    }

    /**
     * Get the mod version
     */
    public String getVersion()
    {
        return this.container.getVersion();
    }

    /**
     * Get the nod identifier
     */
    public String getIdentifier()
    {
        return this.container.getIdentifier();
    }

    /**
     * Get the mod URL
     */
    public String getURL()
    {
        return this.container instanceof LoadableMod<?> ? ((LoadableMod<?>)this.container).getMetaValue("url", "") : null;
    }

    /**
     * Get the mod author(s)
     */
    public String getAuthor()
    {
        return this.container.getAuthor();
    }

    /**
     * Get the mod description
     */
    public String getDescription()
    {
        return this.container.getDescription(null);
    }

    /**
     * If this container has a tweak
     */
    public boolean hasTweakClass()
    {
        return (this.container instanceof TweakContainer && ((TweakContainer<?>)this.container).hasTweakClass());
    }

    /**
     * If this has transformers (NOT robots in disguise, the other kind)
     */
    public boolean hasClassTransformers()
    {
        return (this.container instanceof TweakContainer && ((TweakContainer<?>)this.container).hasClassTransformers());
    }

    /**
     * If this has JSON event transformers
     */
    public boolean hasEventTransformers()
    {
        return (this.container instanceof TweakContainer && ((TweakContainer<?>)this.container).hasEventTransformers());
    }
    
    /**
     * If this has mixins
     */
    public boolean hasMixins()
    {
        return (this.container instanceof MixinContainer && ((MixinContainer<?>)this.container).hasMixins());
    }

    /**
     * Get whether this mod uses external (3rd-party) API
     */
    public boolean usesAPI()
    {
        if (this.container instanceof LoadableMod<?>)
        {
            for (String requiredAPI : ((LoadableMod<?>)this.container).getRequiredAPIs())
            {
                if (!ModInfo.BUILT_IN_APIS.contains(requiredAPI))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get the mod instance
     */
    public abstract LiteMod getMod();

    /**
     * Get the mod class
     */
    public abstract Class<? extends LiteMod> getModClass();

    /**
     * Get the mod class full name
     */
    public abstract String getModClassName();

    /**
     * Get the mod class simple name
     */
    public abstract String getModClassSimpleName();
}
