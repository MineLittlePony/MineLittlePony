package com.mumfrey.liteloader.client.gui.modlist;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;

import com.mumfrey.liteloader.api.ModInfoDecorator;
import com.mumfrey.liteloader.core.ModInfo;

public class GuiModListPanelInvalid extends GuiModListPanel
{
    private static final int BAD_PANEL_HEIGHT = 22;

    public GuiModListPanelInvalid(ModListEntry owner, FontRenderer fontRenderer, int brandColour, ModInfo<?> modInfo,
            List<ModInfoDecorator> decorators)
    {
        super(owner, fontRenderer, brandColour, modInfo, decorators);
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, boolean selected)
    {
        int gradientColour = selected ? ERROR_GRADIENT_COLOUR : ERROR_GRADIENT_COLOUR2;

        this.drawGradientRect(xPosition, yPosition, xPosition + width, yPosition + 22, gradientColour, GuiModListPanel.GRADIENT_COLOUR2);

        String titleText = this.owner.getTitleText();
        String reasonText = this.modInfo.getDescription();

        this.fontRenderer.drawString(titleText,   xPosition + 5, yPosition + 2,  0xFF8888);
        this.fontRenderer.drawString(reasonText, xPosition + 5, yPosition + 12, GuiModListPanel.ERROR_GRADIENT_COLOUR);

        this.updateMouseOver(mouseX, mouseY, xPosition, yPosition, width); 
        drawRect(xPosition, yPosition, xPosition + 1, yPosition + 22, ERROR_COLOUR);
    }

    @Override
    protected void postRender(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, boolean selected)
    {
    }

    @Override
    public int getHeight()
    {
        return GuiModListPanelInvalid.BAD_PANEL_HEIGHT;
    }

    @Override
    public int getTotalHeight()
    {
        return GuiModListPanelInvalid.BAD_PANEL_HEIGHT + GuiModListPanel.PANEL_SPACING;
    }
}
