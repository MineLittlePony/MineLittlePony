package com.mumfrey.liteloader.client.gui;

import net.minecraft.client.gui.Gui;

/**
 * Extremely simple scrollbar implementation
 *
 * @author Adam Mummery-Smith
 */
public class GuiSimpleScrollBar extends Gui
{
    /**
     * Current value 
     */
    private int value = 0;

    /**
     * Current maximum value
     */
    private int maxValue = 100;

    private int backColour = 0x44FFFFFF;
    private int foreColour = 0xFFFFFFFF;

    /**
     * True if mouse was over the drag bar when last drawn 
     */
    private boolean mouseOver = false;

    /**
     * True if currently dragging the scroll bar
     */
    private boolean dragging = false;

    /**
     * Value prior to starting to drag
     */
    private int mouseDownValue = 0;

    /**
     * mouse Y coordinate prior to starting to drag
     */
    private int mouseDownY = 0;

    /**
     * Get the current scroll value
     */
    public int getValue()
    {
        return this.value;
    }

    /**
     * Set the scroll value, the value is clamped between 0 and the current max
     * value.
     */
    public void setValue(int value)
    {
        this.value = Math.min(Math.max(value, 0), this.maxValue);
    }

    /**
     * Offset the scroll value by the specified amount, the value is clamped
     * between 0 and the current max value.
     */
    public void offsetValue(int offset)
    {
        this.setValue(this.value + offset);
    }

    /**
     * Get the current max value
     */
    public int getMaxValue()
    {
        return this.maxValue;
    }

    /**
     * Sets the current max value
     */
    public void setMaxValue(int maxValue)
    {
        this.maxValue = Math.max(0, maxValue);
        this.value = Math.min(this.value, this.maxValue);
    }

    /**
     * Returns true if the mouse was over the drag bar on the last render
     */
    public boolean wasMouseOver()
    {
        return this.mouseOver;
    }

    /**
     * Set the current dragging state
     */
    public void setDragging(boolean dragging)
    {
        this.dragging = dragging;
    }

    /**
     * Draw the scroll bar
     * 
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     * @param xPosition
     * @param yPosition
     * @param width
     * @param height
     * @param totalHeight
     */
    public void drawScrollBar(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, int height, int totalHeight)
    {
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, this.backColour);

        if (totalHeight > 0)
        {
            int slideHeight = height - 2;
            float pct = Math.min(1.0F, (float)slideHeight / (float)totalHeight);
            int barHeight = (int)(pct * slideHeight);
            int barTravel = slideHeight - barHeight;
            int barPosition = yPosition + 1 + (this.maxValue > 0 ? (int)((this.value / (float)this.maxValue) * barTravel) : 0);

            drawRect(xPosition + 1, barPosition, xPosition + width - 1, barPosition + barHeight, this.foreColour);

            this.mouseOver = mouseX > xPosition && mouseX < xPosition + width && mouseY > barPosition && mouseY < barPosition + barHeight;
            this.handleDrag(mouseY, barTravel);
        }
    }

    /**
     * @param mouseY
     * @param barTravel
     */
    public void handleDrag(int mouseY, int barTravel)
    {
        if (this.dragging)
        {
            // Convert pixel delta to value delta
            float valuePerPixel = (float)this.maxValue / barTravel;
            this.setValue((int)(this.mouseDownValue + ((mouseY - this.mouseDownY) * valuePerPixel)));
        }
        else
        {
            this.mouseDownY = mouseY;
            this.mouseDownValue = this.value;
        }
    }
}
