package com.voxelmodpack.common.gui;

import static com.mumfrey.liteloader.gl.GL.*;
import net.minecraft.client.Minecraft;

import com.voxelmodpack.common.LiteModVoxelCommon;

/**
 * General-purpose scrollbar control
 * 
 * @author Adam Mummery-Smith
 */
public class GuiScrollBar extends GuiControlEx {
    public enum ScrollBarOrientation {
        Vertical,
        Horizontal
    }

    protected ScrollBarOrientation orientation;

    /**
     * Scroll bar minimum value
     */
    protected int min;

    /**
     * Scroll bar maximum value
     */
    protected int max;

    /**
     * Scroll bar current value
     */
    protected int value;

    /**
     * Current visual position of the scroll button relative to the scroll tray
     */
    protected int buttonPos = 0;

    /**
     * Size of the scroll button
     */
    protected int scrollButtonSize = 20;

    /**
     * Total scrollable distance
     */
    protected int traySize;

    /**
     * Mousedown offset relative to the scroll button, used for mouse drag delta
     * calculations
     */
    protected int dragOffset;

    /**
     * Mousedown state, track whether holding down the drag button, up button,
     * or down button
     */
    protected int mouseDownState = 0;

    /**
     * Used to track a small delay before activating scroll on the up/down
     * buttons
     */
    protected int mouseDownTime = 0;

    /**
     * Make a new scrollbar control
     * 
     * @param minecraft Reference to the minecraft game instance
     * @param controlId Control ID, used for handling actionPerformed dispatches
     * @param xPos Control's X position
     * @param yPos Control's Y position
     * @param controlWidth Width of the control (also the height of the buttons)
     * @param controlHeight Height of the control
     * @param minValue Minimum scroll value
     * @param maxValue Maximum scroll value
     */
    public GuiScrollBar(Minecraft minecraft, int controlId, int xPos, int yPos, int controlWidth, int controlHeight,
            int minValue, int maxValue, ScrollBarOrientation orientation) {
        super(minecraft, controlId, xPos, yPos, controlWidth, controlHeight, "");

        this.orientation = orientation;

        this.value = this.min = minValue;
        this.max = Math.max(this.min, maxValue);

        this.traySize = this.getLargeDimension() - (this.getSmallDimension() * 2) - this.scrollButtonSize;
    }

    protected int getLargeDimension() {
        return (this.orientation == ScrollBarOrientation.Vertical) ? this.height : this.width;
    }

    protected int getSmallDimension() {
        return (this.orientation == ScrollBarOrientation.Vertical) ? this.width : this.height;
    }

    /**
     * Get the current value of the control
     * 
     * @return current value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Set the current control value
     * 
     * @param value New value (will be clamped within current min and max values
     */
    public void setValue(int value) {
        this.value = value;
        this.updateValue();
    }

    /**
     * Set the minimum value for the control, the current value will be clamped
     * between min and max
     * 
     * @param value
     */
    public void setMin(int value) {
        this.min = value;
        this.max = Math.max(this.min, this.max);
        this.updateValue();
    }

    /**
     * Set the maximum value for the control, the current value will be clamped
     * between min and max.
     * 
     * @param value
     */
    public void setMax(int value) {
        this.max = value;
        this.min = Math.min(this.max, this.min);
        this.updateValue();
    }

    /**
     * Set the control's position
     * 
     * @param left New left, X coordinate
     * @param top New top, Y coordinate
     */
    public void setPosition(int left, int top) {
        this.setXPosition(left);
        this.setYPosition(top);
    }

    /**
     * Set the size of the control
     * 
     * @param controlWidth New width for the control. Minimum 60 pixels
     * @param controlHeight New height for the control. Minimum 8 pixels
     */
    public void setSize(int controlWidth, int controlHeight) {
        this.setWidth(controlWidth);
        this.setHeight(controlHeight);
        this.traySize = this.getLargeDimension() - (this.getSmallDimension() * 2) - this.scrollButtonSize;
        this.updateValue();
    }

