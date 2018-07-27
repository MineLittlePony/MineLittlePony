package com.voxelmodpack.hdskins.gui;

import com.minelittlepony.gui.Button;
import com.minelittlepony.gui.IGuiAction;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GuiItemStackButton extends Button {

    private ItemStack itemStack;

    public GuiItemStackButton(int x, int y, ItemStack itemStack, IGuiAction<GuiItemStackButton> callback) {
        super(x, y, 20, 20, "", callback);
        this.itemStack = itemStack;
    }

    public GuiItemStackButton(int x, int y, ItemStack itemStack, int colour, IGuiAction<GuiItemStackButton> callback) {
        this(x, y, itemStack, callback);
        Items.LEATHER_LEGGINGS.setColor(itemStack, colour);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);

        mc.getRenderItem().renderItemIntoGUI(itemStack, this.x + 2, this.y + 2);
    }
}
