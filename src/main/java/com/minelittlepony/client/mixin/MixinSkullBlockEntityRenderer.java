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
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

import org.jetbrains.annotations.Nullable;

@Mixin(value = SkullBlockEntityRenderer.class, priority = 2000)
abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<SkullBlockEntity, SkullBlockEntityRenderState> {
    @Inject(
        method = "render(Lnet/minecraft/client/render/block/entity/state/SkullBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
        at = @At("HEAD"))
    public void render(
            SkullBlockEntityRenderState state,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            CameraRenderState camera,
            CallbackInfo info
        ) {
        PonySkullRenderer.INSTANCE.pushState(state.getData(PonySkullRenderer.DATA_KEY));
    }

    @Inject(
        method = "render(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V",
        at = @At("HEAD"),
        cancellable = true)
    private static void onRenderSkull(@Nullable Direction direction,
            float yaw, float poweredTicks,
            MatrixStack matrices, OrderedRenderCommandQueue queue,
            int light,
            SkullBlockEntityModel model, RenderLayer layer,
            int outlineColor,
            @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay,
            CallbackInfo info) {
        var state = PonySkullRenderer.INSTANCE.popState();
        if (!info.isCancelled() && state != null && state.render(direction, yaw, poweredTicks, matrices, queue, light, outlineColor, crumblingOverlay)) {
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
        state.setData(PonySkullRenderer.DATA_KEY, PonySkullRenderer.INSTANCE.getSkullState(state.skullType, entity.getOwner(), null));
    }

    @ModifyReturnValue(
        method = "getCutoutRenderLayer(Lnet/minecraft/block/SkullBlock$SkullType;Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;",
        at = @At("RETURN"))
    private static RenderLayer replaceRenderLayer(RenderLayer layer, SkullBlock.SkullType skullType, Identifier overrideTexture) {
        if (overrideTexture == null) {
            var state = PonySkullRenderer.INSTANCE.getSkullState(skullType, null, overrideTexture);
            if (state != null && state.model().canRender(PonyConfig.getInstance())) {
                PonySkullRenderer.INSTANCE.pushState(state);
                return state.layer();
            }
        }
        return layer;
    }
}

