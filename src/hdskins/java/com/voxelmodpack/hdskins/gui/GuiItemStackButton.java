package com.voxelmodpack.hdskins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class GuiItemStackButton extends GuiButton {

    private ItemStack itemStack;

    public GuiItemStackButton(int buttonId, int x, int y, @Nonnull ItemStack itemStack) {
        super(buttonId, x, y, 20, 20, "");
        this.itemStack = itemStack;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);

        mc.getRenderItem().renderItemIntoGUI(itemStack, this.xPosition + 2, this.yPosition + 2);
    }
}
