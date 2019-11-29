package com.minelittlepony.client.mixin;

import net.minecraft.server.network.packet.PlayerMoveC2SPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.HorseCam;

@Mixin(PlayerMoveC2SPacket.Both.class)
abstract class MixinPlayerMoveC2SPacket_Both extends PlayerMoveC2SPacket {
    @Inject(method = "<init>(DDDFFZ)V",
            at = @At("RETURN"))
    private void onInit(CallbackInfo info) {
        this.pitch = HorseCam.transformCameraAngle(this.pitch);
    }
}
