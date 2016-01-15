package com.mumfrey.liteloader.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.mumfrey.liteloader.core.Proxy;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.ServerConfigurationManager;

@Mixin(ServerConfigurationManager.class)
public abstract class MixinServerConfigurationManager
{
    @Inject(
        method = "initializeConnectionToPlayer(Lnet/minecraft/network/NetworkManager;Lnet/minecraft/entity/player/EntityPlayerMP;)V",
        at = @At("RETURN")
    )
    private void onInitializePlayerConnection(NetworkManager netManager, EntityPlayerMP player, CallbackInfo ci)
    {
        Proxy.onInitializePlayerConnection((ServerConfigurationManager)(Object)this, netManager, player);
    }

    // Because, forge
    @Surrogate
    private void onInitializePlayerConnection(NetworkManager netManager, EntityPlayerMP player, NetHandlerPlayServer nhps, CallbackInfo ci)
    {
        Proxy.onInitializePlayerConnection((ServerConfigurationManager)(Object)this, netManager, player);
    }
    
    @Inject(method = "playerLoggedIn(Lnet/minecraft/entity/player/EntityPlayerMP;)V", at = @At("RETURN"))
    private void onPlayerLogin(EntityPlayerMP player, CallbackInfo ci)
    {
        Proxy.onPlayerLogin((ServerConfigurationManager)(Object)this, player);
    }
    
    @Inject(method = "playerLoggedOut(Lnet/minecraft/entity/player/EntityPlayerMP;)V", at = @At("RETURN"))
    private void onPlayerLogout(EntityPlayerMP player, CallbackInfo ci)
    {
        Proxy.onPlayerLogout((ServerConfigurationManager)(Object)this, player);
    }
    
    @Inject(
        method = "createPlayerForUser(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/entity/player/EntityPlayerMP;",
        cancellable = true,
        at = @At("RETURN")
    )
    private void onSpawnPlayer(GameProfile profile, CallbackInfoReturnable<EntityPlayerMP> cir)
    {
        Proxy.onSpawnPlayer(cir, (ServerConfigurationManager)(Object)this, profile);
    }
    
    @Inject(
        method = "recreatePlayerEntity(Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;",
        cancellable = true,
        at = @At("RETURN")
    )
    private void onRespawnPlayer(EntityPlayerMP player, int dimension, boolean conqueredEnd, CallbackInfoReturnable<EntityPlayerMP> cir)
    {
        Proxy.onRespawnPlayer(cir, (ServerConfigurationManager)(Object)this, player, dimension, conqueredEnd);
    }
}
