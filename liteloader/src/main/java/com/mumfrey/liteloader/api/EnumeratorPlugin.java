package com.mumfrey.liteloader.api;

import java.util.List;

import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;

/**
 * LiteLoader Extensible API - Interface for objects which can interact with the
 * enumeration process, not yet available to APIs.
 * 
 * @author Adam Mummery-Smith
 */
public interface EnumeratorPlugin
{
    /**
     * Initialise this plugin
     */
    public abstract void init(LoaderEnvironment environment, LoaderProperties properties);

    /**
     * Get classes in the supplied container
     * 
     * @param container Container to inspect
     * @param classloader ClassLoader for this container
     * @param validator Mod class validator
     * @return list of classes in the container
     */
    public abstract <T> List<Class<? extends T>> getClasses(LoadableMod<?> container, ClassLoader classloader, ModClassValidator validator);

    public abstract boolean checkEnabled(ContainerRegistry containers, LoadableMod<?> container);

    public abstract boolean checkDependencies(ContainerRegistry containers, LoadableMod<?> base);

    public abstract boolean checkAPIRequirements(ContainerRegistry containers, LoadableMod<?> container);
}
