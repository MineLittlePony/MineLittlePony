package com.minelittlepony.gui;

import java.io.IOException;

import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

/**
 * Boilerplate because LiteLoader has to be such a 'special flower' -_-
 */
public class PonySettingsPanel extends GuiPonySettings implements ConfigPanel {

    private int contentHeight;

    @Override
    public String getPanelTitle() {
        return I18n.format(getTitle());
    }

    @Override
    public int getContentHeight() {
        return contentHeight + 40;
    }

    @Override
    protected <T extends GuiButton> T addButton(T button) {
        if (button.y > contentHeight) {
            contentHeight = button.y;
        }
        return super.addButton(button);
    }

    @Override
    public void onPanelShown(ConfigPanelHost host) {
        mc = Minecraft.getMinecraft();
        width = host.getWidth();
        buttonList.clear();
        initGui();
    }

    @Override
    public void onPanelResize(ConfigPanelHost host) {
        width = host.getWidth();
        buttonList.clear();
        initGui();
    }

    @Override
    public void onPanelHidden() {
        onGuiClosed();
    }

    @Override
    public void onTick(ConfigPanelHost host) {
        updateScreen();
    }

    @Override
    public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks) {
        drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
        try {
            mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
        mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseMoved(ConfigPanelHost host, int mouseX, int mouseY) {

    }

    @Override
    public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode) {
        try {
            keyTyped(keyChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawWorldBackground(int tint) {

    }

    protected boolean mustScroll() {
        return true;
    }

}
