package com.mumfrey.liteloader.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.client.PacketEventsClient;

import net.minecraft.realms.RealmsScreen;

@Mixin(value = RealmsMainScreen.class, remap = false)
public abstract class MixinRealmsMainScreen extends RealmsScreen
{
    @Inject(method = "play(J)V", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(
        value = "INVOKE",
        target = "Lcom/mojang/realmsclient/RealmsMainScreen;stopRealmsFetcherAndPinger()V"
    ))
    private void onJoinRealm(long serverId, CallbackInfo ci, RealmsServer server)
    {
        PacketEventsClient.onJoinRealm(serverId, server);
    }
}
