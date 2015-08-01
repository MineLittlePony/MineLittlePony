package com.voxelmodpack.common.properties.gui;

import net.minecraft.client.Minecraft;

import com.voxelmodpack.common.LiteModVoxelCommon;

/**
 * @author anangrybeaver
 */
public class SettingsPanelMenuTab implements Comparable<SettingsPanelMenuTab> {
    private Minecraft mc;

    private final int priority;
    private final int width;

    private int xPos;
    private int yPos;

    private final String label;

    private boolean active = false;

    public SettingsPanelMenuTab(String label, int xPosition, int priority) {
        this.mc = Minecraft.getMinecraft();

        this.label = label;
        this.priority = priority;
        this.width = this.mc.fontRendererObj.getStringWidth(label);
        this.xPos = xPosition - this.width - 2;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos - this.width - 3;
    }

    public boolean isMouseOver(int tabMenuWidth, int mouseX, int mouseY) {
        int newX = GuiVoxelBoxSettingsPanel.PANEL_LEFT - tabMenuWidth;

        return mouseX > newX && mouseX < newX + tabMenuWidth && mouseY > this.yPos - 4 && mouseY < this.yPos + 12;
    }

    public String getLabel() {
        return this.label;
    }

    public void renderTab(GuiVoxelBoxSettingsPanel panel, int tabMenuWidth, int mouseX, int mouseY, int y,
            boolean mask) {
        this.yPos = y;

        int tabX = GuiVoxelBoxSettingsPanel.PANEL_LEFT - tabMenuWidth;
        int tabY = this.yPos - 4;
        int tabRight = GuiVoxelBoxSettingsPanel.PANEL_LEFT + 4;
        int tabBottom = this.yPos + 12;

        if (mask) {
            // Draw the tab mask over the window border so it looks like this
            // tab "flows" into the window
            if (this.active) {
                this.renderTabMask(panel, tabY, tabRight, tabBottom);
            }
        } else {
            // Draw the actual tab
            boolean mouseOver = this.isMouseOver(tabMenuWidth, mouseX, mouseY);
            int v = mouseOver ? 32 : (this.active ? 16 : 0);
            panel.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, tabX, tabY, tabRight, tabBottom, 0,
                    0 + v, 16, 16 + v, 4);
            panel.zDrop();
            panel.drawDepthRect(tabX + 1, tabY + 1, tabRight - 1, tabBottom - 1, 0x80000000);

            this.mc.fontRendererObj.drawString(this.label, this.xPos, y,
                    this.isMouseOver(tabMenuWidth, mouseX, mouseY) ? 0x55FFFF : (this.active ? 0xFFFF55 : 0xAAAAAA));
        }
    }

    private void renderTabMask(GuiVoxelBoxSettingsPanel panel, int tabY, int tabRight, int tabBottom) {
        panel.drawDepthRect(tabRight - 4, tabY + 1, tabRight - 3, tabBottom - 1, 0x80000000);
        panel.drawDepthRect(tabRight - 3, tabY, tabRight - 2, tabBottom, 0x80000000);
    }

    @Override
    public int compareTo(SettingsPanelMenuTab other) {
        if (other == null)
            return 0;
        return (this.priority == other.priority) ? -1 : this.priority - other.priority;
    }
}
