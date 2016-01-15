package com.mumfrey.liteloader.client.gui;

import static com.mumfrey.liteloader.gl.GL.*;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import com.mumfrey.liteloader.client.api.LiteLoaderBrandingProvider;

/**
 * Base class for panels
 *
 * @author Adam Mummery-Smith
 */
public abstract class GuiPanel extends Gui
{
    protected static final int TOP = 26;
    protected static final int BOTTOM = 40;
    protected static final int MARGIN = 12;

    /**
     * Minecraft
     */
    protected Minecraft mc;

    /**
     * Buttons
     */
    protected List<GuiButton> controls = new LinkedList<GuiButton>();

    /**
     * Current available width
     */
    protected int width = 0;

    /**
     * Current available height
     */
    protected int height = 0;

    /**
     * Current inner pane width (width - margins)
     */
    protected int innerWidth = 0;

    /**
     * Current inner pane visible height (height - chrome)
     */
    protected int innerHeight = 0;

    /**
     * Panel Y position (for scroll)
     */
    protected int innerTop = TOP;

    /**
     * True if the client wants to close the panel 
     */
    private boolean closeRequested;

    /**
     * @param minecraft
     */
    public GuiPanel(Minecraft minecraft)
    {
        this.mc = minecraft;
    }

    boolean stealFocus()
    {
        return true;
    }

    /**
     * Called by the containing screen to set the panel size
     * 
     * @param width
     * @param height
     */
    void setSize(int width, int height)
    {
        this.controls.clear();

        this.width = width;
        this.height = height;

        this.innerHeight = this.height - TOP - BOTTOM;
        this.innerWidth = this.width - (MARGIN * 2) - 6;
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    void draw(int mouseX, int mouseY, float partialTicks)
    {
        for (GuiButton control : this.controls)
            control.drawButton(this.mc, mouseX, mouseY);
    }

    /**
     * 
     */
    public void close()
    {
        this.closeRequested = true;
    }

    /**
     * Get whether the client wants to close the panel
     */
    boolean isCloseRequested()
    {
        return this.closeRequested;
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            for (GuiButton control : this.controls)
            {
                if (control.mousePressed(this.mc, mouseX, mouseY))
                {
                    control.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(control);
                }
            }
        }
    }

    /**
     * @param mouseX
     * @param mouseY
     */
    boolean mouseOverPanel(int mouseX, int mouseY)
    {
        return mouseX > MARGIN && mouseX <= this.width - MARGIN && mouseY > TOP && mouseY <= this.height - BOTTOM;
    }

    /**
     * Called every tick
     */
    abstract void onTick();

    /**
     * Called after the screen is hidden
     */
    abstract void onHidden();

    /**
     * Called when the panel is shown
     */
    abstract void onShown();

    /**
     * @param keyChar
     * @param keyCode
     */
    abstract void keyPressed(char keyChar, int keyCode);

    /**
     * @param mouseX
     * @param mouseY
     */
    abstract void mouseMoved(int mouseX, int mouseY);

    /**
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

    /**
     * @param mouseWheelDelta
     */
    abstract void mouseWheelScrolled(int mouseWheelDelta);

    /**
     * @param control
     */
    abstract void actionPerformed(GuiButton control);

    /**
     * @param x
     * @param y
     * @param frame
     */
    protected void drawThrobber(int x, int y, int frame)
    {
        glEnableBlend();
        glAlphaFunc(GL_GREATER, 0.0F); 
        this.mc.getTextureManager().bindTexture(LiteLoaderBrandingProvider.ABOUT_TEXTURE);
        this.drawTexturedModalRect(x, y, (frame % 4) * 16, 171 + (((frame / 4) % 3) * 16), 16, 16);
        glAlphaFunc(GL_GREATER, 0.1F); 
        glDisableBlend();
    }
}