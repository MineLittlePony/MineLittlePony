package com.mumfrey.liteloader.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{
    @Inject(method = "startGame()V", at = @At("RETURN"))
    private void onStartupComplete(CallbackInfo ci)
    {
        ClientProxy.onStartupComplete();
    }
    
    @Inject(method = "updateFramebufferSize()V", at = @At("HEAD"))
    private void onResize(CallbackInfo ci)
    {
        ClientProxy.onResize((Minecraft)(Object)this);
    }
    
    @Inject(method = "runTick()V", at = @At("HEAD"))
    private void newTick(CallbackInfo ci)
    {
        ClientProxy.newTick();
    }
    
    @Inject(method = "runGameLoop()V", at = @At(
        value = "INVOKE",
        shift = Shift.AFTER,
        target = "Lnet/minecraft/client/renderer/EntityRenderer;updateCameraAndRender(F)V"
    ))
    private void onTick(CallbackInfo ci)
    {
        ClientProxy.onTick();
    }
    
    @Redirect(method = "runGameLoop()V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/shader/Framebuffer;framebufferRender(II)V"
    ))
    private void renderFBO(Framebuffer framebufferMc, int width, int height)
    {
        boolean fboEnabled = OpenGlHelper.isFramebufferEnabled();
        if (fboEnabled)
        {
            ClientProxy.preRenderFBO(framebufferMc);
            framebufferMc.framebufferRender(width, height);
            ClientProxy.preRenderFBO(framebufferMc);
        }
        else
        {
            framebufferMc.framebufferRender(width, height);
        }
    }
    
    @Inject(method = "runGameLoop()V", at = @At(
        value = "INVOKE_STRING",
        target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
        args = "ldc=tick"
    ))
    private void onTimerUpdate(CallbackInfo ci)
    {
        ClientProxy.onTimerUpdate();
    }
    
    @Inject (method = "runGameLoop()V", at = @At(
        value = "INVOKE_STRING",
        target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
        args = "ldc=gameRenderer"
    ))
    private void onRender(CallbackInfo ci)
    {
        ClientProxy.onRender();
    }
}
