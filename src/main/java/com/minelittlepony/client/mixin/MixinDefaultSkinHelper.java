package com.minelittlepony.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.pony.DefaultPonySkinHelper;

import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(DefaultSkinHelper.class)
abstract class MixinDefaultSkinHelper {
    @ModifyReturnValue(method = "getTexture()Lnet/minecraft/util/Identifier;", at = @At("RETURN"))
    private static Identifier replaceDefaultSteveTexture(Identifier returnValue) {
        return PonyConfig.getInstance().ponyLevel.get() == PonyLevel.PONIES ? DefaultPonySkinHelper.STEVE : returnValue;
    }

    @ModifyReturnValue(method = "getSkinTextures(Ljava/util/UUID;)Lnet/minecraft/client/util/SkinTextures;", at = @At("RETURN"))
    private static SkinTextures onGetTexture(SkinTextures returnValue, UUID uuid, CallbackInfoReturnable<SkinTextures> cir) {
        return PonyConfig.getInstance().ponyLevel.get() == PonyLevel.PONIES ? DefaultPonySkinHelper.getTextures(returnValue) : returnValue;
    }
}
