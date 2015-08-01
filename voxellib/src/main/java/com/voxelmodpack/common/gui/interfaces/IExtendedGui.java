package com.voxelmodpack.common.gui.interfaces;

import net.minecraft.util.ResourceLocation;

public interface IExtendedGui {
    /**
     * Draws a tesselated rectangle where the texture is stretched vertically
     * and horizontally but the middle pixels are repeated whilst the joining
     * pixels are stretched. Bordersize specifies the portion of the texture
     * which will remain unstretched.
     * 
     * @param texture Texture to use
     * @param textureSize Size of the texture
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     * @param borderSize Number of pixels to leave unstretched, must be less
     *            than half of the width or height (whichever is smallest)
     */
    public abstract void drawTessellatedModalBorderRect(ResourceLocation texture, int textureSize, int x, int y, int x2,
            int y2, int u, int v, int u2, int v2, int borderSize);

    /**
     * Draws a tesselated rectangle where the texture is stretched vertically
     * and horizontally but the middle pixels are repeated whilst the joining
     * pixels are stretched. Bordersize specifies the portion of the texture
     * which will remain unstretched.
     * 
     * @param texture Texture to use
     * @param textureSize Size of the texture
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     * @param borderSize Number of pixels to leave unstretched, must be less
     *            than half of the width or height (whichever is smallest)
     */
    public abstract void drawTessellatedModalBorderRect(ResourceLocation texture, int textureSize, int x, int y, int x2,
            int y2, int u, int v, int u2, int v2, int borderSize, boolean setcolor);

    /**
     * Advanced version of drawTexturedModalRect which supports separating the
     * UV coordinates from the drawn width/height
     * 
     * @param texture Texture to draw
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     */
    public abstract void drawTexturedModalRect(ResourceLocation texture, int x, int y, int x2, int y2, int u, int v,
            int u2, int v2);

    /**
     * Advanced version of drawTexturedModalRect which supports separating the
     * UV coordinates from the drawn width/height
     * 
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     */
    public abstract void drawTexturedModalRect(int x, int y, int x2, int y2, int u, int v, int u2, int v2);
}