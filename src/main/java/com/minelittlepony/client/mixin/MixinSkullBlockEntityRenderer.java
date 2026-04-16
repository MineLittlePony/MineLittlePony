package com.minelittlepony.client.mixin;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.SkullBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

import org.jetbrains.annotations.Nullable;

@Mixin(value = SkullBlockEntityRenderer.class, priority = 2000)
abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<SkullBlockEntity, SkullBlockEntityRenderState> {
    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/state/SkullBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/render/block/entity/SkullBlockEntityRenderer.render(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
    ))
    private void onSubmit(SkullBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue frame, CameraRenderState camera, CallbackInfo info) {
        PonySkullRenderer.INSTANCE.pushState(state.getData(PonySkullRenderer.DATA_KEY));
    }

    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/state/SkullBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/render/block/entity/SkullBlockEntityRenderer.render(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V",
            shift = Shift.AFTER
    ))
    private void afterSubmit(SkullBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue frame, CameraRenderState camera, CallbackInfo info) {
        PonySkullRenderer.INSTANCE.popState();
    }

    @Inject(method = "render(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V", at = @At("HEAD"), cancellable = true)
    private static void onSubmitSkull(
            final Direction facing,
            float yaw,
            final float animationValue,
            final MatrixStack matrices,
            final OrderedRenderCommandQueue frame,
            final int light,
            final SkullBlockEntityModel model,
            final RenderLayer renderType,
            final int outline,
            @Nullable final ModelCommandRenderer.CrumblingOverlayCommand breakProgress,
            CallbackInfo info) {
        var state = PonySkullRenderer.INSTANCE.popState();
        if (!info.isCancelled() && state != null && state.render(facing, yaw, matrices, frame, light, outline, breakProgress)) {
            info.cancel();
        }
    }

    @Inject(
        method = "updateRenderState",
        at = @At("RETURN"))
    private void onUpdateRenderState(
            SkullBlockEntity entity,
            SkullBlockEntityRenderState state,
            float tickDelta,
            Vec3d cameraPos,
            @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumbling,
            CallbackInfo info
        ) {
        state.setData(PonySkullRenderer.DATA_KEY, PonySkullRenderer.INSTANCE.getSkullState(state.skullType, entity.getOwner(), null, entity.getPoweredTicks(tickDelta)));
    }
}

@Mixin(value = HeadFeatureRenderer.class, priority = 2000)
abstract class MixinCustomHeadRenderer<S extends LivingEntityRenderState, M extends EntityModel<S> & ModelWithHead> extends FeatureRenderer<S, M> {
    MixinCustomHeadRenderer() {super(null); }

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/render/block/entity/SkullBlockEntityRenderer.render(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
    ))
    private void onSubmit(final MatrixStack poseStack, final OrderedRenderCommandQueue submitNodeCollector, final int lightCoords, final S state, final float yRot, final float xRot, CallbackInfo info) {
        if (state.wearingSkullType != null) {
            PonySkullRenderer.INSTANCE.pushState(PonySkullRenderer.INSTANCE.getSkullState(state.wearingSkullType, state.wearingSkullProfile, null, state.headItemAnimationProgress));
        }
    }

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/render/block/entity/SkullBlockEntityRenderer.render(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V",
            shift = Shift.AFTER
    ))
    private void afterSubmit(final MatrixStack poseStack, final OrderedRenderCommandQueue submitNodeCollector, final int lightCoords, final S state, final float yRot, final float xRot, CallbackInfo info) {
        PonySkullRenderer.INSTANCE.popState();
    }
}





