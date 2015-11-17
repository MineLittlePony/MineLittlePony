package com.voxelmodpack.common.gui;

import static com.mumfrey.liteloader.gl.GL.*;

import com.voxelmodpack.common.gui.interfaces.IExtendedGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * Class with many extra ways of drawing rectangular images
 */
public abstract class GuiScreenEx extends GuiScreen implements IExtendedGui {
    public static float texMapScale = 1F / 256F;

    /*
     * (non-Javadoc)
     * @see
     * com.voxelmodpack.common.gui.IExtendedGui#drawTessellatedModalBorderRect(
     * net.minecraft.src.ResourceLocation, int, int, int, int, int, int, int,
     * int, int, int)
     */
    @Override
    public void drawTessellatedModalBorderRect(ResourceLocation texture, int textureSize, int x, int y, int x2, int y2,
            int u, int v, int u2, int v2, int borderSize) {
        this.drawTessellatedModalBorderRect(texture, textureSize, x, y, x2, y2, u, v, u2, v2, borderSize, true);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.voxelmodpack.common.gui.IExtendedGui#drawTessellatedModalBorderRect(
     * net.minecraft.src.ResourceLocation, int, int, int, int, int, int, int,
     * int, int, int, boolean)
     */
    @Override
    public void drawTessellatedModalBorderRect(ResourceLocation texture, int textureSize, int x, int y, int x2, int y2,
            int u, int v, int u2, int v2, int borderSize, boolean setcolor) {
        int tileSize = Math.min(((u2 - u) / 2) - 1, ((v2 - v) / 2) - 1);

        int ul = u + tileSize, ur = u2 - tileSize, vt = v + tileSize, vb = v2 - tileSize;
        int xl = x + borderSize, xr = x2 - borderSize, yt = y + borderSize, yb = y2 - borderSize;

        this.setTexMapSize(textureSize);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        if (setcolor)
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(x, y, xl, yt, u, v, ul, vt);
        this.drawTexturedModalRect(xl, y, xr, yt, ul, v, ur, vt);
        this.drawTexturedModalRect(xr, y, x2, yt, ur, v, u2, vt);
        this.drawTexturedModalRect(x, yb, xl, y2, u, vb, ul, v2);
        this.drawTexturedModalRect(xl, yb, xr, y2, ul, vb, ur, v2);
        this.drawTexturedModalRect(xr, yb, x2, y2, ur, vb, u2, v2);
        this.drawTexturedModalRect(x, yt, xl, yb, u, vt, ul, vb);
        this.drawTexturedModalRect(xr, yt, x2, yb, ur, vt, u2, vb);
        this.drawTexturedModalRect(xl, yt, xr, yb, ul, vt, ur, vb);
    }

    /*
     * (non-Javadoc)
     * @see com.voxelmodpack.common.gui.IExtendedGui#drawTexturedModalRect(net.
     * minecraft.src.ResourceLocation, int, int, int, int, int, int, int, int)
     */
    @Override
    public void drawTexturedModalRect(ResourceLocation texture, int x, int y, int x2, int y2, int u, int v, int u2,
            int v2) {
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        this.drawTexturedModalRect(x, y, x2, y2, u, v, u2, v2);
    }

    /*
     * (non-Javadoc)
     * @see com.voxelmodpack.common.gui.IExtendedGui#drawTexturedModalRect(int,
     * int, int, int, int, int, int, int)
     */
    @Override
    public void drawTexturedModalRect(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRender = tessellator.getWorldRenderer();
        worldRender.startDrawingQuads();
        worldRender.addVertexWithUV(x, y2, this.zLevel, u * texMapScale, v2 * texMapScale);
        worldRender.addVertexWithUV(x2, y2, this.zLevel, u2 * texMapScale, v2 * texMapScale);
        worldRender.addVertexWithUV(x2, y, this.zLevel, u2 * texMapScale, v * texMapScale);
        worldRender.addVertexWithUV(x, y, this.zLevel, u * texMapScale, v * texMapScale);
        tessellator.draw();
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color.
     */
    public void drawDepthRect(int x1, int y1, int x2, int y2, int color) {
        if (x1 < x2) {
            int xTemp = x1;
            x1 = x2;
            x2 = xTemp;
        }

        if (y1 < y2) {
            int yTemp = y1;
            y1 = y2;
            y2 = yTemp;
        }

        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRender = tessellator.getWorldRenderer();
        glEnableBlend();
        glDisableTexture2D();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(red, green, blue, alpha);
        worldRender.startDrawingQuads();
        worldRender.addVertex(x1, y2, this.zLevel);
        worldRender.addVertex(x2, y2, this.zLevel);
        worldRender.addVertex(x2, y1, this.zLevel);
        worldRender.addVertex(x1, y1, this.zLevel);
        tessellator.draw();
        glEnableTexture2D();
        glDisableBlend();
    }

    /**
     * @param textureSize
     */
    protected void setTexMapSize(int textureSize) {
        GuiScreenEx.texMapScale = 1F / textureSize;
    }
}
