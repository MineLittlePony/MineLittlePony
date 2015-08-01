package com.voxelmodpack.common.properties;

import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderBoolean;

/**
 * Adapted from xTiming's checkbox code
 * 
 * @author Adam Mummery-Smith
 */
public class VoxelPropertyCheckBox extends VoxelPropertyToggleButton {
    private int width = 11;

    private final VoxelPropertyCheckBox parent;

    public VoxelPropertyCheckBox(IVoxelPropertyProviderBoolean propertyProvider, String binding, String text, int xPos,
            int yPos) {
        this(propertyProvider, binding, text, xPos, yPos, null);
    }

    public VoxelPropertyCheckBox(IVoxelPropertyProviderBoolean propertyProvider, String binding, String text, int xPos,
            int yPos, VoxelPropertyCheckBox parent) {
        super(propertyProvider, binding, text, xPos, yPos);

        this.width = this.fontRenderer.getStringWidth(this.displayText) + 20;
        this.parent = parent;
    }

    public VoxelPropertyCheckBox getParent() {
        return this.parent;
    }

    @Override
    public void draw(IExtendedGui host, int mouseX, int mouseY) {
        this.drawString(this.fontRenderer, this.displayText, this.xPosition + 20, this.yPosition + 2,
                this.isParentChecked() ? 0xFFFFFF : 0x666666);

        boolean overButton = this.mouseOver(mouseX, mouseY);
        boolean checked = this.isChecked();

        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.xPosition, this.yPosition,
                this.xPosition + 11, this.yPosition + 11, 0, overButton ? 16 : 0, 16, overButton ? 32 : 16, 4);
        host.drawTexturedModalRect(LiteModVoxelCommon.GUIPARTS, this.xPosition, this.yPosition, this.xPosition + 10,
                this.yPosition + 10, checked ? 12 : 0, 52, checked ? 23 : 11, 63);
    }

    /**
     * @return
     */
    boolean isChecked() {
        return this.isParentChecked() && this.propertyProvider.getBoolProperty(this.propertyBinding);
    }

    /**
     * @return
     */
    boolean isParentChecked() {
        return this.parent == null || this.parent.isChecked();
    }

    @Override
    public boolean mouseOver(int mouseX, int mouseY) {
        return this.isParentChecked() && mouseX > this.xPosition && mouseX < this.xPosition + this.width
                && mouseY > this.yPosition && mouseY < this.yPosition + 11;
    }
}
