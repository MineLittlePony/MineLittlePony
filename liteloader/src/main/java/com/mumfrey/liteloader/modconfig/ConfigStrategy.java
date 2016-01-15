package com.mumfrey.liteloader.modconfig;

import java.io.File;

import com.mumfrey.liteloader.core.LiteLoader;

/**
 * Configuration management strategy
 * 
 * @author Adam Mummery-Smith
 */
public enum ConfigStrategy
{
    /**
     * Use the unversioned "common" config folder
     */
    Unversioned,

    /**
     * Use the versioned config folder 
     */
    Versioned;

    public File getFileForStrategy(String fileName)
    {
        if (this == ConfigStrategy.Versioned)
        {
            return new File(LiteLoader.getConfigFolder(), fileName);
        }

        return new File(LiteLoader.getCommonConfigFolder(), fileName);
    }
}
