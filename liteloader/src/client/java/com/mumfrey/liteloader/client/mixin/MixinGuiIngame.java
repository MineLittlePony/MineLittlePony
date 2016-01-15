package com.mumfrey.liteloader.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.ClientProxy;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui
{
    @Shadow private GuiNewChat persistantChatGUI;
    
    @Inject(method = "renderGameOverlay(F)V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/gui/GuiNewChat;drawChat(I)V"
    ))
    private void onRenderChat(float partialTicks, CallbackInfo ci)
    {
        ClientProxy.onRenderChat(this.persistantChatGUI, partialTicks);
    }

    @Inject(method = "renderGameOverlay(F)V", at = @At(
        value = "INVOKE",
        shift = Shift.AFTER,
        target = "Lnet/minecraft/client/gui/GuiNewChat;drawChat(I)V"
    ))
    private void postRenderChat(float partialTicks, CallbackInfo ci)
    {
        ClientProxy.postRenderChat(this.persistantChatGUI, partialTicks);
    }
}
