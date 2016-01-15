package com.mumfrey.liteloader.client.gui;

import static com.mumfrey.liteloader.gl.GL.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import com.mumfrey.liteloader.client.api.LiteLoaderBrandingProvider;

/**
 * Super-simple implementation of a checkbox control
 * 
 * @author Adam Mummery-Smith
 */
public class GuiCheckbox extends GuiButton
{
    public boolean checked;

    public GuiCheckbox(int controlId, int xPosition, int yPosition, String displayString)
    {
        super(controlId, xPosition, yPosition, Minecraft.getMinecraft().fontRendererObj.getStringWidth(displayString) + 16, 12, displayString);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            minecraft.getTextureManager().bindTexture(LiteLoaderBrandingProvider.ABOUT_TEXTURE);
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition
                    && mouseY >= this.yPosition
                    && mouseX < this.xPosition + this.width
                    && mouseY < this.yPosition + this.height;

            this.drawTexturedModalRect(this.xPosition, this.yPosition, this.checked ? 134 : 122, 80, 12, 12);
            this.mouseDragged(minecraft, mouseX, mouseY);

            int colour = 0xE0E0E0;
            if (!this.enabled)
            {
                colour = 0xA0A0A0;
            }
            else if (this.hovered)
            {
                colour = 0xFFFFA0;
            }

            this.drawString(minecraft.fontRendererObj, this.displayString, this.xPosition + 16, this.yPosition + 2, colour);
        }
    }
}
