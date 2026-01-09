package com.minelittlepony.client.mixin;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.HorseCam;

@Mixin(PlayerInteractItemC2SPacket.class)
abstract class MixinPlayerInteractItemC2SPacket implements Packet<ClientPlayPacketListener> {

    @Shadow
    @Mutable
    private @Final float pitch;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(Hand hand, int sequence, float yaw, float pitch, CallbackInfo into) {
        this.pitch = HorseCam.transformCameraAngle(this.pitch);
    }
}
