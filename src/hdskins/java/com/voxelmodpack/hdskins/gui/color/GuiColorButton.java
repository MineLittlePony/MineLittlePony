package com.voxelmodpack.hdskins.gui.color;

import static com.mumfrey.liteloader.gl.GL.glColor4f;

import java.awt.Color;

import net.minecraft.client.Minecraft;

/**
 * Colour picker button control, spawns a colour picker when clicked
 * 
 * @author Adam Mummery-Smith
 */
public class GuiColorButton extends GuiControl {
    /**
     * Picker active colour
     */
    private int colour = 0xFF000000;

    private Color lineColour;

    private GuiColorPicker picker;

    private boolean pickerClicked = false;
    private CloseListener closeListener;

    public GuiColorButton(Minecraft minecraft, int id, int xPosition, int yPosition, int controlWidth, int controlHeight, Color lineColour, String name, CloseListener cl) {
        super(minecraft, id, xPosition, yPosition, controlWidth, controlHeight, name);
        this.lineColour = lineColour;
        this.updateColour(lineColour);
        this.closeListener = cl;
    }

    /**
     * @param lineColour2
     */
    public void updateColour(Color lineColour2) {
        if (lineColour2 == this.lineColour) {
            this.colour = lineColour2.getRGB();
        }
    }

    public int getColor() {
        return this.colour;
    }

    @Override
    public void drawControl(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {

            if (this.displayString != null && this.displayString.length() > 0) {
                this.drawString(minecraft.fontRendererObj, this.displayString, this.xPosition + this.width + 8, this.yPosition + (this.height - 8) / 2, 0xFFFFFFFF);
            }
            boolean mouseOver = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int borderColour = mouseOver || this.picker != null ? 0xFFFFFFFF : 0xFFA0A0A0;

            drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, borderColour);

            int v = Math.min(Math.max((int) (((float) this.height / (float) this.width) * 1024F), 256), 1024);

            minecraft.getTextureManager().bindTexture(GuiColorPicker.COLOURPICKER_CHECKER);
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, 0, 0, 1024, v);

            drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, this.colour);

            this.mouseDragged(minecraft, mouseX, mouseY);

        }
    }

    public void drawPicker(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible && this.picker != null) {
            this.picker.drawButton(minecraft, mouseX, mouseY);

            if (this.picker.getDialogResult() == DialogResult.OK) {
                this.closePicker(true);
            } else if (this.picker.getDialogResult() == DialogResult.Cancel) {
                this.closePicker(false);
            }
        }
    }

    public void closePicker(boolean getColour) {
        if (getColour)
            this.colour = this.picker.getColour();
        this.picker = null;
        this.pickerClicked = false;
        if (this.closeListener != null)
            this.closeListener.onClose();
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
                int xPos = Math.min(this.xPosition + this.width, GuiControl.lastScreenWidth - 233);
                int yPos = Math.min(this.yPosition, GuiControl.lastScreenHeight - 175);

                this.picker = new GuiColorPicker(minecraft, 1, xPos, yPos, this.colour, "Choose colour");
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

    public interface CloseListener {
        void onClose();
    }
}