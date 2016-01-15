package com.mumfrey.liteloader.client.gui;

import static com.mumfrey.liteloader.gl.GL.*;
import static com.mumfrey.liteloader.gl.GLClippingPlanes.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.input.Keyboard;

/**
 * Basic non-interactive scrollable panel using OpenGL clipping planes
 * 
 * TODO handle interaction
 * 
 * @author Adam Mummery-Smith
 */
class GuiScrollPanel extends GuiPanel
{
    private ScrollPanelContent content;

    /**
     * Scroll bar for the panel
     */
    private GuiSimpleScrollBar scrollBar = new GuiSimpleScrollBar();

    /**
     * Left edge coord - specified 
     */
    private int left;

    /**
     * Top edge coord - specified
     */
    private int top;

    /**
     * 
     */
    private int contentHeight;

    public GuiScrollPanel(Minecraft minecraft, ScrollPanelContent content, int left, int top, int width, int height)
    {
        super(minecraft);

        this.setContent(content);
    }

    public void setContent(ScrollPanelContent content)
    {
        if (content == null)
        {
            throw new IllegalArgumentException("Scroll pane content can not be null");
        }

        this.content = content;
    }

    @Override
    void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;

        this.updateHeight();
    }

    public void setSizeAndPosition(int left, int top, int width, int height)
    {
        this.left = left;
        this.top = top;

        this.setSize(width, height);
    }

    public void updateHeight()
    {
        this.contentHeight = this.content.getScrollPanelContentHeight(this);
        this.scrollBar.setMaxValue(this.contentHeight - this.height);
    }

    public void scrollToBottom()
    {
        this.scrollBar.setValue(this.contentHeight);
    }

    public void scrollToTop()
    {
        this.scrollBar.setValue(0);
    }

    public void scrollBy(int amount)
    {
        this.scrollBar.offsetValue(amount);
    }

    public GuiButton addControl(GuiButton control)
    {
        this.controls.add(control);
        return control;
    }

    /**
     * Draw the panel and chrome
     * 
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        int scrollPosition = this.scrollBar.getValue();

        // Clip rect
        glEnableClipping(this.left, this.left + this.width - 6, this.top, this.top + this.height);

        // Offset by scroll
        glPushMatrix();
        glTranslatef(this.left, this.top - scrollPosition, 0.0F);

        this.content.drawScrollPanelContent(this, mouseX, mouseY, partialTicks, scrollPosition, this.height);

        super.draw(mouseX - this.left, mouseY + scrollPosition - this.top, partialTicks);

        // Disable clip rect
        glDisableClipping();

        // Restore transform
        glPopMatrix();

        // Update and draw scroll bar
        this.scrollBar.drawScrollBar(mouseX, mouseY, partialTicks, this.left + this.width - 5, this.top, 5, this.height,
                Math.max(this.height, this.contentHeight));
    }

    @Override
    public void mouseWheelScrolled(int mouseWheelDelta)
    {
        this.scrollBy(-mouseWheelDelta / 8);
    }

    @Override
    public void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        mouseY += this.scrollBar.getValue() - this.top;
        mouseX -= this.left;
        super.mousePressed(mouseX, mouseY, mouseButton);

        if (mouseX > 0 && mouseX < this.width && mouseY > 0 && mouseY < this.contentHeight)
        {
            this.content.scrollPanelMousePressed(this, mouseX, mouseY, mouseButton);
        }

        if (mouseButton == 0)
        {
            if (this.scrollBar.wasMouseOver())
            {
                this.scrollBar.setDragging(true);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            this.scrollBar.setDragging(false);
        }
    }

    @Override
    public void keyPressed(char keyChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_UP) this.scrollBar.offsetValue(-10);
        if (keyCode == Keyboard.KEY_DOWN) this.scrollBar.offsetValue(10);
        if (keyCode == Keyboard.KEY_PRIOR) this.scrollBar.offsetValue(-this.height + 10);
        if (keyCode == Keyboard.KEY_NEXT) this.scrollBar.offsetValue(this.height - 10);
        if (keyCode == Keyboard.KEY_HOME) this.scrollBar.setValue(0);
        if (keyCode == Keyboard.KEY_END) this.scrollBar.setValue(this.contentHeight);
    }

    @Override
    void onTick()
    {
    }

    @Override
    void onHidden()
    {
    }

    @Override
    void onShown()
    {
    }

    @Override
    void mouseMoved(int mouseX, int mouseY)
    {
    }

    @Override
    void actionPerformed(GuiButton control)
    {
        this.content.scrollPanelActionPerformed(this, control);
    }
}
