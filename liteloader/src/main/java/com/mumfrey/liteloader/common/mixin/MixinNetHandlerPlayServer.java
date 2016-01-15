package com.mumfrey.liteloader.common.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mumfrey.liteloader.core.Proxy;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.world.WorldServer;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer
{
    @Inject(
        method = "processPlayerBlockPlacement(Lnet/minecraft/network/play/client/C08PacketPlayerBlockPlacement;)V",
        cancellable = true,
        at = @At(
            value = "INVOKE",
            shift = Shift.AFTER,
            target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue"
                    + "(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"
        )
    )
    private void onPlaceBlock(C08PacketPlayerBlockPlacement packetIn, CallbackInfo ci)
    {
        Proxy.onPlaceBlock(ci, (NetHandlerPlayServer)(Object)this, packetIn);
    }
    
    @Inject(
        method = "handleAnimation(Lnet/minecraft/network/play/client/C0APacketAnimation;)V",
        cancellable = true,
        at = @At(
            value = "INVOKE",
            shift = Shift.AFTER,
            target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue"
                    + "(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"
        )
    )
    private void onClickedAir(C0APacketAnimation packetIn, CallbackInfo ci)
    {
        Proxy.onClickedAir(ci, (NetHandlerPlayServer)(Object)this, packetIn);
    }
    
    @Inject(
        method = "processPlayerDigging(Lnet/minecraft/network/play/client/C07PacketPlayerDigging;)V",
        cancellable = true,
        at = @At(
            value = "INVOKE",
            shift = Shift.AFTER,
            target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue"
                    + "(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"
        )
    )
    private void onPlayerDigging(C07PacketPlayerDigging packetIn, CallbackInfo ci)
    {
        Proxy.onPlayerDigging(ci, (NetHandlerPlayServer)(Object)this, packetIn);
    }
    
    @Inject(
        method = "processPlayer(Lnet/minecraft/network/play/client/C03PacketPlayer;)V",
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD,
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETFIELD,
            target = "Lnet/minecraft/entity/Entity;posY:D",
            ordinal = 4
        )
    )
    private void onPlayerMoved(C03PacketPlayer packetIn, CallbackInfo ci, WorldServer world, double oldPosX, double oldPosY, double oldPosZ,
            double deltaMoveSq, double deltaX, double deltaY, double deltaZ)
    {
        Proxy.onPlayerMoved(ci, (NetHandlerPlayServer)(Object)this, packetIn, world, oldPosX, oldPosY, oldPosZ, deltaMoveSq, deltaX, deltaY, deltaZ);
    }
    
    @Surrogate
    private void onPlayerMoved(C03PacketPlayer packetIn, CallbackInfo ci, WorldServer world, double oldPosX, double oldPosY, double oldPosZ)
    {
        Proxy.onPlayerMoved(ci, (NetHandlerPlayServer)(Object)this, packetIn, world, oldPosX, oldPosY, oldPosZ);
    }
}
