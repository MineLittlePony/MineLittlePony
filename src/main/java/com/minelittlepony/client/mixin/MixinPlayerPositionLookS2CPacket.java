package com.minelittlepony.client.mixin;

import com.minelittlepony.client.HorseCam;

import java.util.Set;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.Packet;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerPositionLookS2CPacket.class)
abstract class MixinPlayerPositionLookS2CPacket implements Packet<ClientPlayPacketListener> {
    @Shadow @Mutable
    private @Final float pitch;
    @Shadow
    private @Final Set<PlayerPositionLookS2CPacket.Flag> flags;

    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V",
            at = @At("HEAD"))
    private void onApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo info) {
        if (!flags.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
            pitch = HorseCam.transformIncomingServerCameraAngle(pitch);
        }
    }
}
