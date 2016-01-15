package com.mumfrey.liteloader.client.api;

import java.net.URI;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import com.mumfrey.liteloader.api.BrandingProvider;
import com.mumfrey.liteloader.client.util.render.IconAbsolute;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.render.Icon;

/**
 * LiteLoader's branding provider
 * 
 * @author Adam Mummery-Smith
 */
public class LiteLoaderBrandingProvider implements BrandingProvider
{
    public static final int BRANDING_COLOUR = 0xFF4785D1;

    public static final ResourceLocation ABOUT_TEXTURE = new ResourceLocation("liteloader", "textures/gui/about.png");
    
    public static final IconAbsolute LOGO_COORDS = new IconAbsolute(LiteLoaderBrandingProvider.ABOUT_TEXTURE,
                                                                        "logo", 128, 40, 0, 0, 256, 80);
    public static final IconAbsolute ICON_COORDS = new IconAbsolute(LiteLoaderBrandingProvider.ABOUT_TEXTURE,
                                                                        "chicken", 32, 45, 0, 80, 64, 170);
    public static final IconAbsolute TWITTER_AVATAR_COORDS = new IconAbsolute(LiteLoaderBrandingProvider.ABOUT_TEXTURE,
                                                                        "twitter_avatar",32, 32, 192, 80, 256, 144);

    public static final URI LITELOADER_URI = URI.create("http://www.liteloader.com/");

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getPriority()
     */
    @Override
    public int getPriority()
    {
        return -1000;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getDisplayName()
     */
    @Override
    public String getDisplayName()
    {
        return "LiteLoader " + I18n.format("gui.about.versiontext", LiteLoader.getVersion());
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getCopyrightText()
     */
    @Override
    public String getCopyrightText()
    {
        return "Copyright (c) 2012-2016 Adam Mummery-Smith";
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getHomepage()
     */
    @Override
    public URI getHomepage()
    {
        return LiteLoaderBrandingProvider.LITELOADER_URI;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getBrandingColour()
     */
    @Override
    public int getBrandingColour()
    {
        return LiteLoaderBrandingProvider.BRANDING_COLOUR;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getLogoResource()
     */
    @Override
    public ResourceLocation getLogoResource()
    {
        return LiteLoaderBrandingProvider.ABOUT_TEXTURE;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getLogoCoords()
     */
    @Override
    public Icon getLogoCoords()
    {
        return LiteLoaderBrandingProvider.LOGO_COORDS; 
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getIconResource()
     */
    @Override
    public ResourceLocation getIconResource()
    {
        return LiteLoaderBrandingProvider.ABOUT_TEXTURE;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getIconCoords()
     */
    @Override
    public Icon getIconCoords()
    {
        return LiteLoaderBrandingProvider.ICON_COORDS; 
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getTwitterUserName()
     */
    @Override
    public String getTwitterUserName()
    {
        return "therealeq2";
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider
     *      #getTwitterAvatarResource()
     */
    @Override
    public ResourceLocation getTwitterAvatarResource()
    {
        return LiteLoaderBrandingProvider.ABOUT_TEXTURE;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.BrandingProvider#getTwitterAvatarCoords()
     */
    @Override
    public Icon getTwitterAvatarCoords()
    {
        return LiteLoaderBrandingProvider.TWITTER_AVATAR_COORDS; 
    }
}
