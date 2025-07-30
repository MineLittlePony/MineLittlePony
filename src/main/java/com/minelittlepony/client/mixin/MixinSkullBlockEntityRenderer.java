package com.minelittlepony.client.mixin;

import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.math.Direction;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.Nullable;

@Mixin(value = SkullBlockEntityRenderer.class, priority = 2000)
abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<SkullBlockEntity> {
    @Nullable
    private static final AtomicReference<PonySkullRenderer.Data> ponySkullState = new AtomicReference<>(null);

    @Inject(method = "renderSkull", at = @At("HEAD"), cancellable = true)
    private static void onRenderSkull(@Nullable Direction direction,
            float yaw, float animationProgress,
            MatrixStack matrices, VertexConsumerProvider vertices,
            int light,
            SkullBlockEntityModel model, RenderLayer layer,
            CallbackInfo info) {

        if (!info.isCancelled()) {
            var state = ponySkullState.getAndSet(null);
            if (state != null && state.render(direction, yaw, animationProgress, matrices, vertices, light)) {
                info.cancel();
            }
        }
    }

    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    private static void onGetRenderLayer(SkullBlock.SkullType skullType, @Nullable ProfileComponent profile, CallbackInfoReturnable<RenderLayer> info) {
        if (!info.isCancelled()) {
            var data = PonySkullRenderer.INSTANCE.getSkullState(skullType, profile);
            if (data != null) {
                ponySkullState.set(data);
                info.setReturnValue(data.layer());
            }
        }
    }
}
