package com.voxelmodpack.common.properties.gui;

import static com.mumfrey.liteloader.gl.GL.*;

import org.lwjgl.input.Mouse;

import com.voxelmodpack.common.LiteModVoxelCommon;

/**
 * @author anangrybeaver
 */
public class SettingsPanelScrollBar {
    public boolean mouseHeld = false;

    private final int xPos, yPos;
    private final int width;
    private int height;

    private int handleY;

    public SettingsPanelScrollBar(int x, int y, int width, int height, int startingValue) {
        this.xPos = x;
        this.yPos = y;

        this.width = width;
        this.height = height;

        this.moveHandle(startingValue);
    }

    @SuppressWarnings("cast")
    public double getValue() {
        return ((double) this.handleY - (double) this.yPos - 3) / (double) this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public void moveHandle(int mouseY) {
        this.handleY = mouseY - this.width / 2;

        if (this.handleY < this.yPos + 3)
            this.handleY = this.yPos + 3;

        if (this.handleY > this.yPos + this.height - this.width - 3)
            this.handleY = this.yPos + this.height - this.width - 3;
    }

    public boolean mouseIn(int mouseX, int mouseY) {
        return mouseX > this.xPos && mouseX < this.xPos + this.width && mouseY > this.yPos
                && mouseY < this.yPos + this.height;
    }

    private void renderHandle(GuiVoxelBoxSettingsPanel panel) {
        panel.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.xPos - 2, this.handleY - 1,
                this.xPos + this.width + 2, this.handleY + this.width + 1, 17, 33, 31, 47, 3);
        panel.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.xPos - 2, this.handleY,
                this.xPos + this.width + 2, this.handleY + this.width, 0, 121, 128, 128, 3);
    }

    private void renderBar(GuiVoxelBoxSettingsPanel panel) {
        glEnableDepthTest();

        panel.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, this.xPos, this.yPos,
                this.xPos + this.width, this.yPos + this.height, 0, 16, 16, 32, 4);
        panel.zDrop();
        panel.drawDepthRect(this.xPos + 1, this.yPos + 1, this.xPos + this.width - 1, this.yPos + this.height - 1,
                0x80000000);

        glDisableDepthTest();

    }

    public void render(GuiVoxelBoxSettingsPanel panel, int mouseY) {
        if (Mouse.isButtonDown(0)) {
            if (this.mouseHeld)
                this.moveHandle(mouseY);
        } else
            this.mouseHeld = false;

        this.renderBar(panel);
        this.renderHandle(panel);
    }
}
