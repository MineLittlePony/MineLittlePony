package com.mumfrey.liteloader.api;

import java.io.File;

import com.mumfrey.liteloader.api.ContainerRegistry.DisabledReason;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.interfaces.LoaderEnumerator;
import com.mumfrey.liteloader.interfaces.TweakContainer;

/**
 * LiteLoader Extensible API - Enumeration observer
 * 
 * EnumerationObserver receive callbacks when mod containers are enumerated.
 * Instances of this class <b>must</b> be returned from getPreInitObservers in
 * order to work.
 * 
 * @author Adam Mummery-Smith
 */
public interface EnumerationObserver extends Observer
{
    /**
     * Called upon registration for every discovered container which is enabled
     * 
     * @param enumerator
     * @param container
     */
    public abstract void onRegisterEnabledContainer(LoaderEnumerator enumerator, LoadableMod<?> container);

    /**
     * Called upon registration for every discovered container which is
     * currently disabled, either because 
     * 
     * @param enumerator
     * @param container
     * @param reason
     */
    public abstract void onRegisterDisabledContainer(LoaderEnumerator enumerator, LoadableMod<?> container, DisabledReason reason);

    /**
     * Called AFTER registration of an ENABLED container (eg.
     * onRegisterEnabledContainer will be called first) if that container also
     * contains tweaks.
     * 
     * @param enumerator
     * @param container
     */
    public abstract void onRegisterTweakContainer(LoaderEnumerator enumerator, TweakContainer<File> container);

    /**
     * Called when a mod container is added to the pending mods list. This does
     * not mean that the specific mod will actually be instanced since the mod
     * can still be disabled via the exclusion list (this is to allow entire
     * containers to be disabled or just individual mods) and so if you wish to
     * observe actual mod instantiation you should still provide a
     * {@link com.mumfrey.liteloader.client.ResourceObserver}. However this
     * event expresses a declaration by the enumerator of an intention to load
     * the specified mod. 
     * 
     * @param enumerator
     * @param mod
     */
    public abstract void onModAdded(LoaderEnumerator enumerator, ModInfo<LoadableMod<?>> mod);
}
