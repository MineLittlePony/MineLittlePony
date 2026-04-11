package com.minelittlepony.client.mixin;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.CopperGolemStatueSpecialRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.resources.Identifier;

import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Suppliers;
import com.minelittlepony.client.render.CopperPonyBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(CopperGolemStatueSpecialRenderer.class)
abstract class MixinCopperGolemStatueModelRenderer implements NoDataSpecialModelRenderer {

    @Shadow
    private @Final Identifier texture;
    private final Supplier<CopperPonyBlockEntityRenderer.SpecialModelRenderer> specialModelRenderer = Suppliers.memoize(() -> {
        return new CopperPonyBlockEntityRenderer.SpecialModelRenderer(texture);
    });

    @Inject(
            method = "getExtents(Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCollectVertices(Consumer<Vector3fc> output, CallbackInfo info) {
        if (specialModelRenderer.get().shouldApply()) {
            specialModelRenderer.get().getExtents(output);
            info.cancel();
        }
    }

    @Inject(
            method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRender(PoseStack matrices, SubmitNodeCollector frame, int light, int overlay, boolean hasFoil, int outline, CallbackInfo info) {
        if (specialModelRenderer.get().shouldApply()) {
            specialModelRenderer.get().submit(matrices, frame, light, overlay, hasFoil, outline);
            info.cancel();
        }
    }


}
