package com.mumfrey.liteloader.client.gui;

import com.mumfrey.liteloader.client.api.LiteLoaderBrandingProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiHoverLabel extends GuiButton
{
    private FontRenderer fontRenderer;
    private int colour;
    private int hoverColour;

    public GuiHoverLabel(int id, int xPosition, int yPosition, FontRenderer fontRenderer, String displayText)
    {
        this(id, xPosition, yPosition, fontRenderer, displayText, LiteLoaderBrandingProvider.BRANDING_COLOUR);
    }

    public GuiHoverLabel(int id, int xPosition, int yPosition, FontRenderer fontRenderer, String displayText, int colour)
    {
        this(id, xPosition, yPosition, fontRenderer, displayText, colour, 0xFFFFFFAA);
    }

    public GuiHoverLabel(int id, int xPosition, int yPosition, FontRenderer fontRenderer, String displayText, int colour, int hoverColour)
    {
        super(id, xPosition, yPosition, GuiHoverLabel.getStringWidth(fontRenderer, displayText), 8, displayText);

        this.fontRenderer = fontRenderer;
        this.colour = colour;
        this.hoverColour = hoverColour;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.xPosition
                    && mouseY >= this.yPosition
                    && mouseX < this.xPosition + this.width
                    && mouseY < this.yPosition + this.height;
            this.fontRenderer.drawString(this.displayString, this.xPosition, this.yPosition, this.hovered ? this.hoverColour : this.colour);
        }
        else
        {
            this.hovered = false;
        }
    }

    private static int getStringWidth(FontRenderer fontRenderer, String text)
    {
        return fontRenderer.getStringWidth(text);
    }
}
