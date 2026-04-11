package com.minelittlepony.client.mixin;

import net.minecraft.client.DeltaTracker;
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
    private void beforeRenderWorld(DeltaTracker counter, CallbackInfo info) {
        RenderPass.swap(RenderPass.WORLD);
    }

    @Inject(method = "renderLevel", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/LevelRenderer.renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V",
            shift = Shift.AFTER,
            ordinal = 0
    ))
    private void beforeRenderHud(DeltaTracker counter, CallbackInfo info) {
        RenderPass.swap(RenderPass.HUD);
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void afterRenderWorld(DeltaTracker counter, CallbackInfo info) {
        RenderPass.swap(RenderPass.GUI);
    }
}
