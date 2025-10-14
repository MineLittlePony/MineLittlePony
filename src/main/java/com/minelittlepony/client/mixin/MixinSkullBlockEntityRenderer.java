package com.minelittlepony.client.mixin;

import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.SkullBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
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
abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<SkullBlockEntity, SkullBlockEntityRenderState> {
    @Nullable
    private static final AtomicReference<PonySkullRenderer.Data> ponySkullState = new AtomicReference<>(null);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void onRenderSkull(@Nullable Direction direction,
            float yaw, float poweredTicks,
            MatrixStack matrices, OrderedRenderCommandQueue queue,
            int light,
            SkullBlockEntityModel model, RenderLayer layer,
            int outlineColor,
            @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay,
            CallbackInfo info) {

        if (!info.isCancelled()) {
            var state = ponySkullState.getAndSet(null);
            if (state != null && state.render(direction, yaw, poweredTicks, matrices, queue, light, outlineColor, crumblingOverlay)) {
                info.cancel();
            }
        }
    }

    @Inject(method = "renderSkull(Lnet/minecraft/block/SkullBlock$SkullType;Lnet/minecraft/block/entity/SkullBlockEntity;)Lnet/minecraft/client/render/RenderLayer;", at = @At("HEAD"))
    private static void onGetRenderLayer(SkullBlock.SkullType type, @Nullable ProfileComponent profile, CallbackInfoReturnable<RenderLayer> info) {
        ponySkullState.set(PonySkullRenderer.INSTANCE.getSkullState(type, profile));
    }
}
