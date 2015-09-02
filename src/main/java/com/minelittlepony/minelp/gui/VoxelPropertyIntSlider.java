package com.minelittlepony.minelp.gui;

import org.lwjgl.input.Mouse;

import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.VoxelProperty;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderInteger;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;

public class VoxelPropertyIntSlider extends VoxelProperty<IVoxelPropertyProviderInteger> {
    int minValue;
    int maxValue;
    float value;
    boolean overReset;
    boolean overSlide;
    boolean overBar;
    boolean setBar;
    boolean dragging;
    int offset;
    String minText;
    String midText;
    String maxText;
    String labelText;

    public VoxelPropertyIntSlider(IVoxelPropertyProviderInteger parent, String binding, String text, int xPos,
            int yPos) {
        super(parent, binding, text, xPos, yPos);
        this.minValue = 0;
        this.maxValue = 2;
        this.value = 2.0F;
        this.overReset = false;
        this.overSlide = false;
        this.overBar = false;
        this.setBar = false;
        this.dragging = false;
        this.offset = 0;
        this.minText = I18n.format("minelp.options.ponylevel.human");
        this.midText = I18n.format("minelp.options.ponylevel.mix");
        this.maxText = I18n.format("minelp.options.ponylevel.pony");
    }

    @Override
    public void draw(IExtendedGui gui, int mouseX, int mouseY) {
        this.overReset = this.mouseOverReset(mouseX, mouseY);
        int outset = this.overReset ? 1 : 0;
        int v = this.overReset ? 16 : 0;
        drawRect(this.xPosition + 160 - outset, this.yPosition + 11 - outset, this.xPosition + 212 + outset,
                this.yPosition + 26 + outset, -16777216);
        gui.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.xPosition + 159 - outset,
                this.yPosition + 10 - outset, this.xPosition + 213 + outset, this.yPosition + 27 + outset, 0, v, 16,
                16 + v, 4);
        this.drawString(this.mc.fontRendererObj, "Default", this.xPosition + 169, this.yPosition + 15,
                this.overReset ? 16777215 : 10066329);
        int sliderLeft = this.xPosition + 48;
        int sliderRight = this.xPosition + 124;
        int sliderXPos = sliderLeft + 32;
        int sliderXPos2 = sliderLeft + 45;
        int sliderYPos = this.yPosition + 12;
        int sliderYPos2 = this.yPosition + 25;
        int sliderMinX = sliderLeft - (sliderXPos + sliderXPos2) / 2 + 5;
        int sliderMaxX = sliderRight - (sliderXPos + sliderXPos2) / 2 - 5;
        this.drawHorizontalLine(sliderLeft, sliderRight, this.yPosition + 18, -6710887);
        this.drawVerticalLine(this.xPosition + 86, this.yPosition + 14, this.yPosition + 22, -6710887);
        this.drawVerticalLine(sliderLeft, this.yPosition + 14, this.yPosition + 22, -6710887);
        this.drawVerticalLine(sliderRight, this.yPosition + 14, this.yPosition + 22, -6710887);
        if (this.displayText != null) {
            this.drawString(this.mc.fontRendererObj, this.displayText, this.xPosition + 15, this.yPosition - 14, 10079487);
        }

        this.drawString(this.mc.fontRendererObj, this.minText, this.xPosition + 35, this.yPosition, 16777215);
        if (this.midText != null) {
            this.drawString(this.mc.fontRendererObj, this.midText, this.xPosition + 80, this.yPosition, 16777215);
        }

        if (this.maxText != null) {
            this.drawString(this.mc.fontRendererObj, this.maxText, this.xPosition + 110, this.yPosition, 16777215);
        } else {
            float scale = (this.value + 1.0F) / 2.0F;
            int displayValue = MathHelper.ceiling_float_int(
                    (this.minValue + (this.maxValue - this.minValue) * scale) * 100.0F);
            this.drawString(this.mc.fontRendererObj, displayValue + "%", this.xPosition + 130, this.yPosition + 15,
                    16777215);
        }

        this.overSlide = this.mouseIn(mouseX, mouseY, sliderXPos, sliderYPos, sliderXPos2, sliderYPos2);
        this.overBar = this.mouseIn(mouseX, mouseY, sliderLeft, sliderYPos, sliderRight, sliderYPos2)
                && !this.overSlide;
        if (this.dragging) {
            if (Mouse.isButtonDown(0)) {
                this.offset = Math.min(Math.max(mouseX - (sliderXPos + sliderXPos2) / 2, sliderMinX), sliderMaxX);
                this.value = (float) this.offset / (float) sliderMaxX;
            } else {
                this.value = (float) this.offset / (float) sliderMaxX;
                this.propertyProvider.setProperty(this.propertyBinding,
                        Math.round(this.value) + 1);
                this.dragging = false;
            }
        } else {
            this.offset = (this.propertyProvider.getIntProperty(this.propertyBinding)
                    - 1) * sliderMaxX;
            this.value = (float) this.offset / (float) sliderMaxX;
        }

        if (this.setBar) {
            this.offset = mouseX - (sliderXPos + sliderXPos2) / 2;
            this.value = (float) this.offset / (float) sliderMaxX;
            this.propertyProvider.setProperty(this.propertyBinding,
                    Math.round(this.value) + 1);
            this.setBar = false;
            this.dragging = true;
        }

        if (this.offset > sliderMaxX) {
            this.offset = sliderMaxX;
        }

        if (this.offset < sliderMinX) {
            this.offset = sliderMinX;
        }

        drawRect(sliderXPos2 - 1 + this.offset, sliderYPos2 - 1, sliderXPos + 1 + this.offset, sliderYPos + 1,
                -16777216);
        gui.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, sliderXPos + this.offset, sliderYPos,
                sliderXPos2 + this.offset, sliderYPos2, 0, !this.overSlide && !this.dragging ? 0 : 16, 16,
                !this.overSlide && !this.dragging ? 16 : 32, 4);
    }

    protected boolean mouseIn(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX > x1 + this.offset && mouseX < x2 + this.offset && mouseY > y1 && mouseY < y2;
    }

    protected boolean mouseOverReset(int mouseX, int mouseY) {
        return mouseX > this.xPosition + 159 && mouseX < this.xPosition + 213 && mouseY > this.yPosition + 10
                && mouseY < this.yPosition + 27;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY) {
        if (this.overSlide) {
            this.dragging = true;
            this.playClickSound(this.mc.getSoundHandler());
        } else if (this.overBar) {
            this.setBar = true;
            this.playClickSound(this.mc.getSoundHandler());
        } else if (this.overReset) {
            this.propertyProvider.setProperty(this.propertyBinding, 2);
            this.playClickSound(this.mc.getSoundHandler());
        }

    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {}
}
