package com.brohoof.minelittlepony.gui;

import com.brohoof.minelittlepony.Value;
import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.VoxelProperty;

import net.minecraft.client.resources.I18n;

public class VoxelPropertyEnum<E extends Enum<E>> extends VoxelProperty<IPropertyProviderEnum<E>> {

    private Value<E> value;
    private E[] possibleValues;
    private String i18NPrefix;
    private int height = 15;

    private int defaultX;
    private int defaultW;

    public VoxelPropertyEnum(Value<E> value, String displayText, String i18nPrefix, int xPos, int yPos,
            Class<E> eclass) {
        super(null, null, displayText, xPos, yPos);
        this.value = value;
        this.i18NPrefix = i18nPrefix;
        this.possibleValues = eclass.getEnumConstants();
        defaultX = xPosition + 150;
        defaultW = 55;
    }

    @Override
    public void draw(IExtendedGui gui, int mouseX, int mouseY) {
        boolean overReset = mouseOverReset(mouseX, mouseY);
        int outset = overReset ? 1 : 0;
        int v = overReset ? 16 : 0;

        drawRect(defaultX - outset, this.yPosition + 11 - outset,
                defaultX + defaultW + outset - 1, this.yPosition + 26 + outset,
                -16777216);
        gui.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256,
                defaultX - 1 - outset, this.yPosition + 10 - outset,
                defaultX + outset + defaultW, this.yPosition + 27 + outset,
                0, v, 16, 16 + v, 4);
        this.drawString(this.mc.fontRendererObj, "Default", defaultX + 8, this.yPosition + 15,
                overReset ? 16777215 : 10066329);

        if (this.displayText != null) {
            this.drawString(this.mc.fontRendererObj, I18n.format(this.displayText), this.xPosition + 15,
                    this.yPosition - 14, 10079487);
        }

        for (int i = 0; i < possibleValues.length; i++) {
            drawRadio(gui, mouseX, mouseY, i);
        }

    }

    public void drawRadio(IExtendedGui host, int mouseX, int mouseY, int idx) {
        boolean overButton = this.mouseOver(mouseX, mouseY, idx);
        boolean checked = this.possibleValues[idx] == value.get();

        int xPos = xPosition + 30;
        int yPos = yPosition + (height * idx);

        E e = possibleValues[idx];
        String text = I18n.format(i18NPrefix + e.toString().toLowerCase());
        this.drawString(this.fontRenderer, text, xPos + 20, yPos + 2, 0xffffff);

        // the border
        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, xPos, yPos,
                xPos + 11, yPos + 11, 0, overButton ? 16 : 0, 16, overButton ? 32 : 16, 4);
        // the check
        host.drawTexturedModalRect(LiteModVoxelCommon.GUIPARTS, xPos, yPos, xPos + 10,
                yPos + 10, checked ? 12 : 0, 52, checked ? 23 : 11, 63);
    }

    protected boolean mouseIn(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX > x1 + 5 && mouseX < x2 + 5 && mouseY > y1 && mouseY < y2;
    }

    protected boolean mouseOverReset(int mouseX, int mouseY) {
        return mouseX > this.defaultX && mouseX < this.defaultX + this.defaultW
                && mouseY > this.yPosition + 10 && mouseY < this.yPosition + 27;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY) {
        if (mouseOverReset(mouseX, mouseY)) {
            value.set(possibleValues[0]);
        } else {
            for (int i = 0; i < possibleValues.length; i++) {
                if (mouseOver(mouseX, mouseY, i)) {
                    value.set(possibleValues[i]);
                    return;
                }
            }
        }
    }

    protected boolean mouseOver(int mouseX, int mouseY, int idx) {
        E e = possibleValues[idx];
        int yPos = yPosition + (height * idx);
        int width = 20 + mc.fontRendererObj.getStringWidth(I18n.format(i18NPrefix + e.toString().toLowerCase()));

        return mouseX > this.xPosition + 30 && mouseX < this.xPosition + width + 30
                && mouseY > yPos && mouseY < yPos + 11;
    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {}
}
