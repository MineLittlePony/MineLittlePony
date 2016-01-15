package com.mumfrey.liteloader.client.util.render;

import net.minecraft.util.ResourceLocation;

import com.mumfrey.liteloader.util.render.IconClickable;

public abstract class IconAbsoluteClickable extends IconAbsolute implements IconClickable
{
    public IconAbsoluteClickable(ResourceLocation textureResource, String displayText, int width, int height, float uCoord, float vCoord,
            float uCoord2, float vCoord2)
    {
        super(textureResource, displayText, width, height, uCoord, vCoord, uCoord2, vCoord2);
    }

    public IconAbsoluteClickable(ResourceLocation textureResource, String displayText, int width, int height, float uCoord, float vCoord,
            float uCoord2, float vCoord2, int texMapSize)
    {
        super(textureResource, displayText, width, height, uCoord, vCoord, uCoord2, vCoord2, texMapSize);
    }
}
