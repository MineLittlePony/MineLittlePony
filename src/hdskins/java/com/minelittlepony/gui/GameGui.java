package com.minelittlepony.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public abstract class GameGui extends GuiScreen {

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof IActionable) {
            ((IActionable)button).perform();
        }
    }

    protected static String format(String string) {
        return I18n.format(string);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawContents(mouseX, mouseY, partialTicks);

        buttonList.forEach(button -> {
            if (button instanceof IGuiTooltipped) {
                ((IGuiTooltipped)button).renderToolTip(mc, mouseX, mouseY);
            }
        });
    }

    protected void drawContents(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
