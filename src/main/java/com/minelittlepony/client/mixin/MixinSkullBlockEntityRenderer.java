package com.minelittlepony.client.mixin;

import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.mojang.authlib.GameProfile;

import javax.annotation.Nullable;

@Mixin(SkullBlockEntityRenderer.class)
abstract class MixinSkullBlockEntityRenderer extends BlockEntityRenderer<SkullBlockEntity> {

    MixinSkullBlockEntityRenderer() { super(null); }

    @Inject(method = "render("
            + "Lnet/minecraft/util/math/Direction;"
            + "F"
            + "Lnet/minecraft/block/SkullBlock$SkullType;"
            + "Lcom/mojang/authlib/GameProfile;"
            + "F"
            + "Lnet/minecraft/client/util/math/MatrixStack;"
            + "Lnet/minecraft/client/render/VertexConsumerProvider;"
            + "I"
            + ")V", at = @At("HEAD"), cancellable = true)
    private static void onRender(@Nullable Direction direction, float angle,
            SkullBlock.SkullType skullType, @Nullable GameProfile profile, float poweredTicks,
            MatrixStack stack, VertexConsumerProvider renderContext, int lightUv,
            CallbackInfo info) {
        if (!info.isCancelled() && PonySkullRenderer.renderPonySkull(direction, angle, skullType, profile, poweredTicks, stack, renderContext, lightUv)) {
            info.cancel();
        }
    }
}
