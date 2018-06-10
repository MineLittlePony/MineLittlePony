package com.voxelmodpack.hdskins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = "hdskins", value = Side.CLIENT)
public class MainMenuListener {

    private static final int SKINS = 5000;

    @SubscribeEvent
    public static void openGui(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiScreen gui = event.getGui();
        if (gui instanceof GuiMainMenu) {
            ItemStack itemStack = new ItemStack(Items.LEATHER_LEGGINGS);
            Items.LEATHER_LEGGINGS.setColor(itemStack, 0x3c5dcb);
            GuiButton btn = new GuiItemStackButton(SKINS, gui.width - 50, gui.height - 50, itemStack);
            event.getButtonList().add(btn);
        }
    }

    @SubscribeEvent
    public static void actionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu && event.getButton().id == SKINS) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiSkins());
        }
    }
}
