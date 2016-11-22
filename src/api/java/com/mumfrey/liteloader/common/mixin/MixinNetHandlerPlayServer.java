/*
 * This file is part of LiteLoader.
 * Copyright (C) 2012-16 Adam Mummery-Smith
 * All Rights Reserved.
 */
package com.mumfrey.liteloader.common.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mumfrey.liteloader.common.ducks.ITeleportHandler;
import com.mumfrey.liteloader.core.Proxy;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer implements ITeleportHandler
{
    @Shadow private int teleportId;
    @Shadow private Vec3d targetPos;
    
    @Inject(
        method = "processTryUseItem(Lnet/minecraft/network/play/client/CPacketPlayerTryUseItem;)V",
        cancellable = true,
        at = @At(
            value = "INVOKE",
            shift = Shift.AFTER,
            target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue"
                    + "(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"
        )
    )
    private void onPlaceBlock(CPacketPlayerTryUseItem packetIn, CallbackInfo ci)
    {
        Proxy.onPlaceBlock(ci, (NetHandlerPlayServer)(Object)this, packetIn);
    }
    
    @Inject(
        method = "handleAnimation(Lnet/minecraft/network/play/client/CPacketAnimation;)V",
        cancellable = true,
        at = @At(
            value = "INVOKE",
            shift = Shift.AFTER,
            target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue"
                    + "(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"
        )
    )
    private void onClickedAir(CPacketAnimation packetIn, CallbackInfo ci)
    {
        Proxy.onClickedAir(ci, (NetHandlerPlayServer)(Object)this, packetIn);
    }
    
    @Inject(
        method = "processPlayerDigging(Lnet/minecraft/network/play/client/CPacketPlayerDigging;)V",
        cancellable = true,
        at = @At(
            value = "INVOKE",
            shift = Shift.AFTER,
            target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue"
                    + "(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"
        )
    )
    private void onPlayerDigging(CPacketPlayerDigging packetIn, CallbackInfo ci)
    {
        Proxy.onPlayerDigging(ci, (NetHandlerPlayServer)(Object)this, packetIn);
    }
    
    @Inject(
        method = "processPlayer(Lnet/minecraft/network/play/client/CPacketPlayer;)V",
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD,
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETFIELD,
            target = "Lnet/minecraft/entity/player/EntityPlayerMP;posY:D",
            ordinal = 3
        )
    )
    private void onPlayerMoved(CPacketPlayer packetIn, CallbackInfo ci, WorldServer world)
    {
        Proxy.onPlayerMoved(ci, (NetHandlerPlayServer)(Object)this, packetIn, world);
    }
    
    @Override
    public int beginTeleport(Vec3d location)
    {
        this.targetPos = location;

        if (++this.teleportId == Integer.MAX_VALUE)
        {
            this.teleportId = 0;
        }
        
        return this.teleportId;
    }
}