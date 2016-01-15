package com.mumfrey.liteloader.client.gui;

import java.net.URI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.core.LiteLoaderUpdateSite;
import com.mumfrey.liteloader.launch.ClassPathUtilities;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.update.UpdateSite;

/**
 * "Check for updates" panel which docks in the mod info screen
 *
 * @author Adam Mummery-Smith
 */
class GuiPanelUpdateCheck extends GuiPanel
{
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * URI to open if a new version is available
     */
    private static final URI DOWNLOAD_URI = URI.create("http://dl.liteloader.com");

    private final GuiLiteLoaderPanel parentScreen;

    /**
     * Update site to contact
     */
    private final UpdateSite updateSite;

    /**
     * Panel title
     */
    private final String panelTitle;

    /**
     * Buttons
     */
    private GuiButton btnCheck, btnDownload;

    /**
     * Throbber frame
     */
    private int throb;

    private boolean canForceUpdate, updateForced;

    public GuiPanelUpdateCheck(GuiLiteLoaderPanel parentScreen, Minecraft minecraft, UpdateSite updateSite, String updateName,
            LoaderProperties properties)
    {
        super(minecraft);

        this.parentScreen = parentScreen;
        this.updateSite = updateSite;
        this.panelTitle = I18n.format("gui.updates.title", updateName);

        this.canForceUpdate = (updateSite instanceof LiteLoaderUpdateSite && ((LiteLoaderUpdateSite)updateSite).canForceUpdate(properties));
    }

    @Override
    void setSize(int width, int height)
    {
        super.setSize(width, height);

        this.controls.add(new GuiButton(0, this.width - 99 - MARGIN, this.height - BOTTOM + 9, 100, 20,
                this.updateForced ? I18n.format("gui.exitgame") : I18n.format("gui.done")));
        this.controls.add(this.btnCheck = new GuiButton(1, MARGIN + 16, TOP + 16, 100, 20,
                I18n.format("gui.checknow")));
        this.controls.add(this.btnDownload = new GuiButton(2, MARGIN + 16, TOP + 118, 100, 20,
                this.canForceUpdate ? I18n.format("gui.forceupdate") : I18n.format("gui.downloadupdate")));
    }

    @Override
    void draw(int mouseX, int mouseY, float partialTicks)
    {
        FontRenderer fontRenderer = this.mc.fontRendererObj;

        // Draw panel title
        fontRenderer.drawString(this.panelTitle, MARGIN, TOP - 14, GuiPanelUpdateCheck.WHITE);

        // Draw top and bottom horizontal bars
        drawRect(MARGIN, TOP - 4, this.width - MARGIN, TOP - 3, 0xFF999999);
        drawRect(MARGIN, this.height - BOTTOM + 2, this.width - MARGIN, this.height - BOTTOM + 3, 0xFF999999);

        this.btnCheck.enabled = !this.updateForced && !this.updateSite.isCheckInProgress();
        this.btnDownload.visible = false;

        if (this.updateSite.isCheckInProgress())
        {
            this.drawThrobber(MARGIN, TOP + 40, this.throb);
            fontRenderer.drawString(I18n.format("gui.updates.status.checking", ""), MARGIN + 18, TOP + 44, GuiPanelUpdateCheck.WHITE);
        }
        else if (this.updateSite.isCheckComplete())
        {
            boolean success = this.updateSite.isCheckSucceess();
            String status = success ? I18n.format("gui.updates.status.success") : I18n.format("gui.updates.status.failed");
            fontRenderer.drawString(I18n.format("gui.updates.status.checking", status), MARGIN + 18, TOP + 44, GuiPanelUpdateCheck.WHITE);

            if (success)
            {
                fontRenderer.drawString(I18n.format("gui.updates.available.title"), MARGIN + 18, TOP + 70, GuiPanelUpdateCheck.WHITE);
                if (this.updateSite.isUpdateAvailable())
                {
                    this.btnDownload.visible = !this.updateForced;
                    fontRenderer.drawString(I18n.format("gui.updates.available.newversion"), MARGIN + 18, TOP + 84, GuiPanelUpdateCheck.WHITE);
                    fontRenderer.drawString(I18n.format("gui.updates.available.version", this.updateSite.getAvailableVersion()),
                            MARGIN + 18, TOP + 94, GuiPanelUpdateCheck.WHITE);
                    fontRenderer.drawString(I18n.format("gui.updates.available.date", this.updateSite.getAvailableVersionDate()),
                            MARGIN + 18, TOP + 104, GuiPanelUpdateCheck.WHITE);

                    if (this.updateForced)
                    {
                        fontRenderer.drawString(I18n.format("gui.updates.forced"), MARGIN + 18, TOP + 144, 0xFFFFAA00);
                    }
                }
                else
                {
                    fontRenderer.drawString(I18n.format("gui.updates.available.nonewversion"), MARGIN + 18, TOP + 84, GuiPanelUpdateCheck.WHITE);
                }
            }
        }
        else
        {
            fontRenderer.drawString(I18n.format("gui.updates.status.idle"), MARGIN + 18, TOP + 44, GuiPanelUpdateCheck.WHITE);
        }

        super.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    public void close()
    {
        if (this.updateForced)
        {
            return;
        }

        super.close();
    }

    /**
     * @param control
     */
    @Override
    void actionPerformed(GuiButton control)
    {
        if (control.id == 0)
        {
            if (this.updateForced)
            {
                ClassPathUtilities.terminateRuntime(0);
                return;
            }

            this.close();
        }
        if (control.id == 1) this.updateSite.beginUpdateCheck();
        if (control.id == 2)
        {
            if (this.canForceUpdate && ((LiteLoaderUpdateSite)this.updateSite).forceUpdate())
            {
                this.updateForced = true;
                this.parentScreen.setToggleable(false);
                ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
                this.parentScreen.setWorldAndResolution(this.mc, sr.getScaledWidth(), sr.getScaledHeight());
            }
            else
            {
                this.openURI(GuiPanelUpdateCheck.DOWNLOAD_URI);
            }

            this.btnDownload.enabled = false;
        }
    }

    private void openURI(URI uri)
    {
        try
        {
            Class<?> desktop = Class.forName("java.awt.Desktop");
            Object instance = desktop.getMethod("getDesktop").invoke(null);
            desktop.getMethod("browse", URI.class).invoke(instance, uri);
        }
        catch (Throwable th) {}
    }

    @Override
    void onTick()
    {
        this.throb++;
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
    void keyPressed(char keyChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE) this.close();
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
