package com.mumfrey.liteloader.client.gui;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.api.BrandingProvider;
import com.mumfrey.liteloader.api.LiteAPI;
import com.mumfrey.liteloader.client.api.LiteLoaderBrandingProvider;
import com.mumfrey.liteloader.client.util.render.IconAbsolute;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.SortableValue;
import com.mumfrey.liteloader.util.render.Icon;

/**
 * "About LiteLoader" panel which docks in the mod info screen and lists
 * information about the installed APIs.
 *
 * @author Adam Mummery-Smith
 */
class GuiPanelAbout extends GuiPanel implements ScrollPanelContent
{
    public static final IconAbsolute apiIconCoords = new IconAbsolute(LiteLoaderBrandingProvider.ABOUT_TEXTURE, "api_icon",
            32, 32, 192, 144, 256, 208);

    private static final int ROW_HEIGHT = 40;

    private static final URI MCP_URI = URI.create("http://mcp.ocean-labs.de/");

    private GuiLiteLoaderPanel parent;

    private GuiScrollPanel scrollPane;

    private List<BrandingProvider> brandings = new ArrayList<BrandingProvider>();

    private boolean mouseOverLogo;

    public GuiPanelAbout(Minecraft minecraft, GuiLiteLoaderPanel parent)
    {
        super(minecraft);
        this.parent = parent;
        this.scrollPane = new GuiScrollPanel(minecraft, this, MARGIN, 90, 100, 100);

        this.sortBrandingProviders();

        this.scrollPane.addControl(new GuiHoverLabel(-2, 38, 22 + this.brandings.size() * GuiPanelAbout.ROW_HEIGHT, this.mc.fontRendererObj,
                "\247n" + MCP_URI.toString(), this.parent.getBrandColour()));
    }

    /**
     * 
     */
    private void sortBrandingProviders()
    {
        Set<SortableValue<BrandingProvider>> sortedBrandingProviders = new TreeSet<SortableValue<BrandingProvider>>();

        for (LiteAPI api : LiteLoader.getAPIs())
        {
            BrandingProvider brandingProvider = LiteLoader.getCustomisationProvider(api, BrandingProvider.class);
            if (brandingProvider != null)
            {
                sortedBrandingProviders.add(new SortableValue<BrandingProvider>(Integer.MAX_VALUE - brandingProvider.getPriority(), 0,
                        brandingProvider));
            }
        }

        int brandingIndex = 0;

        for (SortableValue<BrandingProvider> sortedBrandingProvider : sortedBrandingProviders)
        {
            BrandingProvider brandingProvider = sortedBrandingProvider.getValue();

            this.brandings.add(brandingProvider);
            URI homepage = brandingProvider.getHomepage();
            if (homepage != null)
            {
                this.scrollPane.addControl(new GuiHoverLabel(brandingIndex, 38, 22 + brandingIndex * GuiPanelAbout.ROW_HEIGHT,
                        this.mc.fontRendererObj, "\247n" + homepage, this.parent.getBrandColour()));
            }

            brandingIndex++;
        }
    }

    @Override
    void setSize(int width, int height)
    {
        super.setSize(width, height);

        this.scrollPane.setSizeAndPosition(MARGIN, 86, this.width - MARGIN * 2, this.height - 126);
        this.controls.add(new GuiButton(-1, this.width - 99 - MARGIN, this.height - BOTTOM + 9, 100, 20, I18n.format("gui.done")));
        this.controls.add(new GuiButton(-3, MARGIN, this.height - BOTTOM + 9, 100, 20, I18n.format("gui.log.button")));
    }

    @Override
    void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.mouseOverLogo = this.parent.drawInfoPanel(mouseX, mouseY, partialTicks, 0, 38);

        this.scrollPane.draw(mouseX, mouseY, partialTicks);

        super.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    public int getScrollPanelContentHeight(GuiScrollPanel source)
    {
        return 64 + this.brandings.size() * GuiPanelAbout.ROW_HEIGHT;
    }

    @Override
    public void drawScrollPanelContent(GuiScrollPanel source, int mouseX, int mouseY, float partialTicks, int scrollAmount, int visibleHeight)
    {
        FontRenderer fontRenderer = this.mc.fontRendererObj;
        int textColour = 0xFFAAAAAA;

        int yPos = 0;

        for (BrandingProvider branding : this.brandings)
        {
            ResourceLocation twitterAvatarResource = branding.getTwitterAvatarResource();
            Icon twitterAvatarCoords = branding.getTwitterAvatarCoords();

            this.mc.getTextureManager().bindTexture(twitterAvatarResource != null ? twitterAvatarResource : LiteLoaderBrandingProvider.ABOUT_TEXTURE);
            GuiLiteLoaderPanel.glDrawTexturedRect(0, yPos, twitterAvatarCoords != null ? twitterAvatarCoords : GuiPanelAbout.apiIconCoords, 1.0F);

            fontRenderer.drawString(branding.getDisplayName(), 38, yPos, 0xFFFFFFFF);
            fontRenderer.drawString(branding.getCopyrightText(), 38, yPos + 11, textColour);

            yPos += GuiPanelAbout.ROW_HEIGHT;
        }

        fontRenderer.drawString("Created using Mod Coder Pack", 38, yPos, 0xFFFFFFFF);
        fontRenderer.drawString("MCP is (c) Copyright by the MCP Team", 38, yPos + 11, textColour);

        yPos += GuiPanelAbout.ROW_HEIGHT;

        fontRenderer.drawString("Minecraft is Copyright (c) Mojang AB", 38, yPos, textColour);
        fontRenderer.drawString("All rights reserved.", 38, yPos + 11, textColour);
    }

    @Override
    public void scrollPanelMousePressed(GuiScrollPanel source, int mouseX, int mouseY, int mouseButton)
    {
        int index = mouseY / GuiPanelAbout.ROW_HEIGHT;
        int yOffset = mouseY - (GuiPanelAbout.ROW_HEIGHT * index);

        if (mouseButton == 0 && mouseX < 33 && index >= 0 && index < this.brandings.size() && yOffset < 33)
        {
            String twitterUserName = this.brandings.get(index).getTwitterUserName();
            if (twitterUserName != null)
            {
                URI twitterURI = URI.create("https://www.twitter.com/" + twitterUserName);
                this.openURI(twitterURI);
            }
        }
    }

    /**
     * @param control
     */
    @Override
    void actionPerformed(GuiButton control)
    {
        if (control.id == -1) this.close();
        if (control.id == -2) this.openURI(MCP_URI);
        if (control.id == -3) this.parent.showLogPanel();
    }

    @Override
    public void scrollPanelActionPerformed(GuiScrollPanel source, GuiButton control)
    {
        if (control.id >= 0 && control.id < this.brandings.size())
        {
            URI homepage = this.brandings.get(control.id).getHomepage();
            if (homepage != null) this.openURI(homepage);
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
    void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollPane.mousePressed(mouseX, mouseY, mouseButton);

        if (mouseButton == 0 && this.mouseOverLogo)
        {
            this.close();
        }

        super.mousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    void mouseMoved(int mouseX, int mouseY)
    {
    }

    @Override
    void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollPane.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    void mouseWheelScrolled(int mouseWheelDelta)
    {
        this.scrollPane.mouseWheelScrolled(mouseWheelDelta);
    }
}
