package com.mumfrey.liteloader.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mumfrey.liteloader.api.ContainerRegistry;
import com.mumfrey.liteloader.interfaces.Loadable;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.interfaces.TweakContainer;

/**
 * Implementation of ContainerRegistry for LiteLoaderEnumerator
 * 
 * @author Adam Mummery-Smith
 */
class Containers implements ContainerRegistry
{
    /**
     * Mod containers which are disabled 
     */
    private final Map<String, ModInfo<Loadable<?>>> disabledContainers = new HashMap<String, ModInfo<Loadable<?>>>();

    /**
     * Mapping of identifiers to mod containers 
     */
    private final Map<String, LoadableMod<?>> enabledContainers = new HashMap<String, LoadableMod<?>>();

    /**
     * Map of containers which cannot be loaded to reasons
     */
    private final Set<ModInfo<Loadable<?>>> badContainers = new HashSet<ModInfo<Loadable<?>>>();

    /**
     * Tweaks to inject 
     */
    private final List<TweakContainer<File>> tweakContainers = new ArrayList<TweakContainer<File>>();

    /**
     * Other tweak-containing jars which we have injected 
     */
    private final List<ModInfo<Loadable<?>>> injectedTweaks = new ArrayList<ModInfo<Loadable<?>>>();

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry#getDisabledContainers()
     */
    @Override
    public Collection<? extends ModInfo<Loadable<?>>> getDisabledContainers()
    {
        return this.disabledContainers.values();
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry#getEnabledContainers()
     */
    @Override
    public Collection<? extends LoadableMod<?>> getEnabledContainers()
    {
        return this.enabledContainers.values();
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry#getBadContainers()
     */
    @Override
    public Collection<? extends ModInfo<Loadable<?>>> getBadContainers()
    {
        return this.badContainers;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry#getTweakContainers()
     */
    @Override
    public List<TweakContainer<File>> getTweakContainers()
    {
        return this.tweakContainers;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry#getInjectedTweaks()
     */
    @Override
    public List<? extends ModInfo<Loadable<?>>> getInjectedTweaks()
    {
        return this.injectedTweaks;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry
     *      #isDisabledContainer(com.mumfrey.liteloader.interfaces.LoadableMod)
     */
    @Override
    public boolean isDisabledContainer(LoadableMod<?> container)
    {
        return this.disabledContainers.containsValue(container);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry
     *      #getEnabledContainer(java.lang.String)
     */
    @Override
    public LoadableMod<?> getEnabledContainer(String identifier)
    {
        LoadableMod<?> container = this.enabledContainers.get(identifier);
        return container != null ? container : LoadableMod.NONE;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry
     *      #registerBadContainer(com.mumfrey.liteloader.interfaces.Loadable,
     *      java.lang.String)
     */
    @Override
    public void registerBadContainer(Loadable<?> container, String reason)
    {
        this.badContainers.add(new BadContainerInfo(container, reason));
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry
     *      #registerEnabledContainer(
     *      com.mumfrey.liteloader.interfaces.LoadableMod)
     */
    @Override
    public void registerEnabledContainer(LoadableMod<?> container)
    {
        this.disabledContainers.remove(container.getIdentifier());
        this.enabledContainers.put(container.getIdentifier(), container);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry
     *      #registerDisabledContainer(
     *      com.mumfrey.liteloader.interfaces.LoadableMod, 
     *      com.mumfrey.liteloader.api.ContainerRegistry.DisabledReason)
     */
    @Override
    public void registerDisabledContainer(LoadableMod<?> container, DisabledReason reason)
    {
        this.enabledContainers.remove(container.getIdentifier());
        this.disabledContainers.put(container.getIdentifier(), new NonMod(container, false));
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry
     *      #registerTweakContainer(
     *      com.mumfrey.liteloader.interfaces.TweakContainer)
     */
    @Override
    public void registerTweakContainer(TweakContainer<File> container)
    {
        this.tweakContainers.add(container);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ContainerRegistry
     *      #registerInjectedTweak(
     *      com.mumfrey.liteloader.interfaces.TweakContainer)
     */
    @Override
    public void registerInjectedTweak(TweakContainer<File> container)
    {
        this.injectedTweaks.add(new NonMod(container, true));
    }
}
