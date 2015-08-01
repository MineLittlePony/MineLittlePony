package com.voxelmodpack.common.gui;

import static com.mumfrey.liteloader.gl.GL.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

/**
 * GuiControlEx is the base class for additional controls. It includes some
 * advanced drawing methods which are used by several derived classes
 * 
 * @author Adam Mummery-Smith
 */
public abstract class GuiControlEx extends GuiControl {
    /**
     * Used by some controls to indicate the manner in which they have handled a
     * keypress
     */
    public enum KeyHandledState {
        /**
         * The control did not handle the keypress
         */
        None,

        /**
         * The control handled the keypress and the container should do no
         * further processing
         */
        Handled,

        /**
         * The control handled the keypress and the container should call
         * actionPerformed
         */
        ActionPerformed
    }

    /**
     * Set by parent screen to enable cursor flash etc
     */
    public int updateCounter;

    /**
     * Reference to the minecraft game instance
     */
    protected Minecraft mc;

    /**
     * Flag indicating whether an action was performed, to support GuiScreenEx's
     * callback mechanism
     */
    protected boolean actionPerformed;

    /**
     * Flag tracking whether an item was double-clicked
     */
    protected boolean doubleClicked;

    /**
     * Constructor, passes through to GuiButton constructor
     * 
     * @param minecraft Minecraft game instance
     * @param controlId Control's ID (used for actionPerformed)
     * @param xPos Control X position (left)
     * @param yPos Control Y position (top)
     * @param controlWidth Control width
     * @param controlHeight Control height
     * @param displayText Control display text
     */
    public GuiControlEx(Minecraft minecraft, int controlId, int xPos, int yPos, int controlWidth, int controlHeight,
            String displayText) {
        super(controlId, xPos, yPos, controlWidth, controlHeight, displayText);
        this.mc = minecraft;
    }

