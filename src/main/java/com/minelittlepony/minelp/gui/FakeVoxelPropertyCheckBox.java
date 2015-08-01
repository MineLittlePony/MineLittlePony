package com.minelittlepony.minelp.gui;

import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.VoxelProperty;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderInteger;

public class FakeVoxelPropertyCheckBox extends VoxelProperty<IVoxelPropertyProviderInteger> {
    private int width = 11;

    public FakeVoxelPropertyCheckBox(IVoxelPropertyProvider propertyProvider, String binding, String text, int xPos,
            int yPos) {
        super(propertyProvider, binding, text, xPos, yPos);
        this.width = this.fontRenderer.getStringWidth(this.displayText) + 20;
    }

    @Override
    public void draw(IExtendedGui host, int mouseX, int mouseY) {
        this.drawString(this.fontRenderer, this.displayText, this.xPosition + 20, this.yPosition + 2, 16777215);
        boolean overButton = this.mouseOver(mouseX, mouseY);
        boolean checked = true;

        try {
            int e = this.propertyProvider.getIntProperty(this.propertyBinding);
            if (e < 2 && e > -1) {
                if (e == 0) {
                    checked = false;
                } else {
                    checked = true;
                }
            }
        } catch (Exception var7) {
            ;
        }

        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.xPosition, this.yPosition,
                this.xPosition + 11, this.yPosition + 11, 0, overButton ? 16 : 0, 16, overButton ? 32 : 16, 4);
        host.drawTexturedModalRect(LiteModVoxelCommon.GUIPARTS, this.xPosition, this.yPosition, this.xPosition + 10,
                this.yPosition + 10, checked ? 12 : 0, 52, checked ? 23 : 11, 63);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY) {
        if (this.mouseOver(mouseX, mouseY)) {
            boolean checked = true;

            try {
                int e = this.propertyProvider.getIntProperty(this.propertyBinding);
                if (e < 2 && e > -1) {
                    if (e == 0) {
                        checked = false;
                    } else {
                        checked = true;
                    }
                }
            } catch (Exception var5) {
                ;
            }

            if (checked) {
                this.propertyProvider.setProperty(this.propertyBinding, 0);
            } else {
                this.propertyProvider.setProperty(this.propertyBinding, 1);
            }
        }

    }

    public boolean mouseOver(int mouseX, int mouseY) {
        return mouseX > this.xPosition && mouseX < this.xPosition + this.width && mouseY > this.yPosition
                && mouseY < this.yPosition + 11;
    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {}
}
