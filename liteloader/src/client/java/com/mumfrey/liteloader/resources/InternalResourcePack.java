package com.mumfrey.liteloader.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

/**
 * Resource pack which returns resources using Class::getResourceAsStream() on
 * the specified class.
 * 
 * @author Adam Mummery-Smith
 */
public class InternalResourcePack implements IResourcePack
{
    /**
     * Domains supported by this resource pack
     */
    private final Set<String> resourceDomains = new HashSet<String>();

    /**
     * Name of this resource pack
     */
    private final String packName;

    /**
     * Class to execute getResourceAsStream() upon
     */
    private final Class<?> resourceClass;

    /**
     * @param name
     * @param resourceClass
     * @param domains
     */
    public InternalResourcePack(String name, Class<?> resourceClass, String... domains)
    {
        if (domains.length < 1) throw new IllegalArgumentException("No domains specified for internal resource pack");

        this.packName = name;
        this.resourceClass = resourceClass;

        for (String domain : domains)
            this.resourceDomains.add(domain);
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.resources.IResourcePack
     *      #getInputStream(net.minecraft.util.ResourceLocation)
     */
    @Override
    public InputStream getInputStream(ResourceLocation resourceLocation) throws IOException
    {
        return this.getResourceStream(resourceLocation);
    }

    /**
     * @param resourceLocation
     */
    private InputStream getResourceStream(ResourceLocation resourceLocation)
    {
        return this.resourceClass.getResourceAsStream(String.format("/assets/%s/%s",
                resourceLocation.getResourceDomain(), resourceLocation.getResourcePath()));
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.resources.IResourcePack#resourceExists(
     *      net.minecraft.util.ResourceLocation)
     */
    @Override
    public boolean resourceExists(ResourceLocation resourceLocation)
    {
        return this.getResourceStream(resourceLocation) != null;
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.resources.IResourcePack#getResourceDomains()
     */
    @Override
    public Set<String> getResourceDomains()
    {
        return this.resourceDomains;
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.resources.IResourcePack#getPackMetadata(
     *      net.minecraft.client.resources.data.IMetadataSerializer,
     *      java.lang.String)
     */
    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer par1MetadataSerializer, String par2Str) throws IOException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.resources.IResourcePack#getPackImage()
     */
    @Override
    public BufferedImage getPackImage() throws IOException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.resources.IResourcePack#getPackName()
     */
    @Override
    public String getPackName()
    {
        return this.packName;
    }
}
