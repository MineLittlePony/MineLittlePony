package com.mumfrey.liteloader.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.core.Proxy;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer
{
    @Inject(method = "updateTimeLightAndEntities()V", at = @At("HEAD"))
    private void onServerTick(CallbackInfo ci)
    {
        Proxy.onServerTick((MinecraftServer)(Object)this);
    }
}
