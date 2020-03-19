package com.minelittlepony.client.mixin;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.HorseCam;

@Mixin(PlayerMoveC2SPacket.Both.class)
abstract class MixinPlayerMoveC2SPacket extends PlayerMoveC2SPacket {
    @Inject(method = "<init>(DDDFFZ)V",
            at = @At("RETURN"))
    private void onInit(CallbackInfo info) {
        this.pitch = HorseCam.transformCameraAngle(this.pitch);
    }
}
@Mixin(PlayerMoveC2SPacket.LookOnly.class)
abstract class MixinPlayerMoveC2SPacket_LookOnly extends PlayerMoveC2SPacket {
    @Inject(method = "<init>(FFZ)V",
            at = @At("RETURN"))
    private void onInit(CallbackInfo info) {
        this.pitch = HorseCam.transformCameraAngle(this.pitch);
    }
}
