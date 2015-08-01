package com.voxelmodpack.common.properties;

import org.lwjgl.input.Mouse;

import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderFloat;

/**
 * Rebuilt by anangrybeaver
 * 
 * @author anangrybeaver
 */
public class VoxelPropertySlider extends VoxelProperty<IVoxelPropertyProviderFloat> {
    private float value = 0;
    private float valueMin = -1;
    private float valueMax = 1;
    private float valueShift = -1;
    private float valueScale;

    private float indicatorValue = 0;

    private int sliderHeight;
    private int sliderWidth;

    private int handleXPos;
    private int handleXPosMin;
    private int handleXPosMax;
    private int handleWidth;
    private int handleHeight;

    private boolean snap = false;

    private boolean indicatorShow = false;

    private String minText = "Min";
    private String maxText = "Max";

    @SuppressWarnings("unused")
    private String labelText;

    public VoxelPropertySlider(IVoxelPropertyProvider propertyProvider, String binding, String text, int xPos, int yPos,
            int w, int h, float minValue, float maxValue, boolean snap) {
        this(propertyProvider, binding, text, xPos, yPos, w, h, snap);

        this.valueMax = maxValue;
        this.valueMin = minValue;

        if (this.valueMax < this.valueMin || this.valueMax == this.valueMin)
            this.valueMax = this.valueMin + 1;

        this.valueShift = this.valueMin;

        this.value = this.propertyProvider.getFloatProperty(binding) - this.valueShift;
        this.valueScale = (this.handleXPosMax - this.handleXPosMin) / (this.valueMax - this.valueMin);

        if (this.propertyProvider.getFloatProperty(binding) < this.valueMin)
            this.propertyProvider.setProperty(binding, this.valueMin);

        else if (this.propertyProvider.getFloatProperty(binding) > this.valueMax)
            this.propertyProvider.setProperty(binding, this.valueMax);
    }

    /**
     * Creates a more defined Slider with given specifications.
     * 
     * @param parent The GUI Element, "this"
     * @param binding The numeral binding to be linked to.
     * @param text Name of the slider.
     * @param xPos x Coordinate
     * @param yPos y coordinate
     * @param w Width of the slider
     * @param h Height of the slider
     * @param minValue minimum value of the slider
     * @param maxValue max value of the slider
     * @param snap If it snaps to whole numbers.
     */
    public VoxelPropertySlider(IVoxelPropertyProvider propertyProvider, String binding, String text, int xPos, int yPos,
            int w, int h, float minValue, float maxValue, float indicatorValue, boolean snap) {
        this(propertyProvider, binding, text, xPos, yPos, w, h, indicatorValue, snap);

        this.valueMax = maxValue;
        this.valueMin = minValue;

        if (this.valueMax < this.valueMin || this.valueMax == this.valueMin)
            this.valueMax = this.valueMin + 1;

        this.valueShift = this.valueMin;

        this.value = this.propertyProvider.getFloatProperty(binding) - this.valueShift;
        this.valueScale = (this.handleXPosMax - this.handleXPosMin) / (this.valueMax - this.valueMin);

        if (this.propertyProvider.getFloatProperty(binding) < this.valueMin)
            this.propertyProvider.setProperty(binding, this.valueMin);

        else if (this.propertyProvider.getFloatProperty(binding) > this.valueMax)
            this.propertyProvider.setProperty(binding, this.valueMax);

    }

    /**
     * Creates a generic Slider with given specifications.
     * 
     * @param parent The GUI Element, "this"
     * @param binding The numeral binding to be linked to.
     * @param text Name of the slider.
     * @param xPos x Coordinate
     * @param yPos y coordinate
     * @param w Width of the slider
     * @param h Height of the slider
     * @param indicatorValue The value of a visible indicator on the slider.
     * @param snap If it snaps to whole numbers.
     */
    public VoxelPropertySlider(IVoxelPropertyProvider propertyProvider, String binding, String text, int xPos, int yPos,
            int w, int h, float indicatorValue, boolean snap) {
        this(propertyProvider, binding, text, xPos, yPos, w, h, snap);

        this.indicatorValue = indicatorValue;
        this.indicatorShow = true;
    }

    /**
     * Creates a generic Slider with given specifications.
     * 
     * @param parent The GUI Element, "this"
     * @param binding The numeral binding to be linked to.
     * @param displayText Name of the slider.
     * @param xPos x Coordinate
     * @param yPos y coordinate
     * @param w Width of the slider
     * @param h Height of the slider
     * @param snap If it snaps to whole numbers.
     */
    public VoxelPropertySlider(IVoxelPropertyProvider propertyProvider, String binding, String displayText, int xPos,
            int yPos, int w, int h, boolean snap) {
        super(propertyProvider, binding, displayText, xPos, yPos);

        this.sliderHeight = h;
        this.sliderWidth = w;

        this.handleWidth = this.sliderHeight - 2;
        this.handleHeight = this.sliderHeight + 4;

        this.handleXPos = this.xPosition;
        this.handleXPosMin = this.xPosition + 2;
        this.handleXPosMax = this.xPosition + this.sliderWidth - this.handleWidth - 2;

        this.value = this.propertyProvider.getFloatProperty(binding) - this.valueShift;
        this.valueScale = (this.handleXPosMax - this.handleXPosMin) / (this.valueMax - this.valueMin);

        this.snap = snap;
    }

