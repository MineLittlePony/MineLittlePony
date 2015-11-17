package com.brohoof.minelittlepony.gui;

import com.brohoof.minelittlepony.Value;
import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.VoxelProperty;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderInteger;

import net.minecraft.client.resources.I18n;

public class FakeVoxelPropertyToggleBox extends VoxelProperty<IVoxelPropertyProviderInteger> {

    private Value<Boolean> value;
    private int width = 11;

    public FakeVoxelPropertyToggleBox(Value<Boolean> value, String text, int xPos,
            int yPos) {
        super(null, null, text, xPos, yPos);
        this.value = value;
        this.width = this.fontRenderer.getStringWidth(I18n.format(this.displayText)) + 20;
    }

    @Override
    public void draw(IExtendedGui host, int mouseX, int mouseY) {
        this.drawString(this.fontRenderer, I18n.format(this.displayText), this.xPosition + 20, this.yPosition + 2, 16777215);
        boolean overButton = this.mouseOver(mouseX, mouseY);
        boolean checked = this.value.get();

        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.xPosition, this.yPosition,
                this.xPosition + 11, this.yPosition + 11, 0, overButton ? 16 : 0, 16, overButton ? 32 : 16, 4);
        host.drawTexturedModalRect(LiteModVoxelCommon.GUIPARTS, this.xPosition, this.yPosition, this.xPosition + 10,
                this.yPosition + 10, checked ? 12 : 0, 52, checked ? 23 : 11, 63);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY) {
        if (this.mouseOver(mouseX, mouseY)) {
            value.set(!value.get());
        }
    }

    public boolean mouseOver(int mouseX, int mouseY) {
        return mouseX > this.xPosition && mouseX < this.xPosition + this.width && mouseY > this.yPosition
                && mouseY < this.yPosition + 11;
    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {}
}
