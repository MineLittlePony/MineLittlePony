package com.mumfrey.liteloader.client.util.render;

import com.mumfrey.liteloader.util.render.IconTextured;

import net.minecraft.util.ResourceLocation;

public class IconAbsolute implements IconTextured
{
    private ResourceLocation textureResource;

    private String displayText;

    private int texMapSize = 256; 

    private int width;
    private int height;

    private int uPos, vPos;

    private float uCoord;
    private float uCoord2;
    private float vCoord;
    private float vCoord2;

    public IconAbsolute(ResourceLocation textureResource, String displayText, int width, int height, float uCoord, float vCoord, float uCoord2,
            float vCoord2)
    {
        this(textureResource, displayText, width, height, uCoord, vCoord, uCoord2, vCoord2, 256);
    }

    public IconAbsolute(ResourceLocation textureResource, String displayText, int width, int height, float uCoord, float vCoord, float uCoord2,
            float vCoord2, int texMapSize)
    {
        this.textureResource = textureResource;
        this.displayText = displayText;
        this.width = width;
        this.height = height;

        this.uPos = (int)uCoord;
        this.vPos = (int)vCoord;

        this.texMapSize = texMapSize;
        this.uCoord = uCoord / this.texMapSize;
        this.uCoord2 = uCoord2 / this.texMapSize;
        this.vCoord = vCoord / this.texMapSize;
        this.vCoord2 = vCoord2 / this.texMapSize;
    }

    @Override
    public String getDisplayText()
    {
        return this.displayText;
    }

    @Override
    public ResourceLocation getTextureResource()
    {
        return this.textureResource;
    }

    @Override
    public int getIconWidth()
    {
        return this.width;
    }

    @Override
    public int getIconHeight()
    {
        return this.height;
    }

    @Override
    public int getUPos()
    {
        return this.uPos;
    }

    @Override
    public int getVPos()
    {
        return this.vPos;
    }

    @Override
    public float getMinU()
    {
        return this.uCoord;
    }

    @Override
    public float getMaxU()
    {
        return this.uCoord2 - Float.MIN_VALUE;
    }

    @Override
    public float getInterpolatedU(double slice)
    {
        float uSize = this.uCoord2 - this.uCoord;
        return this.uCoord + uSize * ((float)slice / 16.0F) - Float.MIN_VALUE;
    }

    @Override
    public float getMinV()
    {
        return this.vCoord;
    }

    @Override
    public float getMaxV()
    {
        return this.vCoord2 - Float.MIN_VALUE;
    }

    @Override
    public float getInterpolatedV(double slice)
    {
        float vSize = this.vCoord2 - this.vCoord;
        return this.vCoord + vSize * ((float)slice / 16.0F) - Float.MIN_VALUE;
    }

    @Override
    public String getIconName()
    {
        return this.displayText;
    }
}
