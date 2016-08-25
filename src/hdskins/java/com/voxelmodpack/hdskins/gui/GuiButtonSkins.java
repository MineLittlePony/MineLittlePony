package com.voxelmodpack.hdskins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GuiButtonSkins extends GuiButton {

    public GuiButtonSkins(int buttonId, int x, int y) {
        super(buttonId, x, y, 20, 20, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);

        ItemStack stack = new ItemStack(Items.LEATHER_LEGGINGS, 1, 0);
        Items.LEATHER_LEGGINGS.setColor(stack, 0x3c5dcb);
        mc.getRenderItem().renderItemIntoGUI(stack, this.xPosition + 2, this.yPosition + 2);
    }
}
