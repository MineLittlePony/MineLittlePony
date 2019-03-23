package com.minelittlepony.common.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class Button extends GuiButton implements IGuiTooltipped<Button> {

    private int tipX = 0;
    private int tipY = 0;

    protected IGuiAction<Button> action;

    private List<String> tooltip = null;

    public Button(int x, int y, String label, IGuiAction<? extends Button> callback) {
        this(x, y, 200, 20, label, callback);
    }

    @SuppressWarnings("unchecked")
    public Button(int x, int y, int width, int height, String label, IGuiAction<? extends Button> callback) {
        super(5000, x, y, width, height, GameGui.format(label));
        action = (IGuiAction<Button>)callback;
    }

    public void perform() {
        action.perform(this);
    }

    public Button setEnabled(boolean enable) {
        enabled = enable;
        return this;
    }

    @Override
    public Button setTooltip(List<String> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    protected List<String> getTooltip() {
        return tooltip;
    }

    @Override
    public void renderToolTip(Minecraft mc, int mouseX, int mouseY) {
        List<String> tooltip = getTooltip();

        if (visible && isMouseOver() && tooltip != null) {
            mc.currentScreen.drawHoveringText(tooltip, mouseX + tipX, mouseY + tipY);
        }
    }

    @Override
    public Button setTooltipOffset(int x, int y) {
        tipX = x;
        tipY = y;
        return this;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            perform();

            return true;
        }

        return false;
    }
}
