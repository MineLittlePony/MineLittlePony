package com.minelittlepony.common.gui;

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
public class Checkbox extends GuiCheckbox implements IGuiTooltipped<Checkbox> {

    private int tipX = 0;
    private int tipY = 0;

    private List<String> tooltip = null;

    private final IGuiCallback<Boolean> action;

    public Checkbox(int x, int y, String displayString, boolean value, IGuiCallback<Boolean> callback) {
        super(0, x, y, I18n.format(displayString));
        action = callback;
        checked = value;
    }

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
            mc.currentScreen.drawHoveringText(tooltip, mouseX + tipX, mouseY + tipY);
        }
    }

    @Override
    public Checkbox setTooltipOffset(int x, int y) {
        tipX = x;
        tipY = y;
        return this;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            perform();

            return false;
        }

        return false;
    }
}
