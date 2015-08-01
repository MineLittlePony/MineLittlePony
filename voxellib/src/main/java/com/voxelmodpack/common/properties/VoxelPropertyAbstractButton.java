package com.voxelmodpack.common.properties;

import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;

public abstract class VoxelPropertyAbstractButton<PropertyType extends IVoxelPropertyProvider>
        extends VoxelProperty<PropertyType> {
    private int buttonOffset = 60;
    private int buttonWidth = 70;
    private int buttonHeight = 16;

    public VoxelPropertyAbstractButton(IVoxelPropertyProvider propertyProvider, String binding, String displayText,
            int xPos, int yPos) {
        this(propertyProvider, binding, displayText, xPos, yPos, 60, 70, 16);
    }

    public VoxelPropertyAbstractButton(IVoxelPropertyProvider propertyProvider, String binding, String displayText,
            int xPos, int yPos, int buttonOffset, int buttonWidth, int buttonHeight) {
        super(propertyProvider, binding, displayText, xPos, yPos);
        this.buttonOffset = buttonOffset;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
    }

    @Override
    public void draw(IExtendedGui host, int mouseX, int mouseY) {
        boolean overButton = this.mouseOver(mouseX, mouseY);

        int outset = overButton ? 1 : 0;
        int v = overButton ? 16 : 0;
        int colour = overButton ? 0xFFFFFF : 0x999999;

        drawRect(this.xPosition + this.buttonOffset + this.buttonWidth - 1, this.yPosition + this.buttonHeight,
                this.xPosition + this.buttonOffset + 1, this.yPosition, 0xFF000000);
        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256,
                this.xPosition + this.buttonOffset - outset, this.yPosition - 1 - outset,
                this.xPosition + this.buttonOffset + this.buttonWidth + outset,
                this.yPosition + 1 + this.buttonHeight + outset, 0, v, 16, v + 16, 4);

        this.drawString(this.fontRenderer, this.displayText,
                (this.xPosition + this.buttonOffset + (this.buttonWidth / 2))
                        - (this.fontRenderer.getStringWidth(this.displayText) / 2),
                this.yPosition + 4, colour);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY) {
        if (this.mouseOver(mouseX, mouseY)) {
            this.onClick();
        }
    }

    protected abstract void onClick();

    /**
     * @param mouseX
     * @param mouseY
     * @return
     */
    public boolean mouseOver(int mouseX, int mouseY) {
        return mouseX > this.xPosition + this.buttonOffset
                && mouseX < this.xPosition + this.buttonOffset + this.buttonWidth && mouseY >= this.yPosition
                && mouseY <= this.yPosition + this.buttonHeight;
    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {}
}
