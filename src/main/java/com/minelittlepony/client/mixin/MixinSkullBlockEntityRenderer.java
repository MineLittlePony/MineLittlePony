package com.minelittlepony.client.mixin;

import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

import org.jetbrains.annotations.Nullable;

@Mixin(value = SkullBlockRenderer.class, priority = 2000)
abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<SkullBlockEntity, SkullBlockRenderState> {
    @Inject(method = "submit", at = @At("HEAD"))
    private void onSubmit(SkullBlockRenderState state, PoseStack matrices, SubmitNodeCollector frame, CameraRenderState camera, CallbackInfo info) {
        PonySkullRenderer.INSTANCE.pushState(state.getData(PonySkullRenderer.DATA_KEY));
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
        if (!info.isCancelled() && state != null && state.render(matrices, frame, light, outline, breakProgress)) {
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

    @ModifyReturnValue(method = "getSkullRenderType", at = @At("RETURN"))
    private static RenderType replaceRenderLayer(RenderType layer, SkullBlock.Type skullType, @Nullable Identifier overrideTexture) {
        if (overrideTexture == null) {
            var state = PonySkullRenderer.INSTANCE.getSkullState(skullType, null, null, 0);
            if (state != null && state.model().canRender(PonyConfig.getInstance())) {
                PonySkullRenderer.INSTANCE.pushState(state);
                return state.layer();
            }
        }
        return layer;
    }
}

