package com.minelittlepony.client.mixin;

import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.HorseCam;

@Mixin(PlayerMoveC2SPacket.class)
abstract class MixinPlayerMoveC2SPacket implements Packet<ServerPlayPacketListener> {
    @Shadow @Final @Mutable
    protected float pitch;

    @Shadow @Final
    protected boolean changeLook;

    @Inject(method = "<init>(DDDFFZZZ)V",
            at = @At("RETURN"))
    private void onInit(CallbackInfo info) {
        if (changeLook) {
            pitch = HorseCam.transformCameraAngle(pitch);
        }
    }
}
