package com.minelittlepony.client.mixin;

import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entity.PlayerPonyRenderer;

@Mixin(FishingBobberEntityRenderer.class)
abstract class MixinFishingBobberEntityRenderer {
    @SuppressWarnings("unchecked")
    @Inject(method = "getHandPos", at = @At("HEAD"), cancellable = true)
    private void onGetHandPos(PlayerEntity player, float f, float tickProgress, CallbackInfoReturnable<Vec3d> info) {
        if (MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(player) instanceof PlayerPonyRenderer ponyRenderer) {
            Vec3d handPos = ponyRenderer.getHandPos(player, FishingBobberEntityRenderer.getArmHoldingRod(player), f, tickProgress);
            if (handPos != null) {
                info.setReturnValue(handPos);
            }
        }
    }
}
