package com.minelittlepony.gui;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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

    /**
     * Formats a translation string and returns it in a list wrapped to a given width.
     * This can be safely used in initGui, where the fontRenderer is often still null.
     */
    protected List<String> formatMultiLine(String string, int width, Object...pars) {
        FontRenderer fr = fontRenderer;

        if (fr == null) {
            fr = Minecraft.getMinecraft().fontRenderer;
        }

        return fr.listFormattedStringToWidth(format(string, pars), width);
    }

    /**
     * Converts a given string to title case regardless of initial case.
     */
    protected static String toTitleCase(String string) {
        return WordUtils.capitalize(string.toLowerCase());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawContents(mouseX, mouseY, partialTicks);
        postDrawContents(mouseX, mouseY, partialTicks);
    }

    protected void drawContents(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void postDrawContents(int mouseX, int mouseY, float partialTicks) {
        buttonList.forEach(button -> {
            if (button instanceof IGuiTooltipped) {
                ((IGuiTooltipped)button).renderToolTip(mc, mouseX, mouseY);
            }
        });
    }
}
