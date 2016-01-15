package com.mumfrey.liteloader.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.ClientProxy;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer
{
    @Inject(method = "updateCameraAndRender(F)V", at = @At(
        value = "INVOKE",
        shift = Shift.AFTER,
        target = "Lnet/minecraft/client/renderer/GlStateManager;clear(I)V"
    ))
    private void onPreRenderGUI(float partialTicks, CallbackInfo ci)
    {
        ClientProxy.preRenderGUI(partialTicks);
    }

    @Inject(method = "updateCameraAndRender(F)V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"
    ))
    private void onRenderHUD(float partialTicks, CallbackInfo ci)
    {
        ClientProxy.onRenderHUD(partialTicks);
    }
    
    @Inject(method = "updateCameraAndRender(F)V", at = @At(
        value = "INVOKE",
        shift = Shift.AFTER,
        target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"
    ))
    private void onPostRenderHUD(float partialTicks, CallbackInfo ci)
    {
        ClientProxy.postRenderHUD(partialTicks);
    }
    
    @Inject(method = "renderWorld(FJ)V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
        ordinal = 0
    ))
    private void onRenderWorld(float partialTicks, long timeSlice, CallbackInfo ci)
    {
        ClientProxy.onRenderWorld(partialTicks, timeSlice);
    }
    
    @Inject(method = "renderWorld(FJ)V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/profiler/Profiler;endSection()V",
        ordinal = 0
    ))
    private void onPostRender(float partialTicks, long timeSlice, CallbackInfo ci)
    {
        ClientProxy.postRender(partialTicks, timeSlice);
    }
    
    @Inject(method = "renderWorldPass(IFJ)V", at = @At(
        value = "INVOKE_STRING",
        target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
        args = "ldc=frustum"
    ))
    private void onSetupCameraTransform(int pass, float partialTicks, long timeSlice, CallbackInfo ci)
    {
        ClientProxy.onSetupCameraTransform(pass, partialTicks, timeSlice);
    }
    
    @Inject(method = "renderWorldPass(IFJ)V", at = @At(
        value = "INVOKE_STRING",
        target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
        args = "ldc=sky"
    ))
    private void onRenderSky(int pass, float partialTicks, long timeSlice, CallbackInfo ci)
    {
        ClientProxy.onRenderSky(pass, partialTicks, timeSlice);
    }
    
    @Inject(method = "renderWorldPass(IFJ)V", at = @At(
        value = "INVOKE_STRING",
        target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
        args = "ldc=terrain"
    ))
    private void onRenderTerrain(int pass, float partialTicks, long timeSlice, CallbackInfo ci)
    {
        ClientProxy.onRenderTerrain(pass, partialTicks, timeSlice);
        
    }
    
    @Inject(method = "renderWorldPass(IFJ)V", at = @At(
        value = "INVOKE_STRING",
        target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
        args = "ldc=litParticles"
    ))
    private void onPostRenderEntities(int pass, float partialTicks, long timeSlice, CallbackInfo ci)
    {
        ClientProxy.postRenderEntities(pass, partialTicks, timeSlice);
    }
    
    @Inject(method = "renderCloudsCheck(Lnet/minecraft/client/renderer/RenderGlobal;FI)V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V"
    ))
    private void onRenderClouds(RenderGlobal renderGlobalIn, float partialTicks, int pass, CallbackInfo ci)
    {
        ClientProxy.onRenderClouds(renderGlobalIn, partialTicks, pass);
    }
}
