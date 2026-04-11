package com.minelittlepony.client.mixin;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.HorseCam;

@Mixin(ServerboundUseItemPacket.class)
abstract class MixinPlayerInteractItemC2SPacket implements Packet<ServerGamePacketListener> {
    @Shadow
    @Mutable
    private @Final float xRot;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(InteractionHand hand, int sequence, float yRot, float xRot, CallbackInfo into) {
        this.xRot = HorseCam.transformCameraAngle(this.xRot);
    }
}
