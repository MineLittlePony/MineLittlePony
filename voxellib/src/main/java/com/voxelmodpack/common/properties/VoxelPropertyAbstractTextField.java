package com.voxelmodpack.common.properties;

import org.lwjgl.input.Keyboard;

import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;

/**
 * Adapted from xTiming's text field code
 * 
 * @author Adam Mummery-Smith
 */
public abstract class VoxelPropertyAbstractTextField<PropertyType extends IVoxelPropertyProvider>
        extends VoxelProperty<PropertyType> {
    protected String allowedCharacters = "0123456789";
    protected String fieldValue = "0";
    protected String defaultFieldValue = "0";
    protected int fieldOffset = 74;
    protected int fieldWidth = 52;
    protected int fieldHeight = 16;

    public VoxelPropertyAbstractTextField(IVoxelPropertyProvider propertyProvider, String binding, String displayText,
            int xPos, int yPos, int fieldOffset) {
        super(propertyProvider, binding, displayText, xPos, yPos);

        this.fieldValue = this.propertyProvider.getStringProperty(binding);
        this.defaultFieldValue = this.propertyProvider.getDefaultPropertyValue(this.propertyBinding);
        this.fieldOffset = fieldOffset;
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param fieldValue
     * @param focused
     * @param field_146128_h
     * @param field_146129_i
     * @return
     */
    @Override
    public void draw(IExtendedGui host, int mouseX, int mouseY) {
        drawRect(this.xPosition + this.fieldOffset + this.fieldWidth, this.yPosition + this.fieldHeight,
                this.xPosition + this.fieldOffset, this.yPosition - 1, this.focused ? 0xFFFFFFFF : 0xFF999999);
        drawRect(this.xPosition + this.fieldOffset + this.fieldWidth - 1, this.yPosition + this.fieldHeight - 1,
                this.xPosition + this.fieldOffset + 1, this.yPosition, 0xFF000000);

        boolean optionCursor = this.focused && (this.cursorCounter / 6) % 2 == 0;
        this.drawString(this.fontRenderer, this.displayText, this.xPosition, this.yPosition + 4, 0xFFFFFF);
        this.drawString(this.fontRenderer, this.fieldValue + (optionCursor ? "_" : ""),
                this.fieldValue.length() > 0
                        ? (this.xPosition + this.fieldOffset + (this.fieldWidth / 2))
                                - (this.fontRenderer.getStringWidth(this.fieldValue) / 2)
                        : this.xPosition + this.fieldOffset + (this.fieldWidth / 2) - 3,
                this.yPosition + 4, 0xFFFFFF);
    }

    /**
     * @param keyChar
     * @param keyCode
     */
    @Override
    public void keyTyped(char keyChar, int keyCode) {
        if (this.focused) {
            if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER || keyCode == Keyboard.KEY_ESCAPE)
                this.onLostFocus();

            if (keyCode == Keyboard.KEY_BACK && this.fieldValue.length() > 0)
                this.fieldValue = this.fieldValue.substring(0, this.fieldValue.length() - 1);

            if (keyCode == Keyboard.KEY_PERIOD && this.allowedCharacters.indexOf('.') >= 0) {
                if (this.fieldValue.indexOf(keyChar) >= 0)
                    return;
                if (this.fieldValue.length() == 0)
                    this.fieldValue += "0";
            }

            if (this.allowedCharacters.indexOf(keyChar) >= 0 && this.fieldValue.length() < 4)
                this.fieldValue += keyChar;

            if (this.checkInvalidValue())
                this.fieldValue = this.defaultFieldValue;
        }
    }

    /**
     * @return
     */
    protected abstract boolean checkInvalidValue();

    /**
     * @param mouseX
     * @param mouseY
     */
    @Override
    public void mouseClicked(int mouseX, int mouseY) {
        boolean mouseOver = this.mouseOver(mouseX, mouseY);

        if (!this.focused && mouseOver) {
            this.playClickSound(this.mc.getSoundHandler());
            this.fieldValue = this.propertyProvider.getStringProperty(this.propertyBinding);
        } else if (this.focused && !mouseOver) {
            this.onLostFocus();
        }

        this.focused = mouseOver;
    }

    @Override
    public void onClosed() {
        this.onLostFocus();
    }

    protected abstract void onLostFocus();

    /**
     * @param mouseX
     * @param mouseY
     * @return
     */
    public boolean mouseOver(int mouseX, int mouseY) {
        return mouseX > this.xPosition + this.fieldOffset
                && mouseX < this.xPosition + this.fieldOffset + this.fieldWidth && mouseY > this.yPosition
                && mouseY < this.yPosition + 15;
    }

    public void update() {
        this.fieldValue = this.propertyProvider.getStringProperty(this.propertyBinding);
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public void setFocused(boolean focus) {
        if (this.focused && !focus) {
            this.onLostFocus();
        }

        this.focused = focus;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }
}