package com.minelittlepony.client.mixin;

import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.api.model.RenderPass;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer {
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void beforeRenderWorld(CallbackInfo info) {
        RenderPass.swap(RenderPass.WORLD);
    }

    @Inject(method = "renderLevel", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/LevelRenderer.render(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZZ)V",
            shift = Shift.AFTER,
            ordinal = 0
    ))
    private void beforeRenderHud(CallbackInfo info) {
        RenderPass.swap(RenderPass.HUD);
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void afterRenderWorld(CallbackInfo info) {
        RenderPass.swap(RenderPass.GUI);
    }
}
