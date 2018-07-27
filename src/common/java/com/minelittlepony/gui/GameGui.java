package com.minelittlepony.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public abstract class GameGui extends GuiScreen {

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof IActionable) {
            ((IActionable)button).perform();
        }
    }

    protected static String translate(String string) {
        return I18n.format(string);
    }
}
