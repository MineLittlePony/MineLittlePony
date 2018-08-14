package com.minelittlepony.gui;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

/**
 * Checkbox that supports a gui action when it changes.
 *
 * @author Sollace
 *
 */
public class Checkbox extends GuiCheckbox implements IActionable, IGuiTooltipped {

    private List<String> tooltip = null;

    private final IGuiCallback<Boolean> action;

    public Checkbox(int x, int y, String displayString, boolean value, IGuiCallback<Boolean> callback) {
        super(0, x, y, I18n.format(displayString));
        action = callback;
        checked = value;
    }

    @Override
    public void perform() {
        checked = action.perform(!checked);
    }

    @Override
    public Checkbox setTooltip(List<String> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public void renderToolTip(Minecraft mc, int mouseX, int mouseY) {
        if (visible && isMouseOver() && tooltip != null) {
            mc.currentScreen.drawHoveringText(tooltip, mouseX, mouseY);
        }
    }
}
