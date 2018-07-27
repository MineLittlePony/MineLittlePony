package com.voxelmodpack.hdskins.mixin;

import com.minelittlepony.gui.IActionable;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.GuiItemStackButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {

    @Inject(method = "initGui()V", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        addButton(new GuiItemStackButton(width - 50, height - 50, new ItemStack(Items.LEATHER_LEGGINGS), 0x3c5dcb, sender -> {
            mc.displayGuiScreen(HDSkinManager.INSTANCE.createSkinsGui());
        }));
    }

    @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At("RETURN"))
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button instanceof IActionable) {
            ((IActionable)button).perform();
        }
    }
}
