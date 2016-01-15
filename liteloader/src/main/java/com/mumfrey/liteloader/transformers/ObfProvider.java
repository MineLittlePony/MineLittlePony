package com.mumfrey.liteloader.transformers;

import com.mumfrey.liteloader.core.runtime.Obf;

/**
 * Interface for dynamic (context-specific) obfuscation provider, used
 * internally by ModEventInjectionTransformer to provide obf entries for the
 * AccessorTransformer from JSON 
 * 
 * @author Adam Mummery-Smith
 */
public interface ObfProvider
{
    /**
     * Try to locate an obfuscation table entry by name (id), returns null if no
     * entry was found
     * 
     * @param name
     */
    public abstract Obf getByName(String name);
}
