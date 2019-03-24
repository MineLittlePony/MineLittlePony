package com.minelittlepony.common.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Style implements IGuiTooltipped<Style> {
    ItemStack icon = ItemStack.EMPTY;

    private boolean hasOffset = false;
    private int toolTipX = 0;
    private int toolTipY = 0;

    private List<String> tooltip;

    public Style setIcon(ItemStack stack) {
        icon = stack;

        return this;
    }

    public Style setIcon(ItemStack stack, int colour) {
        stack.getOrCreateChildTag("display").setInt("color", colour);
        return setIcon(stack);
    }

    protected void apply(IconicToggle button) {
        button.setIcon(icon)
              .setTooltip(tooltip);
        if (hasOffset) {
            button.setTooltipOffset(toolTipX, toolTipY);
        }
    }

    @Override
    public Style setTooltip(List<String> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public Style setTooltipOffset(int x, int y) {
        hasOffset = true;
        toolTipX = x;
        toolTipY = y;
        return this;
    }

    @Override
    public void renderToolTip(Minecraft mc, int mouseX, int mouseY) {

    }
}