    /**
     * Override from GuiButton, handle this call and forward it to DrawControl
     * for neatness
     * 
     * @param minecraft Reference to the minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     */
    @Override
    public final void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        this.drawControl(minecraft, mouseX, mouseY);
    }

    /**
     * Draw the control
     * 
     * @param minecraft Reference to the minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     */
    protected abstract void drawControl(Minecraft minecraft, int mouseX, int mouseY);

    /**
     * GuiControlEx returns true from mousePressed if the mouse was captured,
     * NOT if an action was performed. Containers should call this function
     * afterwards to determine whether an action was performed.
     * 
     * @return True if actionPerformed should be dispatched
     */
    public boolean getActionPerformed() {
        return this.actionPerformed;
    }

    /**
     * Get whether an actionPerformed was a double-click event
     * 
     * @return
     */
    public boolean getDoubleClicked(boolean resetDoubleClicked) {
        boolean result = this.doubleClicked;
        if (resetDoubleClicked)
            this.doubleClicked = false;
        return result;
    }

    /**
     * Draws a line between two points with the specified width and colour
     * 
     * @param x1 Origin x coordinate
     * @param y1 Origin y coordinate
     * @param x2 End x coordinate
     * @param y2 End y coordinate
     * @param width Line width in pixels
     * @param colour Line colour
     */
    protected void drawLine(int x1, int y1, int x2, int y2, int width, int colour) {
        this.drawArrow(x1, y1, x2, y2, width, colour, false, 0);
    }

    /**
     * Draws an arrow between two points with the specified width and colour
     * 
     * @param x1 Origin x coordinate
     * @param y1 Origin y coordinate
     * @param x2 End x coordinate
     * @param y2 End y coordinate
     * @param width Line width in pixels
     * @param arrowHeadSize Size of the arrow head
     * @param colour Colour
     */
    protected void drawArrow(int x1, int y1, int x2, int y2, int width, int arrowHeadSize, int colour) {
        this.drawArrow(x1, y1, x2, y2, width, colour, true, arrowHeadSize);
    }

    /**
     * Internal function for drawing lines and arrows
     * 
     * @param x1 Origin x coordinate
     * @param y1 Origin y coordinate
     * @param x2 End x coordinate
     * @param y2 End y coordinate
     * @param width Line width in pixels
     * @param colour Colour
     * @param arrowHead True to draw an arrow, otherwise draws a line
     * @param arrowHeadSize Size of the arrow head
     */
    private void drawArrow(int x1, int y1, int x2, int y2, int width, int colour, boolean arrowHead,
            int arrowHeadSize) {
        // Calculate the line length and angle defined by the specified points
        int length = (int) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));

        // Local rotation
        glPushMatrix();
        glTranslatef(x1, y1, 0.0f);
        glRotatef(angle, 0.0f, 0.0f, 1.0f);

        // Calc coordinates for the line and arrow points
        x1 = 0;
        x2 = length - (arrowHead ? arrowHeadSize : 0);
        y1 = (int) (width * -0.5);
        y2 = y1 + width;

        // Calc colour components
        float f = (colour >> 24 & 0xff) / 255F;
        float f1 = (colour >> 16 & 0xff) / 255F;
        float f2 = (colour >> 8 & 0xff) / 255F;
        float f3 = (colour & 0xff) / 255F;

        glEnableBlend();
        glDisableTexture2D();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(f1, f2, f3, f);

        // Draw the line
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRender = tessellator.getWorldRenderer();
        worldRender.startDrawingQuads();
        worldRender.addVertex(x1, y2, 0.0D);
        worldRender.addVertex(x2, y2, 0.0D);
        worldRender.addVertex(x2, y1, 0.0D);
        worldRender.addVertex(x1, y1, 0.0D);
        tessellator.draw();

        // If an arrow then draw the arrow head
        if (arrowHead && arrowHeadSize > 0) {
            worldRender.startDrawing(4);
            worldRender.addVertex(x2, 0 - arrowHeadSize / 2, 0);
            worldRender.addVertex(x2, arrowHeadSize / 2, 0);
            worldRender.addVertex(length, 0, 0);
            tessellator.draw();
        }

        glEnableTexture2D();
        glDisableBlend();

        glPopMatrix();
    }

    /**
     * Set the texmap scale factor
     * 
     * @param textureSize
     */
    public void setTexMapSize(int textureSize) {
        texMapScale = 1F / textureSize;
    }

    /**
     * Draws a textured rectangle at 90 degrees
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
    public void drawTexturedModalRectRot(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRender = tessellator.getWorldRenderer();
        worldRender.startDrawingQuads();
        worldRender.addVertexWithUV(x2, y2, this.getZLevel(), (u) * texMapScale, (v2) * texMapScale);
        worldRender.addVertexWithUV(x2, y, this.getZLevel(), (u2) * texMapScale, (v2) * texMapScale);
        worldRender.addVertexWithUV(x, y, this.getZLevel(), (u2) * texMapScale, (v) * texMapScale);
        worldRender.addVertexWithUV(x, y2, this.getZLevel(), (u) * texMapScale, (v) * texMapScale);
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle at 90 degrees
     * 
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param width Width of texture to draw
     * @param height Height of texture to draw
     */
    public void drawTexturedModalRectRot(int x, int y, int u, int v, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRender = tessellator.getWorldRenderer();
        worldRender.startDrawingQuads();
        worldRender.addVertexWithUV(x + height, y + width, this.getZLevel(), (u) * texMapScale,
                (v + height) * texMapScale);
        worldRender.addVertexWithUV(x + height, y, this.getZLevel(), (u + width) * texMapScale,
                (v + height) * texMapScale);
        worldRender.addVertexWithUV(x, y, this.getZLevel(), (u + width) * texMapScale, (v) * texMapScale);
        worldRender.addVertexWithUV(x, y + width, this.getZLevel(), (u) * texMapScale, (v) * texMapScale);
        tessellator.draw();
    }

    /**
     * Draws a tesselated rectangle where the texture is stretched horizontally
     * but vertical scaling is achieved by splitting the texture in half and
     * repeating the middle pixels
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
    public void drawTessellatedModalRectV(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        int tileSize = ((v2 - v) / 2);
        int vMidTop = v + tileSize;
        int vMidBtm = vMidTop + 1;

        this.drawTexturedModalRect(x, y, x2, y + tileSize, u, v, u2, vMidTop);
        this.drawTexturedModalRect(x, y + tileSize, x2, y2 - tileSize + 1, u, vMidTop, u2, vMidBtm);
        this.drawTexturedModalRect(x, y2 - tileSize + 1, x2, y2, u, vMidBtm, u2, v2);
    }

    /**
     * Draws a tesselated rectangle where the texture is stretched vertically
     * but horizontal scaling is achieved by splitting the texture in half and
     * repeating the middle pixels
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
    public void drawTessellatedModalRectH(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        int tileSize = ((u2 - u) / 2);
        int uMidLeft = u + tileSize;
        int uMidRight = uMidLeft + 1;

        this.drawTexturedModalRect(x, y, x + tileSize, y2, u, v, uMidLeft, v2);
        this.drawTexturedModalRect(x + tileSize, y, x2 - tileSize + 1, y2, uMidLeft, v, uMidRight, v2);
        this.drawTexturedModalRect(x2 - tileSize + 1, y, x2, y2, uMidRight, v, u2, v2);
    }

    /**
     * Draws a tesselated rectangle where the texture is stretched vertically
     * and horizontally but the middle pixels are repeated whilst the joining
     * pixels are stretched.
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
    public void drawTessellatedModalBorderRect(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        this.drawTessellatedModalBorderRect(x, y, x2, y2, u, v, u2, v2,
                Math.min(((x2 - x) / 2) - 1, ((y2 - y) / 2) - 1));
    }

    /**
     * Draws a tesselated rectangle where the texture is stretched vertically
     * and horizontally but the middle pixels are repeated whilst the joining
     * pixels are stretched. Bordersize specifies the portion of the texture
     * which will remain unstretched.
     * 
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
    public void drawTessellatedModalBorderRect(int x, int y, int x2, int y2, int u, int v, int u2, int v2,
            int borderSize) {
        int tileSize = Math.min(((u2 - u) / 2) - 1, ((v2 - v) / 2) - 1);

        int ul = u + tileSize, ur = u2 - tileSize, vt = v + tileSize, vb = v2 - tileSize;
        int xl = x + borderSize, xr = x2 - borderSize, yt = y + borderSize, yb = y2 - borderSize;

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

    /**
     * Draw a string but cut it off if it's too long to fit in the specified
     * width
     * 
     * @param fontrenderer
     * @param s
     * @param x
     * @param y
     * @param width
     * @param colour
     */
    public static void drawStringWithEllipsis(FontRenderer fontrenderer, String s, int x, int y, int width,
            int colour) {
        if (fontrenderer.getStringWidth(s) <= width) {
            fontrenderer.drawStringWithShadow(s, x, y, colour);
        } else if (width < 8) {
            fontrenderer.drawStringWithShadow("..", x, y, colour);
        } else {
            String trimmedText = s;

            while (fontrenderer.getStringWidth(trimmedText) > width - 8 && trimmedText.length() > 0)
                trimmedText = trimmedText.substring(0, trimmedText.length() - 1);

            fontrenderer.drawStringWithShadow(trimmedText + "...", x, y, colour);
        }
    }
}
