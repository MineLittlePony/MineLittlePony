package com.minelittlepony.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.List;

public class IconicToggle extends IconicButton {

    private Style onState = new Style();
    private Style offState = new Style();

    private boolean value;

    public IconicToggle(int x, int y, IGuiAction<IconicToggle> callback) {
        super(x, y, callback);
    }

    public boolean getValue() {
        return value;
    }

    public IconicToggle setValue(boolean value) {
        if (this.value != value) {
            this.value = value;
            (value ? onState : offState).apply(this);
        }

        return this;
    }

    public IconicToggle setStyle(Style style, boolean value) {
        if (value) {
            onState = style;
        } else {
            offState = style;
        }

        if (this.value == value) {
            style.apply(this);
        }

        return this;
    }

    @Override
    public void perform() {
        setValue(!value);
        super.perform();
    }

    public static class Style implements IGuiTooltipped<Style> {
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
            Items.LEATHER_LEGGINGS.setColor(stack, colour);
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
}
