package com.mumfrey.liteloader.client.util.render;

import com.mumfrey.liteloader.util.render.Icon;

import net.minecraft.util.ResourceLocation;

public class IconTiled implements Icon
{
    private ResourceLocation textureResource;

    protected int iconID;

    protected int iconU;
    protected int iconV;
    private int width;
    private int height;
    private float uCoord;
    private float uCoord2;
    private float vCoord;
    private float vCoord2;

    private int textureWidth, textureHeight;

    public IconTiled(ResourceLocation textureResource, int id)
    {
        this(textureResource, id, 16);
    }

    public IconTiled(ResourceLocation textureResource, int id, int iconSize)
    {
        this(textureResource, id, iconSize, 0);
    }

    public IconTiled(ResourceLocation textureResource, int id, int iconSize, int yOffset)
    {
        this(textureResource, id, iconSize, (id % (256 / iconSize)) * iconSize, (id / (256 / iconSize)) * iconSize + yOffset);
    }

    public IconTiled(ResourceLocation textureResource, int id, int iconSize, int iconU, int iconV)
    {
        this(textureResource, id, iconU, iconV, iconSize, iconSize, 256, 256);
    }

    public IconTiled(ResourceLocation textureResource, int id, int iconU, int iconV, int width, int height, int textureWidth, int textureHeight)
    {
        this.iconID = id;
        this.textureResource = textureResource;

        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        this.width = width;
        this.height = height;

        this.init(iconU, iconV);
    }

    protected void init(int iconU, int iconV)
    {
        this.iconU = iconU;
        this.iconV = iconV;

        this.uCoord = (float)iconU / (float)this.textureWidth;
        this.uCoord2 = (float)(iconU + this.width) / (float)this.textureWidth;
        this.vCoord = (float)iconV / (float)this.textureHeight;
        this.vCoord2 = (float)(iconV + this.height) / (float)this.textureHeight;
    }

    public ResourceLocation getTextureResource()
    {
        return this.textureResource;
    }

    public int getIconID()
    {
        return this.iconID;
    }

    public void setIconID(int id)
    {
        this.iconID = id;
        this.init((id % 16) * 16, (id / 16) * 16);
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
        return this.textureResource + "_" + this.iconID;
    }
}