    /**
     * Set the size and position of the control
     * 
     * @param left New left, X coordinate
     * @param top New top, Y coordinate
     * @param controlWidth New width for the control. Minimum 60 pixels
     * @param controlHeight New height for the control. Minimum 8 pixels
     */
    public void setSizeAndPosition(int left, int top, int controlWidth, int controlHeight) {
        this.setPosition(left, top);
        this.setSize(controlWidth, controlHeight);
    }

    /**
     * Clamp the value between max and min and update the button position
     */
    private void updateValue() {
        if (this.value < this.min)
            this.value = this.min;
        if (this.value > this.max)
            this.value = this.max;

        this.buttonPos = (int) (((float) (this.value - this.min) / (float) (this.max - this.min)) * this.traySize);
    }

    /**
     * Draw the control
     * 
     * @param minecraft Minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     */
    @Override
    protected void drawControl(Minecraft minecraft, int mouseX, int mouseY) {
        if (!this.isVisible())
            return;

        float opacity = this.isEnabled() ? 1.0F : 0.3F;

        glColor4f(opacity, opacity, opacity, opacity);

        // Calc mouseover state of buttons
        boolean mouseOverUpButton = this.mouseDownState == 3 || this.mouseIsOverButton(3, mouseX, mouseY);
        boolean mouseOverDownButton = this.mouseDownState == 2 || this.mouseIsOverButton(2, mouseX, mouseY);
        boolean mouseOverButton = this.mouseDownState == 1 || this.mouseIsOverButton(1, mouseX, mouseY);

        // V coordinates based on button hover states
        int upButtonHoverState = 0 + (this.getHoverState(mouseOverUpButton) * 16);
        int downButtonHoverState = 0 + (this.getHoverState(mouseOverDownButton) * 16);
        int buttonHoverState = 0 + (this.getHoverState(mouseOverButton) * 16);

        if (this.orientation == ScrollBarOrientation.Vertical) {
            minecraft.getTextureManager().bindTexture(LiteModVoxelCommon.GUIPARTS);
            this.setTexMapSize(256);

            this.drawTessellatedModalBorderRect(this.xPosition, this.yPosition, this.xPosition + this.width,
                    this.yPosition + this.width, 0, upButtonHoverState, 16, upButtonHoverState + 16, 4);
            this.drawTessellatedModalBorderRect(this.xPosition, this.yPosition + this.height - this.width,
                    this.xPosition + this.width, this.yPosition + this.height, 0, downButtonHoverState, 16,
                    downButtonHoverState + 16, 4);

            // Slider tray
            this.drawTessellatedModalBorderRect(this.xPosition + 1, this.yPosition + this.width,
                    this.xPosition + this.width - 1, this.yPosition + this.height - this.width, 0, 0, 16, 16, 4);

            // Button icons
            this.drawTexturedModalRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 2,
                    this.yPosition + this.width - 2, 36, 0, 54, 18);
            this.drawTexturedModalRect(this.xPosition + 1, this.yPosition + this.height - this.width + 1,
                    this.xPosition + this.width - 2, this.yPosition + this.height - 1, 18, 0, 36, 18);
        }

        // Handle mouse dragged event
        this.mouseDragged(minecraft, mouseX, mouseY);

        minecraft.getTextureManager().bindTexture(LiteModVoxelCommon.GUIPARTS);

