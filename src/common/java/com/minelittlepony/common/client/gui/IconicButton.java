package com.minelittlepony.common.client.gui;

import net.minecraft.client.Minecraft;
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
        stack.getOrCreateChildTag("display").setInt("color", colour);
        return setIcon(stack);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(itemStack, x + 2, y + 2);
    }
}
