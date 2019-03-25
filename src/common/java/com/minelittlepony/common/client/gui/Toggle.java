package com.minelittlepony.common.client.gui;

import net.minecraft.client.gui.GuiButton;

public class Toggle extends GuiButton {

    private boolean on;

    protected IGuiCallback<Boolean> action;

    public Toggle(int x, int y, boolean value, String label, IGuiCallback<Boolean> callback) {
        super(0, x, y, 20, 20, label);

        on = value;
        action = callback;
    }

    public boolean getValue() {
        return on;
    }

    public void setValue(boolean value) {
        if (value != on) {
            on = action.perform(value);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        setValue(!on);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        int i = hovered ? 2 : 1;
        float value = on ? 1 : 0;
        drawTexturedModalRect(x + (int)(value * (width - 8)), y, 0, 46 + i * 20, 4, 20);
        drawTexturedModalRect(x + (int)(value * (width - 8)) + 4, y, 196, 46 + i * 20, 4, 20);
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }
}