        if (this.orientation == ScrollBarOrientation.Vertical) {
            this.drawTessellatedModalBorderRect(this.xPosition, this.yPosition + this.width + this.buttonPos,
                    this.xPosition + this.width, this.yPosition + this.width + this.buttonPos + this.scrollButtonSize,
                    0, buttonHoverState, 16, buttonHoverState + 16, 4);
        }
    }

    protected boolean mouseIsOverButton(int button, int mouseX, int mouseY) {
        int buttonX = 0, buttonY = 0, buttonWidth = this.getSmallDimension(), buttonHeight = this.getSmallDimension();
        mouseX -= this.xPosition;
        mouseY -= this.yPosition;

        if (mouseX < 0 || mouseY < 0 || mouseX > this.width || mouseY > this.height)
            return false;

        if (button == 2) // down button
        {
            if (this.orientation == ScrollBarOrientation.Vertical)
                buttonY = this.getLargeDimension() - this.getSmallDimension();
            if (this.orientation == ScrollBarOrientation.Horizontal)
                buttonX = this.getLargeDimension() - this.getSmallDimension();
        } else if (button == 1) // drag button
        {
            if (this.orientation == ScrollBarOrientation.Vertical) {
                buttonY = this.getSmallDimension() + this.buttonPos;
                buttonHeight = this.scrollButtonSize;
            }
            if (this.orientation == ScrollBarOrientation.Horizontal) {
                buttonX = this.getSmallDimension() + this.buttonPos;
                buttonWidth = this.scrollButtonSize;
            }
        }

        return (mouseX >= buttonX && mouseY >= buttonY && mouseX < buttonX + buttonWidth
                && mouseY < buttonY + buttonHeight);
    }

    /**
     * Handle mouse dragged event
     * 
     * @param minecraft Minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     */
    @Override
    protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
        if (!this.isVisible())
            return;

        if (this.mouseDownState > 0) {
            int mouseDownTicks = this.updateCounter - this.mouseDownTime;

            if (this.mouseDownState == 1) {
                int mPos = (this.orientation == ScrollBarOrientation.Vertical) ? mouseY - this.yPosition
                        : mouseX - this.xPosition;
                this.value = (int) (((float) (mPos - this.dragOffset - this.getSmallDimension())
                        / (float) this.traySize) * (this.max - this.min)) + this.min;
            } else if (this.mouseDownState == 2 && mouseDownTicks > 6) {
                this.value += 4;
            } else if (this.mouseDownState == 3 && mouseDownTicks > 6) {
                this.value -= 4;
            }

            this.updateValue();
        }
    }

    /**
     * Handle mouse released event
     * 
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     */
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.mouseDownState = 0;
    }

    /**
     * Mouse pressed event
     * 
     * @param minecraft Minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     */
    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        this.actionPerformed = false;
        boolean returnValue = false;

        if (super.mousePressed(minecraft, mouseX, mouseY)) {
            // Adjust coords relative to control
            mouseX -= this.xPosition;
            mouseY -= this.yPosition;

            if (this.orientation == ScrollBarOrientation.Horizontal) {
                int mouseT = mouseY;
                mouseY = mouseX;
                mouseX = mouseT;
            }

            // Counter starts now and we wait 10 seconds for mousedown effects
            // to kick in
            this.mouseDownTime = this.updateCounter;

            if (mouseY < this.getSmallDimension()) // up button
            {
                this.mouseDownState = 3;
                this.value -= 4;
                this.actionPerformed = true;
            } else if (mouseY > this.getLargeDimension() - this.getSmallDimension()) // down
                                                                                     // button
            {
                this.mouseDownState = 2;
                this.value += 4;
                this.actionPerformed = true;
            } else if (mouseY > this.buttonPos + this.getSmallDimension()
                    && mouseY < this.buttonPos + this.getSmallDimension() + this.scrollButtonSize) // drag
                                                                                                   // button
            {
                this.mouseDownState = 1;
                this.dragOffset = mouseY - this.buttonPos - this.getSmallDimension();
                returnValue = true;
            } else if (mouseY < this.buttonPos + this.getSmallDimension()) // upper
                                                                           // tray
            {
                this.value -= 40;
                this.actionPerformed = true;
            } else if (mouseY > this.buttonPos + this.getSmallDimension() + this.scrollButtonSize) // lower
                                                                                                   // tray
            {
                this.value += 40;
                this.actionPerformed = true;
            }

            // Clamp value and update slider position
            this.updateValue();
        }

        return this.actionPerformed || returnValue;
    }
}
