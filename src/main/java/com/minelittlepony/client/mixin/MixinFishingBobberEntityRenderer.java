package com.minelittlepony.client.mixin;

import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entity.PlayerPonyRenderer;

@Mixin(FishingHookRenderer.class)
abstract class MixinFishingBobberEntityRenderer {
    @SuppressWarnings("unchecked")
    @Inject(method = "getPlayerHandPos", at = @At("HEAD"), cancellable = true)
    private void onGetHandPos(Player player, float swing, float tickDelta, CallbackInfoReturnable<Vec3> info) {
        if (MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(player) instanceof PlayerPonyRenderer ponyRenderer) {
            Vec3 handPos = ponyRenderer.getHandPos(player, FishingHookRenderer.getHoldingArm(player), swing, tickDelta);
            if (handPos != null) {
                info.setReturnValue(handPos);
            }
        }
    }
}
