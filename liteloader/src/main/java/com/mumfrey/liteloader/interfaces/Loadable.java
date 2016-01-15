package com.mumfrey.liteloader.interfaces;

import java.io.File;

import com.mumfrey.liteloader.launch.LoaderEnvironment;

/**
 * Interface for things which are loadable, essentially mods and tweaks
 * 
 * @author Adam Mummery-Smith
 *
 * @param <L> base class type for Comparable<?> so that implementors can specify
 *      their Comparable type. 
 */
public interface Loadable<L> extends Comparable<L>
{
    /**
     * Get the target resource
     */
    public abstract L getTarget();

    /**
     * Get the name of the loadable (usually the file name)
     */
    public abstract String getName();

    /**
     * Get the name to use when displaying this loadable, such as file name,
     * identifier or friendly name
     */
    public abstract String getDisplayName();

    /**
     * Get the location (path or URL) of this loadable
     */
    public abstract String getLocation();

    /**
     * Get the identifier (usually "name" from metadata) of this loadable, used
     * as the exclusivity key for mods and also the metadata key
     */
    public abstract String getIdentifier();

    /**
     * Get the version specified in the metadata or other location
     */
    public abstract String getVersion();

    /**
     * Get the author specified in the metadata
     */
    public abstract String getAuthor();

    /**
     * Get the description
     */
    public abstract String getDescription(String key);

    /**
     * Returns true if this is an external jar containing a tweak rather than a
     * mod.
     */
    public abstract boolean isExternalJar();

    /**
     * Returns true if this loadable supports being enabled and disabled via the
     * GUI.
     */
    public abstract boolean isToggleable();

    /**
     * Get whether this loadable is currently enabled in the context of the
     * supplied mods list.
     * 
     * @param environment
     */
    public abstract boolean isEnabled(LoaderEnvironment environment);

    /**
     * Get whether this loadable is a file container
     */
    public abstract boolean isFile();

    /**
     * Get whether this loadable is a directory container
     */
    public abstract boolean isDirectory();

    /**
     * If isFile or isDirectory return true then this method returns the inner
     * File instance, otherwise returns null.
     */
    public abstract File toFile();
    
    /**
     * Get whether this container requires early injection, eg. it contains a
     * tweaker, transformer or mixins
     */
    public abstract boolean requiresPreInitInjection();
}
