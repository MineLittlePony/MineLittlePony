package com.mumfrey.liteloader.client.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;

@Mixin(Session.class)
public abstract class MixinSession
{
    @Shadow public abstract String getUsername();

    @Inject(method = "getProfile()Lcom/mojang/authlib/GameProfile;", cancellable = true, at = @At(
        value = "NEW",
        args = "class=com/mojang/authlib/GameProfile",
        ordinal = 1
    ))
    private void generateGameProfile(CallbackInfoReturnable<GameProfile> ci)
    {
        UUID uuid = EntityPlayer.getUUID(new GameProfile((UUID)null, this.getUsername()));
        ci.setReturnValue(new GameProfile(uuid, this.getUsername()));
    }
}
