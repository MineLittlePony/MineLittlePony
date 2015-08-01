package com.voxelmodpack.common.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

/**
 * Vestigial abstraction class, not really required any more since MCP updates
 * so quickly but has some handy convenience methods
 *
 * @author Adam Mummery-Smith
 */
public class GuiControl extends GuiButton {
    /**
     * Scale factor which translates texture pixel coordinates to relative
     * coordinates
     */
    protected static float texMapScale = 0.00390625F;

    public GuiControl(int id, int xPosition, int yPosition, String displayText) {
        super(id, xPosition, yPosition, displayText);
    }

    public GuiControl(int id, int xPosition, int yPosition, int controlWidth, int controlHeight, String displayText) {
        super(id, xPosition, yPosition, controlWidth, controlHeight, displayText);
    }

    /**
     * Draws a textured rectangle with custom UV coordinates
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
    public void drawTexturedModalRect(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRender = tessellator.getWorldRenderer();
        worldRender.startDrawingQuads();
        worldRender.addVertexWithUV(x, y2, this.getZLevel(), (u) * texMapScale, (v2) * texMapScale);
        worldRender.addVertexWithUV(x2, y2, this.getZLevel(), (u2) * texMapScale, (v2) * texMapScale);
        worldRender.addVertexWithUV(x2, y, this.getZLevel(), (u2) * texMapScale, (v) * texMapScale);
        worldRender.addVertexWithUV(x, y, this.getZLevel(), (u) * texMapScale, (v) * texMapScale);
        tessellator.draw();
    }

    public final int getID() {
        return this.id;
    }

    public final int getHeight() {
        return this.height;
    }

    public final void setHeight(int newHeight) {
        this.height = newHeight;
    }

    public final int getXPosition() {
        return this.xPosition;
    }

    public final void setXPosition(int newXPosition) {
        this.xPosition = newXPosition;
    }

    public final int getYPosition() {
        return this.yPosition;
    }

    public final void setYPosition(int newYPosition) {
        this.yPosition = newYPosition;
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    public final void setEnabled(boolean newEnabled) {
        this.enabled = newEnabled;
    }

    public final boolean isVisible() {
        return this.visible;
    }

    public final void setVisible(boolean newVisible) {
        this.visible = newVisible;
    }

    protected final float getZLevel() {
        return this.zLevel;
    }

    @Override
    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        super.drawString(fontRendererIn, text, x, y, color);
    }
}
