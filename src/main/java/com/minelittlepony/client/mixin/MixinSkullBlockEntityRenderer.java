package com.minelittlepony.client.mixin;

import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.mojang.authlib.GameProfile;

import javax.annotation.Nullable;

@Mixin(SkullBlockEntityRenderer.class)
abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<SkullBlockEntity> {
    @Inject(method = "method_32161("
            + "Lnet/minecraft/util/math/Direction;"
            + "F"
            + "F"
            + "Lnet/minecraft/client/util/math/MatrixStack;"
            + "Lnet/minecraft/client/render/VertexConsumerProvider;"
            + "I"
            + "Lnet/minecraft/client/render/block/entity/SkullBlockEntityModel;"
            + "Lnet/minecraft/client/render/RenderLayer;"
            + ")V", at = @At("HEAD"), cancellable = true)
    private static void onMethod_32161(@Nullable Direction direction,
            float angle, float poweredTicks,
            MatrixStack stack, VertexConsumerProvider renderContext, int lightUv,
            SkullBlockEntityModel model, RenderLayer layer,
            CallbackInfo info) {

        if (PonySkullRenderer.INSTANCE == null) {
            return;
        }

        if (!info.isCancelled() && PonySkullRenderer.INSTANCE.renderSkull(direction, angle, poweredTicks, stack, renderContext, layer, lightUv)) {
            info.cancel();
        }
    }

    @Inject(method = "method_3578("
            + "Lnet/minecraft/block/SkullBlock$SkullType;"
            + "Lcom/mojang/authlib/GameProfile;"
            + ")Lnet/minecraft/client/render/RenderLayer;", at = @At("HEAD"), cancellable = true)
    private static void onMethod_3578(SkullBlock.SkullType skullType, @Nullable GameProfile profile, CallbackInfoReturnable<RenderLayer> info) {
        if (info.isCancelled() || PonySkullRenderer.INSTANCE == null) {
            return;
        }
        RenderLayer result = PonySkullRenderer.INSTANCE.getRenderLayer(skullType, profile);
        if (result != null) {
            info.setReturnValue(result);
        }
    }
}
