package com.mumfrey.liteloader.client.gui.modlist;

import static com.mumfrey.liteloader.gl.GL.*;
import static com.mumfrey.liteloader.gl.GLClippingPlanes.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

import com.google.common.base.Strings;
import com.mumfrey.liteloader.client.api.LiteLoaderBrandingProvider;
import com.mumfrey.liteloader.client.gui.GuiSimpleScrollBar;
import com.mumfrey.liteloader.client.util.render.IconAbsolute;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.util.render.IconTextured;

public class GuiModInfoPanel extends Gui
{
    private static final int TITLE_COLOUR       = GuiModListPanel.WHITE;
    private static final int AUTHORS_COLOUR     = GuiModListPanel.WHITE;
    private static final int DIVIDER_COLOUR     = GuiModListPanel.GREY;
    private static final int DESCRIPTION_COLOUR = GuiModListPanel.WHITE;

    private static final IconAbsolute infoIcon = new IconAbsolute(LiteLoaderBrandingProvider.ABOUT_TEXTURE, "Info", 12, 12, 146, 92, 158, 104);

    private final ModListEntry owner;

    private final FontRenderer fontRenderer;

    private final int brandColour;

    private final ModInfo<?> modInfo;

    private GuiSimpleScrollBar scrollBar = new GuiSimpleScrollBar();

    private boolean mouseOverPanel, mouseOverScrollBar;

    private boolean showHelp;

    private String helpTitle, helpText;

    public GuiModInfoPanel(ModListEntry owner, FontRenderer fontRenderer, int brandColour, ModInfo<?> modInfo)
    {
        this.owner = owner;
        this.fontRenderer = fontRenderer;
        this.brandColour = brandColour;
        this.modInfo = modInfo;
    }

    public void draw(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, int height)
    {
        int bottom = height + yPosition;
        int yPos = yPosition + 2;

        this.mouseOverPanel = this.isMouseOver(mouseX, mouseY, xPosition, yPos, width, height);

        this.fontRenderer.drawString(this.owner.getTitleText(), xPosition + 5, yPos, GuiModInfoPanel.TITLE_COLOUR); yPos += 10;
        this.fontRenderer.drawString(this.owner.getVersionText(), xPosition + 5, yPos, GuiModListPanel.VERSION_TEXT_COLOUR); yPos += 10;

        drawRect(xPosition + 5, yPos, xPosition + width, yPos + 1, GuiModInfoPanel.DIVIDER_COLOUR); yPos += 4; // divider

        this.fontRenderer.drawString(I18n.format("gui.about.authors") + ": \2477" + this.modInfo.getAuthor(), xPosition + 5, yPos,
                GuiModInfoPanel.AUTHORS_COLOUR); yPos += 10;
        if (!Strings.isNullOrEmpty(this.modInfo.getURL()))
        {
            this.fontRenderer.drawString(this.modInfo.getURL(), xPosition + 5, yPos, GuiModListPanel.BLEND_2THRDS & this.brandColour); yPos += 10;
        }

        drawRect(xPosition + 5, yPos, xPosition + width, yPos + 1, GuiModInfoPanel.DIVIDER_COLOUR); yPos += 4; // divider
        drawRect(xPosition + 5, bottom - 1, xPosition + width, bottom, GuiModInfoPanel.DIVIDER_COLOUR); // divider

        glEnableClipping(-1, -1, yPos, bottom - 3);

        int scrollHeight = bottom - yPos - 3;
        int contentHeight = this.drawContent(xPosition, width, yPos);

        this.scrollBar.setMaxValue(contentHeight - scrollHeight);
        this.scrollBar.drawScrollBar(mouseX, mouseY, partialTicks, xPosition + width - 5, yPos, 5, scrollHeight, contentHeight);

        this.mouseOverScrollBar = this.isMouseOver(mouseX, mouseY, xPosition + width - 5, yPos, 5, scrollHeight);
    }

    private int drawContent(int xPosition, int width, int yPos)
    {
        yPos -= this.scrollBar.getValue();

        if (this.showHelp)
        {
            this.drawIcon(xPosition + 3, yPos, GuiModInfoPanel.infoIcon); yPos += 2;
            this.fontRenderer.drawString(this.helpTitle, xPosition + 17, yPos, this.brandColour); yPos += 12;
            return this.drawText(xPosition + 17, width - 24, yPos, this.helpText, GuiModInfoPanel.DESCRIPTION_COLOUR) + 15;
        }

        return this.drawText(xPosition + 5, width - 11, yPos, this.modInfo.getDescription(), GuiModInfoPanel.DESCRIPTION_COLOUR);
    }

    protected void drawIcon(int xPosition, int yPosition, IconTextured icon)
    {
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(icon.getTextureResource());

        glEnableBlend();
        this.drawTexturedModalRect(xPosition, yPosition, icon.getUPos(), icon.getVPos(), icon.getIconWidth(), icon.getIconHeight());
        glDisableBlend();
    }

    private int drawText(int xPosition, int width, int yPos, String text, int colour)
    {
        int totalHeight = this.fontRenderer.splitStringWidth(text, width);
        this.fontRenderer.drawSplitString(text, xPosition, yPos, width, colour);
        return totalHeight;
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public void mousePressed()
    {
        if (this.mouseOverScrollBar)
        {
            this.scrollBar.setDragging(true);
        }
    }

    public void mouseReleased()
    {
        this.scrollBar.setDragging(false);
    }

    public boolean mouseWheelScrolled(int mouseWheelDelta)
    {
        if (this.mouseOverPanel)
        {
            this.scrollBar.offsetValue(-mouseWheelDelta / 8);
            return true;
        }

        return false;
    }

    public void displayHelpMessage(String title, String text)
    {
        this.showHelp = true;
        this.helpTitle = I18n.format(title);
        this.helpText = I18n.format(text);
        this.scrollBar.setValue(0);
    }

    public void clearHelpMessage()
    {
        this.showHelp = false;
    }
}
