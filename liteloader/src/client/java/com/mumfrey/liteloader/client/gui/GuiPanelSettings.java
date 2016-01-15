package com.mumfrey.liteloader.client.gui;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.interfaces.PanelManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

class GuiPanelSettings extends GuiPanel
{
    private GuiLiteLoaderPanel parentScreen;

    private GuiCheckbox chkShowTab, chkNoHide, chkForceUpdate;

    private boolean hide;

    private String[] helpText = new String[5];

    GuiPanelSettings(GuiLiteLoaderPanel parentScreen, Minecraft minecraft)
    {
        super(minecraft);

        this.parentScreen = parentScreen;

        this.helpText[0] = I18n.format("gui.settings.showtab.help1");
        this.helpText[1] = I18n.format("gui.settings.showtab.help2");
        this.helpText[2] = I18n.format("gui.settings.notabhide.help1");
        this.helpText[3] = I18n.format("gui.settings.forceupdate.help1");
        this.helpText[4] = I18n.format("gui.settings.forceupdate.help2");
    }

    @Override
    public void close()
    {
        this.hide = true;
    }

    @Override
    boolean isCloseRequested()
    {
        boolean hide = this.hide;
        this.hide = false;
        return hide;
    }

    @Override
    void setSize(int width, int height)
    {
        super.setSize(width, height);

        this.controls.add(new GuiButton(-1, this.width - 99 - MARGIN, this.height - BOTTOM + 9, 100, 20, I18n.format("gui.done")));
        this.controls.add(this.chkShowTab = new GuiCheckbox(0, 34, 90, I18n.format("gui.settings.showtab.label")));
        this.controls.add(this.chkNoHide = new GuiCheckbox(1, 34, 128, I18n.format("gui.settings.notabhide.label")));
        this.controls.add(this.chkForceUpdate = new GuiCheckbox(2, 34, 158, I18n.format("gui.settings.forceupdate.label")));

        this.updateCheckBoxes();
    }

    private void updateCheckBoxes()
    {
        PanelManager<?> panelManager = LiteLoader.getModPanelManager();

        this.chkShowTab.checked = panelManager.isTabVisible();
        this.chkNoHide.checked = panelManager.isTabAlwaysExpanded();
        this.chkForceUpdate.checked = panelManager.isForceUpdateEnabled();
    }

    private void updateSettings()
    {
        PanelManager<?> panelManager = LiteLoader.getModPanelManager();

        panelManager.setTabVisible(this.chkShowTab.checked);
        panelManager.setTabAlwaysExpanded(this.chkNoHide.checked);
        panelManager.setForceUpdateEnabled(this.chkForceUpdate.checked);
    }

    @Override
    void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.parentScreen.drawInfoPanel(mouseX, mouseY, partialTicks, 0, 38);

        FontRenderer fontRenderer = this.mc.fontRendererObj;
        int brandColour = this.parentScreen.getBrandColour();

        fontRenderer.drawString(this.helpText[0], 50, 104, brandColour);
        fontRenderer.drawString(this.helpText[1], 50, 114, brandColour);
        fontRenderer.drawString(this.helpText[2], 50, 142, brandColour);
        fontRenderer.drawString(this.helpText[3], 50, 172, brandColour);
        fontRenderer.drawString(this.helpText[4], 50, 182, brandColour);

        super.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    void actionPerformed(GuiButton control)
    {
        if (control.id == -1)
        {
            this.close();
            return;
        }

        if (control instanceof GuiCheckbox)
        {
            ((GuiCheckbox)control).checked = !((GuiCheckbox)control).checked;
            this.updateSettings();
        }
    }

    @Override
    void keyPressed(char keyChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE)
        {
            this.close();
        }
    }

    @Override
    void onTick()
    {
    }

    @Override
    void onHidden()
    {
    }

    @Override
    void onShown()
    {
    }

    @Override
    void mouseMoved(int mouseX, int mouseY)
    {
    }

    @Override
    void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
    }

    @Override
    void mouseWheelScrolled(int mouseWheelDelta)
    {
    }
}
