package com.minelittlepony.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * A simple label for drawing text to a gui screen.
 *
 * @author Sollace
 *
 */
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
        this.text = translationString;
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return false;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (center) {
            drawCenteredString(mc.fontRenderer, GameGui.format(text), x, y, color);
        } else {
            drawString(mc.fontRenderer, GameGui.format(text), x, y, color);
        }
    }
}
