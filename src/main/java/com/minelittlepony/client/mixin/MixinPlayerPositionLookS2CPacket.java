package com.minelittlepony.client.mixin;

import com.minelittlepony.client.HorseCam;

import net.minecraft.entity.EntityPosition;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerPositionLookS2CPacket.class)
abstract class MixinPlayerPositionLookS2CPacket implements Packet<ClientPlayPacketListener> {
    @Shadow @Mutable
    private @Final EntityPosition change;

    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V",
            at = @At("HEAD"))
    private void onApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo info) {
        change = HorseCam.transformIncomingServerCameraAngle(change);
    }
}
