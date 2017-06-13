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

    // drawButton
    @Override
    public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.func_191745_a(mc, mouseX, mouseY, partialTicks);

        mc.getRenderItem().renderItemIntoGUI(itemStack, this.x + 2, this.y + 2);
    }
}
