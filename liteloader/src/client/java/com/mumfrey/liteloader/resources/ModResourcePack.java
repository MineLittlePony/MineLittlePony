package com.mumfrey.liteloader.resources;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;

/**
 * Resource pack which wraps a mod file
 *
 * @author Adam Mummery-Smith
 */
public class ModResourcePack extends FileResourcePack
{
    /**
     * Display name, only shows up in debug output 
     */
    private final String name;

    /**
     * @param name Friendly name
     * @param modFile
     */
    public ModResourcePack(String name, File modFile)
    {
        super(modFile);
        this.name = name;
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.resources.AbstractResourcePack#getPackMetadata(
     *      net.minecraft.client.resources.data.IMetadataSerializer,
     *      java.lang.String)
     */
    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
    {
        try
        {
            // This will fail when fetching pack.mcmeta if there isn't one in the mod file, since we don't care we
            // just catch the exception and return null instead
            return super.getPackMetadata(metadataSerializer, metadataSectionName);
        }
        catch (Exception ex) {}

        return null;
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.resources.AbstractResourcePack#getPackName()
     */
    @Override
    public String getPackName()
    {
        return this.name;
    }
}
