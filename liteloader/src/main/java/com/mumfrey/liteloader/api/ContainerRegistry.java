package com.mumfrey.liteloader.api;

import java.io.File;
import java.util.Collection;

import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.interfaces.Loadable;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.interfaces.TweakContainer;

/**
 * Registry for enabled, disabled, injected and bad containers
 * 
 * @author Adam Mummery-Smith
 */
public interface ContainerRegistry
{
    public enum DisabledReason
    {
        UNKNOWN("Container %s is could not be loaded for UNKNOWN reason"),
        USER_DISABLED("Container %s is disabled"),
        MISSING_DEPENDENCY("Container %s is missing one or more dependencies"),
        MISSING_API("Container %s is missing one or more required APIs");

        private final String message;

        private DisabledReason(String message)
        {
            this.message = message;
        }

        public String getMessage(LoadableMod<?> container)
        {
            return String.format(this.message, container);
        }
    }

    /**
     * Register an enabled container, removes the container from the disabled
     * containers list if present.
     */
    public abstract void registerEnabledContainer(LoadableMod<?> container);

    /**
     * Get all enabled containers
     */
    public abstract Collection<? extends LoadableMod<?>> getEnabledContainers();

    /**
     * Get a specific enabled container by id
     */
    public abstract LoadableMod<?> getEnabledContainer(String identifier);

    /**
     * Register a disabled container
     */
    public abstract void registerDisabledContainer(LoadableMod<?> container, DisabledReason reason);

    /**
     * Get all disabled containers
     */
    public abstract Collection<? extends ModInfo<Loadable<?>>> getDisabledContainers();

    /**
     * Check whether a specific container is registered as disabled
     */
    public abstract boolean isDisabledContainer(LoadableMod<?> container);

    /**
     * Register a bad container
     */
    public abstract void registerBadContainer(Loadable<?> container, String reason);

    /**
     * Get all bad containers
     */
    public abstract Collection<? extends ModInfo<Loadable<?>>> getBadContainers();

    /**
     * Register a candidate tweak container
     */
    public abstract void registerTweakContainer(TweakContainer<File> container);

    /**
     * Get all registered tweak containers
     */
    public abstract Collection<TweakContainer<File>> getTweakContainers();

    /**
     * Register an injected tweak container
     */
    public abstract void registerInjectedTweak(TweakContainer<File> container);

    /**
     * Get all injected tweak containers
     */
    public abstract Collection<? extends ModInfo<Loadable<?>>> getInjectedTweaks();
}
