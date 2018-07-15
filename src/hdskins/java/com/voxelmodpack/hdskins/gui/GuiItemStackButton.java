package com.voxelmodpack.hdskins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

public class GuiItemStackButton extends GuiButton {

    private ItemStack itemStack;

    public GuiItemStackButton(int buttonId, int x, int y, ItemStack itemStack) {
        super(buttonId, x, y, 20, 20, "");
        this.itemStack = itemStack;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);

        mc.getRenderItem().renderItemIntoGUI(itemStack, x + 2, y + 2);
    }
}
