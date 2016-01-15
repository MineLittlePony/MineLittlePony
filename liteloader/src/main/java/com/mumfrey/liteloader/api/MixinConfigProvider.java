package com.mumfrey.liteloader.api;

import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;

/**
 * Container for all of an API's mixin environment configuration 
 */
public interface MixinConfigProvider
{
    /**
     * Get the minimum required mixin operating compatibility level for this
     * API, can return null.
     */
    public abstract CompatibilityLevel getCompatibilityLevel();
    
    /**
     * Get mixin configuration files for this API, all returned configs will be
     * added to the DEFAULT environment. Can return null.
     */
    public abstract String[] getMixinConfigs();

    /**
     * Get mixin error handler classes to register for this API. Can return
     * null.
     */
    public abstract String[] getErrorHandlers();
}
