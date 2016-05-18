package com.voxelmodpack.hdskins.gui;

import static net.minecraft.client.renderer.GlStateManager.color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

/**
 * Color picker button control, spawns a color picker when clicked. Code
 * originally by Adam Mummery-Smith.
 */
public class GuiColorButton extends GuiButton {

    private Minecraft mc;
    private int color = 0x000000;
    private GuiColorPicker picker;
    private boolean pickerClicked = false;

    public GuiColorButton(Minecraft minecraft, int id, int xPosition, int yPosition, int controlWidth, int controlHeight, int color, String name) {
        super(id, xPosition, yPosition, controlWidth, controlHeight, I18n.format(name));
        this.mc = minecraft;
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        boolean mouseOver = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int borderColor = mouseOver || this.picker != null ? 0xFFFFFFFF : 0xFFA0A0A0;

        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, borderColor);

        color(1.0F, 1.0F, 1.0F, 1.0F);

        drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, 0xFF000000 | this.color);

        this.mouseDragged(mc, mouseX, mouseY);

        if (this.displayString != null && this.displayString.length() > 0) {
            int x = this.xPosition + this.width + 8;
            int y = this.yPosition + (this.height - 8) / 2;
            this.drawString(mc.fontRendererObj, this.displayString, x, y, this.enabled ? 0xFFFFFFFF : 0xFFA0A0A0);
        }
    }

    public void drawPicker(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.picker != null) {
            this.picker.drawButton(minecraft, mouseX, mouseY);

        }
    }

    public void closePicker(boolean getColor) {
        if (getColor)
            this.color = this.picker.getColor();
        this.picker = null;
        this.pickerClicked = false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        if (this.pickerClicked && this.picker != null) {
            this.picker.mouseReleased(mouseX, mouseY);
            this.pickerClicked = false;
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        boolean pressed = super.mousePressed(minecraft, mouseX, mouseY);

        if (this.picker == null) {
            if (pressed) {
                int xPos = Math.min(this.xPosition + this.width, mc.currentScreen.width - 233);
                int yPos = Math.min(this.yPosition, mc.currentScreen.height - 175);

                this.picker = new GuiColorPicker(minecraft, 1, xPos, yPos, 0xFFFFFF & this.color, "Choose color");
                this.pickerClicked = false;
            }

            return pressed;
        }

        this.pickerClicked = this.picker.mousePressed(minecraft, mouseX, mouseY);

        if (pressed && !this.pickerClicked) {
            this.closePicker(true);
        }

        return this.pickerClicked;
    }

    public boolean keyTyped(char keyChar, int keyCode) {
        return (this.picker != null) ? this.picker.textBoxKeyTyped(keyChar, keyCode) : false;
    }

}