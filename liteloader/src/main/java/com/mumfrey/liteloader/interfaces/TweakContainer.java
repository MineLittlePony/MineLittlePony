package com.mumfrey.liteloader.interfaces;

import java.util.List;

/**
 * Interface for loadables which can contain tweaks and transformers
 * 
 * @author Adam Mummery-Smith
 */
public interface TweakContainer<L> extends MixinContainer<L>
{
    /**
     * Get whether this tweak container has a defined tweak class in its
     * metadata.
     */
    public abstract boolean hasTweakClass();

    /**
     * Get the tweak class name defined in the metadata
     */
    public abstract String getTweakClassName();

    /**
     * Get the priority value for this tweak
     */
    public abstract int getTweakPriority();

    /**
     * Get classpath entries defined in the metadata
     */
    public abstract String[] getClassPathEntries();

    /**
     * Get whether this container defines any transformer classes
     */
    public abstract boolean hasClassTransformers();

    /**
     * Get class transformers defined in the metadata
     */
    public abstract List<String> getClassTransformerClassNames();

    /**
     * True if this container defines event transformers via JSON
     */
    public abstract boolean hasEventTransformers();
}