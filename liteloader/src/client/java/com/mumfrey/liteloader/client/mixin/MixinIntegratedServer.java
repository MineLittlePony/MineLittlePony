package com.mumfrey.liteloader.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MinecraftServer
{
    public MixinIntegratedServer()
    {
        super(null, null);
    }
    
    @Inject(
        method = "<init>*", //(Lnet/minecraft/client/Minecraft;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;)V",
        at = @At("RETURN"),
        remap = false
    )
    private void onConstructed(Minecraft mcIn, String folderName, String worldName, WorldSettings settings, CallbackInfo ci)
    {
        ClientProxy.onCreateIntegratedServer((IntegratedServer)(Object)this, folderName, worldName, settings);
    }

    @Surrogate
    private void onConstructed(Minecraft mcIn, CallbackInfo ci)
    {
//        ClientProxy.onCreateIntegratedServer((IntegratedServer)(Object)this, folderName, worldName, settings);
    }
}
