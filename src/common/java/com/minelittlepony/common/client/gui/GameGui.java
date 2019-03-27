package com.minelittlepony.common.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundEvent;

public abstract class GameGui extends GuiScreen {

    public static String format(String string, Object... pars) {
        return string == null ? null : I18n.format(string, pars);
    }

    /**
     * Formats a translation string and returns it in a list wrapped to a given width.
     * This can be safely used in initGui, where the fontRenderer is often still null.
     */
    public List<String> formatMultiLine(String string, int width, Object...pars) {
        FontRenderer fr = fontRenderer;

        if (fr == null) {
            fr = Minecraft.getInstance().fontRenderer;
        }

        return fr.listFormattedStringToWidth(format(string, pars), width);
    }

    protected void playSound(SoundEvent event) {
        mc.getSoundHandler().play(SimpleSound.master(event, 1));
    }

    /**
     * Converts a given string to title case regardless of initial case.
     */
    @SuppressWarnings("deprecation")
    protected static String toTitleCase(String string) {
        return org.apache.commons.lang3.text.WordUtils.capitalize(string.toLowerCase());
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawContents(mouseX, mouseY, partialTicks);
        postDrawContents(mouseX, mouseY, partialTicks);
    }

    protected void drawContents(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
    }

    protected void postDrawContents(int mouseX, int mouseY, float partialTicks) {
        buttons.forEach(button -> {
            if (button instanceof IGuiTooltipped) {
                ((IGuiTooltipped<?>)button).renderToolTip(mc, mouseX, mouseY);
            }
        });
    }
}
