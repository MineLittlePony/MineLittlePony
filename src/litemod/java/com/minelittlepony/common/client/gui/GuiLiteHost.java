package com.minelittlepony.common.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import com.mumfrey.liteloader.gl.GLClippingPlanes;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

import java.io.IOException;

public class GuiLiteHost extends GuiHost implements ConfigPanel {

    private boolean isInPanel = false;

    private int contentHeight;

    public GuiLiteHost(IGuiGuest guest) {
        super(guest);
    }

    @Override
    public String getPanelTitle() {
        return format(getTitle());
    }

    @Override
    public int getContentHeight() {
        return contentHeight + 40;
    }

    @Override
    public <T extends GuiButton> T addButton(T button) {
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
        isInPanel = true;
    }

    @Override
    public void onPanelResize(ConfigPanelHost host) {
        width = host.getWidth();
        buttonList.clear();
        initGui();
    }

    @Override
    public void onPanelHidden() {
        isInPanel = false;
        onGuiClosed();
    }

    @Override
    public void onTick(ConfigPanelHost host) {
        updateScreen();
    }

    @Override
    public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks) {
        drawContents(mouseX, mouseY, partialTicks);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GLClippingPlanes.glDisableClipping();

        postDrawContents(mouseX, mouseY, partialTicks);

        GL11.glPopAttrib();
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
        if (!isInPanel) {
            super.drawWorldBackground(tint);
        }
    }

    @Override
    public boolean mustScroll() {
        return isInPanel;
    }
}
