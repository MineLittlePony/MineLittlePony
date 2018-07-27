package com.minelittlepony.gui;

import java.util.List;

import com.google.common.base.Splitter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class Button extends GuiButton implements IActionable {

    private IGuiAction<Button> action;

    private List<String> tooltip = null;

    @SuppressWarnings("unchecked")
    public Button(int x, int y, int width, int height, String label, IGuiAction<? extends Button> callback) {
        super(0, x, y, width, height, GameGui.format(label));
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

    public Button setTooltip(List<String> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public Button setTooltip(String tooltip) {
        return setTooltip(Splitter.on("\r\n").splitToList(GameGui.format(tooltip)));
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);
        if (visible && isMouseOver() && tooltip != null) {
            mc.currentScreen.drawHoveringText(tooltip, mouseX, mouseY);
        }
    }
}
