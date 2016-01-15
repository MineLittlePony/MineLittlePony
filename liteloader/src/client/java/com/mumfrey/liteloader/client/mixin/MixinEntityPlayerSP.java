package com.mumfrey.liteloader.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.client.ClientProxy;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer
{
    public MixinEntityPlayerSP()
    {
        super(null, null);
    }
    
    @Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = { @At("HEAD") }, cancellable = true)
    public void onSendChatMessage(String message, CallbackInfo ci)
    {
        ClientProxy.onOutboundChat(ci, message);
    }
}
