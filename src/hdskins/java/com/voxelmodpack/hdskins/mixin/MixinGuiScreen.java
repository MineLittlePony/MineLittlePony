package com.voxelmodpack.hdskins.mixin;

import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
abstract class MixinGuiScreen extends Gui implements GuiYesNoCallback {

    @Invoker("addButton")
    protected abstract <T extends GuiButton> T invokeAddButton(T buttonIn);

    @Inject(method = "setWorldAndResolution(Lnet/minecraft/client/Minecraft;II)V", at = @At("RETURN"))
    private void setWorldAndResolution(Minecraft mc, int width, int height, CallbackInfo info) {
        HDSkinManager.INSTANCE.displaySkinningGui((GuiScreen)(Object)this, this::invokeAddButton);
    }
}
