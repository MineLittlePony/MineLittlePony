package com.voxelmodpack.common.properties;

import org.lwjgl.input.Keyboard;

import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderInteger;

/**
 * Adapted from xTiming's key bind button code
 * 
 * @author Adam Mummery-Smith
 */
public class VoxelPropertyKeyBinding extends VoxelProperty<IVoxelPropertyProviderInteger> {
    private int buttonOffset = 60;
    private int buttonWidth = 70;
    private int buttonHeight = 16;

    /**
     * @param parent
     * @param binding
     * @param displayText
     * @param xPos
     * @param yPos
     */
    public VoxelPropertyKeyBinding(IVoxelPropertyProvider propertyProvider, String binding, String displayText,
            int xPos, int yPos) {
        super(propertyProvider, binding, displayText, xPos, yPos);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param displayText
     * @param keyCode
     * @param bindActive
     * @param field_146128_h
     * @param field_146129_i
     * @return
     */
    @Override
    public void draw(IExtendedGui host, int mouseX, int mouseY) {
        boolean overKey = this.focused || this.mouseOver(mouseX, mouseY);
        int outset = overKey ? 1 : 0;
        int v = overKey ? 16 : 0;
        int keyCode = this.propertyProvider.getIntProperty(this.propertyBinding);

        this.drawString(this.fontRenderer, this.displayText, this.xPosition, this.yPosition + 4, 0xFFFFFF);
        String fKey = this.focused ? "Press a key" : Keyboard.getKeyName(keyCode);
        drawRect(this.xPosition + this.buttonOffset + this.buttonWidth - 1, this.yPosition + this.buttonHeight,
                this.xPosition + this.buttonOffset + 1, this.yPosition, 0xFF000000);
        host.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256,
                this.xPosition + this.buttonOffset - outset, this.yPosition - 1 - outset,
                this.xPosition + this.buttonOffset + this.buttonWidth + outset,
                this.yPosition + 1 + this.buttonHeight + outset, 0, v, 16, v + 16, 4);
        this.drawString(this.fontRenderer, fKey,
                (this.xPosition + this.buttonOffset + (this.buttonWidth / 2))
                        - (this.fontRenderer.getStringWidth(fKey) / 2),
                this.yPosition + 4, overKey || this.focused ? 0xFFFFFF : 0x999999);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.voxelbox.voxelflight.VoxelFlightGUI.VoxelProperty#MouseClicked(int,
     * int)
     */
    @Override
    public void mouseClicked(int mouseX, int mouseY) {
        boolean mouseOver = this.mouseOver(mouseX, mouseY);

        if (!this.focused && mouseOver)
            this.playClickSound(this.mc.getSoundHandler());

        this.focused = mouseOver;
    }

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

    /*
     * (non-Javadoc)
     * @see com.voxelbox.voxelflight.VoxelFlightGUI.VoxelProperty#KeyTyped(char,
     * int)
     */
    @Override
    public void keyTyped(char keyChar, int keyCode) {
        if (this.focused) {
            this.propertyProvider.setProperty(this.propertyBinding, keyCode);
            this.focused = false;
        }
    }
}
