package com.minelittlepony.gui;

import org.apache.commons.lang3.text.WordUtils;

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

    protected static String format(String string, Object... pars) {
        return I18n.format(string, pars);
    }

    protected static String toTitleCase(String string) {
        return WordUtils.capitalize(string.toLowerCase());
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
