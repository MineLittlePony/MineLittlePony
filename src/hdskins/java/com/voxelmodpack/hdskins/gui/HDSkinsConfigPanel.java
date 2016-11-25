package com.voxelmodpack.hdskins.gui;

import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class HDSkinsConfigPanel implements ConfigPanel {

    private GuiButton button;

    @Override
    public void onPanelShown(ConfigPanelHost host) {
        this.button = new GuiButton(0, 40, 10, 100, 20, "Clear Skin Cache");

    }

    @Override
    public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks) {
        this.button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    @Override
    public void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
        if (button.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            HDSkinManager.clearSkinCache();
        }
    }

    @Override
    public String getPanelTitle() {
        return "HD Skins Settings";
    }

    @Override
    public int getContentHeight() {
        return 0;
    }

    @Override
    public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode) {
    }

    @Override
    public void mouseMoved(ConfigPanelHost host, int mouseX, int mouseY) {
    }

    @Override
    public void mouseReleased(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void onPanelHidden() {
    }

    @Override
    public void onPanelResize(ConfigPanelHost host) {
    }

    @Override
    public void onTick(ConfigPanelHost host) {
    }
}
