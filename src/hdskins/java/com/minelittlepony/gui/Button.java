package com.minelittlepony.gui;

import java.util.List;

import com.google.common.base.Splitter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class Button extends GuiButton implements IActionable, IGuiTooltipped {

    protected IGuiAction<Button> action;

    private List<String> tooltip = null;

    @SuppressWarnings("unchecked")
    public Button(int x, int y, int width, int height, String label, IGuiAction<? extends Button> callback) {
        super(5000, x, y, width, height, GameGui.format(label));
        action = (IGuiAction<Button>)callback;
    }

    @Override
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

    @Override
    public void renderToolTip(Minecraft mc, int mouseX, int mouseY) {
        if (visible && isMouseOver() && tooltip != null) {
            mc.currentScreen.drawHoveringText(tooltip, mouseX, mouseY);
        }
    }
}