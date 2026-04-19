package com.minelittlepony.client.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

import org.jetbrains.annotations.Nullable;

@Mixin(value = SkullBlockRenderer.class, priority = 2000)
abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<SkullBlockEntity, SkullBlockRenderState> {
    @Inject(method = "submit", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/blockentity/SkullBlockRenderer.submitSkull(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/model/object/skull/SkullModelBase;Lnet/minecraft/client/renderer/rendertype/RenderType;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
    ))
    private void onSubmit(SkullBlockRenderState state, PoseStack matrices, SubmitNodeCollector frame, CameraRenderState camera, CallbackInfo info) {
        PonySkullRenderer.INSTANCE.pushState(state.getData(PonySkullRenderer.DATA_KEY));
    }

    @Inject(method = "submit", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/blockentity/SkullBlockRenderer.submitSkull(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/model/object/skull/SkullModelBase;Lnet/minecraft/client/renderer/rendertype/RenderType;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            shift = Shift.AFTER
    ))
    private void afterSubmit(SkullBlockRenderState state, PoseStack matrices, SubmitNodeCollector frame, CameraRenderState camera, CallbackInfo info) {
        PonySkullRenderer.INSTANCE.popState();
    }

    @Inject(method = "submitSkull", at = @At("HEAD"), cancellable = true)
    private static void onSubmitSkull(
            final float animationValue,
            final PoseStack matrices,
            final SubmitNodeCollector frame,
            final int light,
            final SkullModelBase model,
            final RenderType renderType,
            final int outline,
            @Nullable final ModelFeatureRenderer.CrumblingOverlay breakProgress,
            CallbackInfo info) {
        var state = PonySkullRenderer.INSTANCE.popState();
        if (!info.isCancelled() && state != null && state.submit(matrices, frame, light, outline, breakProgress)) {
            info.cancel();
        }
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void onUpdateRenderState(
            final SkullBlockEntity entity,
            final SkullBlockRenderState state,
            final float tickDelta,
            final Vec3 cameraPosition,
            @Nullable final ModelFeatureRenderer.CrumblingOverlay breakProgress,
            CallbackInfo info
        ) {
        state.setData(PonySkullRenderer.DATA_KEY, PonySkullRenderer.INSTANCE.getSkullState(state.skullType, entity.getOwnerProfile(), null, state.animationProgress));
    }
}

@Mixin(value = CustomHeadLayer.class, priority = 2000)
abstract class MixinCustomHeadRenderer<S extends LivingEntityRenderState, M extends EntityModel<S> & HeadedModel> extends RenderLayer<S, M> {
    MixinCustomHeadRenderer() {super(null); }

    @Inject(method = "submit", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/blockentity/SkullBlockRenderer.submitSkull(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/model/object/skull/SkullModelBase;Lnet/minecraft/client/renderer/rendertype/RenderType;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
    ))
    private void onSubmit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final S state, final float yRot, final float xRot, CallbackInfo info) {
        if (state.wornHeadType != null) {
            PonySkullRenderer.INSTANCE.pushState(PonySkullRenderer.INSTANCE.getSkullState(state.wornHeadType, state.wornHeadProfile, null, state.wornHeadAnimationPos));
        }
    }

    @Inject(method = "submit", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/blockentity/SkullBlockRenderer.submitSkull(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/model/object/skull/SkullModelBase;Lnet/minecraft/client/renderer/rendertype/RenderType;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            shift = Shift.AFTER
    ))
    private void afterSubmit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final S state, final float yRot, final float xRot, CallbackInfo info) {
        PonySkullRenderer.INSTANCE.popState();
    }
}





