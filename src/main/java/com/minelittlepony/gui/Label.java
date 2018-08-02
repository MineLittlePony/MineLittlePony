package com.minelittlepony.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class Label extends GuiButton {

    private boolean center;

    private int color;

    private String text;

    public Label(int x, int y, String translationString, int color) {
        this(x, y, translationString, color, false);
    }

    public Label(int x, int y, String translationString, int color, boolean center) {
        super(0, x, y, "");
        this.color = color;
        this.center = center;
        text = translationString;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (center) {
            drawCenteredString(mc.fontRenderer, I18n.format(text), x, y, color);
        } else {
            drawString(mc.fontRenderer, I18n.format(text), x, y, color);
        }
    }
}
