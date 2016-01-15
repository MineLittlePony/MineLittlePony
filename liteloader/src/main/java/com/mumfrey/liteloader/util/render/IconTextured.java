package com.mumfrey.liteloader.util.render;

import net.minecraft.util.ResourceLocation;

/**
 * Icon with a texture and tooltip allocated to it
 *  
 * @author Adam Mummery-Smith
 */
public interface IconTextured extends Icon
{
    /**
     * Get tooltip text, return null for no tooltip
     */
    public abstract String getDisplayText();

    /**
     * Get the texture resource for this icon
     */
    public abstract ResourceLocation getTextureResource();

    /**
     * Get the U coordinate on the texture for this icon
     */
    public abstract int getUPos();

    /**
     * Get the V coordinate on the texture for this icon
     */
    public abstract int getVPos();
}
