package com.minelittlepony.client.mixin;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.api.model.RenderPass;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer {
    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void beforeRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo info) {
        RenderPass.swap(RenderPass.WORLD);
    }

    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void afterRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo info) {
        RenderPass.swap(RenderPass.GUI);
    }
}

@Mixin(value = WorldRenderer.class, priority = 0)
abstract class MixinWorldRenderer {
    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "net.minecraft.client.render.VertexConsumerProvider$Immediate.drawCurrentLayer()V",
            ordinal = 0
    ))
    private void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo info) {
        RenderPass.swap(RenderPass.HUD);
    }
}