    public VoxelPropertySlider(IVoxelPropertyProvider propertyProvider, String binding, String displayText,
            String minText, String maxText, int xPos, int yPos, float min, float max) {
        this(propertyProvider, binding, displayText, xPos, yPos, min, max);

        this.minText = minText;
        this.maxText = maxText;
    }

    public VoxelPropertySlider(IVoxelPropertyProvider propertyProvider, String binding, String displayText, int xPos,
            int yPos, float min, float max) {
        this(propertyProvider, binding, displayText, xPos, yPos);

        this.valueMin = min;
        this.valueMax = max;

        if (this.valueMax < this.valueMin || this.valueMax == this.valueMin)
            this.valueMax = this.valueMin + 1;

        this.valueShift = this.valueMin;

        this.value = this.propertyProvider.getFloatProperty(binding) - this.valueShift;
        this.valueScale = (this.handleXPosMax - this.handleXPosMin) / (this.valueMax - this.valueMin);

    }

    public VoxelPropertySlider(IVoxelPropertyProvider propertyProvider, String binding, String displayText,
            String minText, String maxText, int xPos, int yPos) {
        this(propertyProvider, binding, displayText, xPos, yPos);

        this.minText = minText;
        this.maxText = maxText;
    }

    public VoxelPropertySlider(IVoxelPropertyProvider propertyProvider, String binding, String displayText, int xPos,
            int yPos) {
        this(propertyProvider, binding, displayText, xPos, yPos, 100, 8, false);
    }

    @Override
    public void draw(IExtendedGui host, int mouseX, int mouseY) {
        if (Mouse.isButtonDown(0)) {
            if (this.focused) {
                this.moveHandle(mouseX);
            }
        } else {
            this.focused = false;
        }

        this.handleXPos = (int) (this.handleXPosMin + (this.value * this.valueScale));

        int mintextX = this.fontRenderer.getStringWidth(this.minText);

        this.fontRenderer.drawStringWithShadow(this.displayText, this.xPosition, this.yPosition - 12, 0xFFFFFF);
        this.fontRenderer.drawStringWithShadow(this.minText, this.xPosition - mintextX - 2, this.yPosition, 0xFFFFFF);
        this.fontRenderer.drawStringWithShadow(this.maxText, this.xPosition + this.sliderWidth + 2, this.yPosition,
                0xFFFFFF);

        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.xPosition, this.yPosition,
                this.xPosition + this.sliderWidth, this.yPosition + this.sliderHeight, 1, 114, 127, 119, 2);

        if (this.indicatorShow)
            this.drawIndicator(host);
        this.drawHandle(host);
    }

    /**
     * Draws an indicator for a value on the slider.
     */
    private void drawIndicator(IExtendedGui host) {
        int xPosZero = (int) (this.handleXPosMin + ((this.indicatorValue + Math.abs(this.valueMin)) * this.valueScale));

        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, xPosZero - 1, this.yPosition + 1,
                xPosZero + this.handleWidth + 1, this.yPosition + this.sliderHeight - 1, 1, 33, 15, 47, 3);
        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, xPosZero, this.yPosition + 1,
                xPosZero + this.handleWidth, this.yPosition + this.sliderHeight - 1, 0, 121, 128, 128, 2);
    }

    /**
     * Draws the handle for the slider.
     */
    private void drawHandle(IExtendedGui host) {
        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.handleXPos - 1, this.yPosition - 2,
                this.handleXPos + this.handleWidth + 1, this.yPosition + this.handleHeight - 2, 17, 33, 31, 47, 3);
        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.handleXPos, this.yPosition - 2,
                this.handleXPos + this.handleWidth, this.yPosition + this.handleHeight - 2, 0, 121, 128, 128, 3);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    protected boolean mouseIn(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX > x1 && mouseX < x2 && mouseY > y1 && mouseY < y2;
    }

    @SuppressWarnings("cast")
    public void moveHandle(int mouseX) {
        mouseX -= (this.handleWidth / 2);

        if (mouseX < this.handleXPosMin || ((mouseX - this.handleXPosMin) / this.valueScale) < 0)
            this.value = 0;
        else if (mouseX > this.handleXPosMin + (this.handleXPosMax - this.handleXPosMin)
                || ((mouseX - this.handleXPosMin) / this.valueScale) > (this.valueMax - this.valueMin))
            this.value = (this.valueMax - this.valueMin);
        else if (this.snap)
            this.value = (int) ((mouseX - this.handleXPosMin) / this.valueScale);
        else
            this.value = (float) ((mouseX - this.handleXPosMin) / this.valueScale);

        this.propertyProvider.setProperty(this.propertyBinding, this.value + this.valueShift);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY) {
        this.focused = this.mouseIn(mouseX, mouseY, this.xPosition, this.yPosition, this.xPosition + this.sliderWidth,
                this.yPosition + this.sliderHeight);
    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {}
}
