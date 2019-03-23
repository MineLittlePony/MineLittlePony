package com.minelittlepony.common.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class IconicButton extends Button {

    private ItemStack itemStack = ItemStack.EMPTY;

    public IconicButton(int x, int y, IGuiAction<? extends IconicButton> callback) {
        super(x, y, 20, 20, "", callback);
    }

    public IconicButton setIcon(ItemStack stack) {
        itemStack = stack;
        return this;
    }

    public IconicButton setIcon(ItemStack stack, int colour) {
        Items.LEATHER_LEGGINGS.setColor(stack, colour);
        return setIcon(stack);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);

        mc.getRenderItem().renderItemIntoGUI(itemStack, x + 2, y + 2);
    }
}
