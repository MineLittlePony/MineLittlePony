package com.voxelmodpack.common.properties;

import net.minecraft.util.MathHelper;

import org.lwjgl.input.Mouse;

import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderFloat;

/**
 * @author xTiming
 */
public class VoxelPropertySliderOld extends VoxelProperty<IVoxelPropertyProviderFloat> {
    float minValue = -1.0F;

    float maxValue = 1.0F;

    float value = 0F;

    boolean overReset = false;
    boolean overSlide = false;
    boolean overBar = false;
    boolean setBar = false;
    boolean dragging = false;

    int offset = 0;

    String minText = "Min";
    String maxText = "Max";

    String labelText;

    public VoxelPropertySliderOld(IVoxelPropertyProvider propertyProvider, String binding, String displayText,
            String minText, String maxText, int xPos, int yPos, float min, float max) {
        this(propertyProvider, binding, displayText, minText, maxText, xPos, yPos);
        this.minValue = min;
        this.maxValue = max;
    }

    public VoxelPropertySliderOld(IVoxelPropertyProvider propertyProvider, String binding, String displayText, int xPos,
            int yPos, float min, float max) {
        this(propertyProvider, binding, displayText, xPos, yPos);
    }

    public VoxelPropertySliderOld(IVoxelPropertyProvider propertyProvider, String binding, String displayText,
            String minText, String maxText, int xPos, int yPos) {
        this(propertyProvider, binding, displayText, xPos, yPos);

        this.minText = minText;
        this.maxText = maxText;
    }

    public VoxelPropertySliderOld(IVoxelPropertyProvider propertyProvider, String binding, String displayText, int xPos,
            int yPos) {
        super(propertyProvider, binding, displayText, xPos, yPos);
    }

    @Override
    public void draw(IExtendedGui host, int mouseX, int mouseY) {
        if (this.displayText != null) {
            this.drawString(this.mc.fontRendererObj, this.displayText, this.xPosition + 18, this.yPosition, 0x99CCFF);
        }

        this.drawString(this.mc.fontRendererObj, this.minText, this.xPosition + 30, this.yPosition + 15, 0xFFFFFF);

        if (this.maxText != null) {
            this.drawString(this.mc.fontRendererObj, this.maxText, this.xPosition + 130, this.yPosition + 15, 0xFFFFFF);
        } else {
            float scale = (this.value + 1.0F) / 2.0F;
            int displayValue = MathHelper
                    .ceiling_float_int((this.minValue + ((this.maxValue - this.minValue) * scale)) * 100F);

            this.drawString(this.mc.fontRendererObj, displayValue + "%", this.xPosition + 130, this.yPosition + 15,
                    0xFFFFFF);
        }

        this.overReset = this.mouseOverReset(mouseX, mouseY);

        int outset = this.overReset ? 1 : 0;
        int v = this.overReset ? 16 : 0;

        drawRect(this.xPosition + 160 - outset, this.yPosition + 11 - outset, this.xPosition + 212 + outset,
                this.yPosition + 26 + outset, 0xFF000000);
        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.xPosition + 159 - outset,
                this.yPosition + 10 - outset, this.xPosition + 213 + outset, this.yPosition + 27 + outset, 0, v, 16,
                16 + v, 4);
        this.drawString(this.mc.fontRendererObj, "Default", this.xPosition + 169, this.yPosition + 15,
                this.overReset ? 0xFFFFFF : 0x999999);

        int sliderLeft = this.xPosition + 48;
        int sliderRight = this.xPosition + 124;
        int sliderXPos = sliderLeft + 32;
        int sliderXPos2 = sliderLeft + 45;
        int sliderYPos = this.yPosition + 12;
        int sliderYPos2 = this.yPosition + 25;
        int sliderMinX = sliderLeft - ((sliderXPos + sliderXPos2) / 2) + 5;
        int sliderMaxX = sliderRight - ((sliderXPos + sliderXPos2) / 2) - 5;

        this.drawHorizontalLine(sliderLeft, sliderRight, this.yPosition + 18, 0xFF999999);
        this.drawVerticalLine(this.xPosition + 86, this.yPosition + 14, this.yPosition + 22, 0xFF999999);
        this.drawVerticalLine(sliderLeft, this.yPosition + 14, this.yPosition + 22, 0xFF999999);
        this.drawVerticalLine(sliderRight, this.yPosition + 14, this.yPosition + 22, 0xFF999999);

        this.overSlide = this.mouseIn(mouseX, mouseY, sliderXPos, sliderYPos, sliderXPos2, sliderYPos2);
        this.overBar = this.mouseIn(mouseX, mouseY, sliderLeft, sliderYPos, sliderRight, sliderYPos2)
                && !this.overSlide;

        if (this.dragging) {
            if (Mouse.isButtonDown(0)) {
                this.offset = Math.min(Math.max(mouseX - (sliderXPos + sliderXPos2) / 2, sliderMinX), sliderMaxX);
                this.value = ((float) this.offset / (float) sliderMaxX);
            } else {
                this.value = ((float) this.offset / (float) sliderMaxX);
                this.propertyProvider.setProperty(this.propertyBinding, this.value);
                this.dragging = false;
            }
        } else {
            this.offset = (int) ((this.propertyProvider.getFloatProperty(this.propertyBinding)) * sliderMaxX);
            this.value = ((float) this.offset / (float) sliderMaxX);
        }

        if (this.setBar) {
            this.offset = mouseX - (sliderXPos + sliderXPos2) / 2;
            this.value = ((float) this.offset / (float) sliderMaxX);
            this.propertyProvider.setProperty(this.propertyBinding, this.value);
            this.setBar = false;
            this.dragging = true;
        }

        if (this.offset > sliderMaxX)
            this.offset = sliderMaxX;
        if (this.offset < sliderMinX)
            this.offset = sliderMinX;

        drawRect(sliderXPos2 - 1 + this.offset, sliderYPos2 - 1, sliderXPos + 1 + this.offset, sliderYPos + 1,
                0xFF000000);
        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, (sliderXPos + this.offset), sliderYPos,
                (sliderXPos2 + this.offset), sliderYPos2, 0, (this.overSlide || this.dragging) ? 16 : 0, 16,
                (this.overSlide || this.dragging) ? 32 : 16, 4);
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
        return mouseX > x1 + this.offset && mouseX < x2 + this.offset && mouseY > y1 && mouseY < y2;
    }

    /**
     * @param mouseX
     * @param mouseY
     * @return
     */
    protected boolean mouseOverReset(int mouseX, int mouseY) {
        return mouseX > this.xPosition + 159 && mouseX < this.xPosition + 213 && mouseY > this.yPosition + 10
                && mouseY < this.yPosition + 27;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY) {
        if (this.overSlide) {
            this.dragging = true;
            this.playClickSound(this.mc.getSoundHandler());
            return;
        }

        if (this.overBar) {
            this.setBar = true;
            this.playClickSound(this.mc.getSoundHandler());
            return;
        }

        if (this.overReset) {
            this.propertyProvider.setProperty(this.propertyBinding, 0.0F);
            this.playClickSound(this.mc.getSoundHandler());
        }
    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {}
}
