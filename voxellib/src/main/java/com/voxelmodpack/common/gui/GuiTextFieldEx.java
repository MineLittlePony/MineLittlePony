package com.voxelmodpack.common.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.client.overlays.IGuiTextField;

/**
 * 'Dynamic' text field which supports resizing and moving and also syntax
 * highlight
 *
 * @author Adam Mummery-Smith
 */
public class GuiTextFieldEx extends GuiTextField {
    /**
     * Width member is private in the superclass
     */
    protected int xPos, yPos, width, height;

    /**
     * Allowed character filter for this text box
     */
    public String allowedCharacters;

    public int minStringLength = 0;

    protected FontRenderer fontRenderer;

    /**
     * Constructor
     * 
     * @param parentScreen Parent screen
     * @param fontrenderer Font renderer
     * @param xPos X location
     * @param yPos Y location
     * @param width Control width
     * @param height Control height
     * @param initialText Text to initially set
     */
    public GuiTextFieldEx(int id, FontRenderer fontrenderer, int xPos, int yPos, int width, int height,
            String initialText, String allowedCharacters, int maxStringLength) {
        super(id, fontrenderer, xPos, yPos, width, height);
        this.allowedCharacters = allowedCharacters;
        this.setMaxStringLength(maxStringLength);
        this.setText(initialText);

        this.width = width;
    }

    /**
     * Constructor
     * 
     * @param parentScreen Parent screen
     * @param fontrenderer Font renderer
     * @param xPos X location
     * @param yPos Y location
     * @param width Control width
     * @param height Control height
     * @param initialText Text to initially set
     */
    public GuiTextFieldEx(int id, FontRenderer fontrenderer, int xPos, int yPos, int width, int height,
            String initialText) {
        super(id, fontrenderer, xPos, yPos, width, height);
        this.setMaxStringLength(65536);
        this.setText(initialText);

        this.width = width;
    }

    /**
     * Constructor
     * 
     * @param parentScreen Parent screen
     * @param fontrenderer Font renderer
     * @param xPos X location
     * @param yPos Y location
     * @param width Control width
     * @param height Control height
     * @param initialText Text to initially set
     */
    public GuiTextFieldEx(int id, FontRenderer fontrenderer, int xPos, int yPos, int width, int height,
            int initialValue, int digits) {
        super(id, fontrenderer, xPos, yPos, width, height);
        this.setMaxStringLength(digits);
        this.setText(String.valueOf(initialValue));
        this.allowedCharacters = "0123456789";
        this.width = width;
    }

    @Override
    public boolean textboxKeyTyped(char keyChar, int keyCode) {
        if ((this.allowedCharacters == null || this.allowedCharacters.indexOf(keyChar) >= 0) ||
                keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT ||
                keyCode == Keyboard.KEY_HOME || keyCode == Keyboard.KEY_END ||
                keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_BACK) {
            return super.textboxKeyTyped(keyChar, keyCode);
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.src.GuiTextField#func_50038_e()
     */
    @Override
    public void setCursorPositionEnd() {
        try {
            super.setCursorPositionEnd();
        } catch (Exception ex) {}
    }

    public void setSizeAndPosition(int xPos, int yPos, int width, int height) {
        this.setPosition(xPos, yPos);
        this.setSize(width, height);
    }

    public void setSize(int width, int height) {
        ((IGuiTextField) this).setInternalWidth(width);
        ((IGuiTextField) this).setHeight(height);

        this.width = width;
        this.height = height;
    }

    public void setPosition(int xPos, int yPos) {
        ((IGuiTextField) this).setXPosition(xPos);
        ((IGuiTextField) this).setYPosition(yPos);

        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void scrollToEnd() {
        this.setCursorPosition(0);
        this.setCursorPosition(this.getText().length());
    }

    /*
     * (non-Javadoc)
     * @see net.minecraft.src.GuiTextField#func_50032_g(int)
     */
    @Override
    public void setCursorPosition(int cursorPos) {
        super.setCursorPosition(cursorPos);
        super.setCursorPosition(cursorPos);
    }

    /**
     * Synchronise private members from the superclass using reflection
     */
    protected void syncMembers() {
        this.xPos = ((IGuiTextField) this).getXPosition();
        this.yPos = ((IGuiTextField) this).getYPosition();
        this.width = ((IGuiTextField) this).getInternalWidth();
        this.height = ((IGuiTextField) this).getHeight();
    }

    public void drawTextBoxAt(int yPos) {
        try {
            ((IGuiTextField) this).setYPosition(yPos);
            this.yPos = yPos;
            this.drawTextBox();
        } catch (Exception ex) {}
    }

    public int getIntValue(int defaultValue) {
        try {
            return Integer.parseInt(this.getText());
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
