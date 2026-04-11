package com.minelittlepony.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.pony.DefaultPonySkinHelper;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DefaultPlayerSkin.class)
abstract class MixinDefaultSkinHelper {
    @ModifyReturnValue(method = "getDefaultTexture()Lnet/minecraft/resources/Identifier;", at = @At("RETURN"))
    private static Identifier replaceDefaultSteveTexture(Identifier returnValue) {
        return PonyConfig.getInstance().ponyLevel.get() == PonyLevel.PONIES ? DefaultPonySkinHelper.STEVE : returnValue;
    }

    @ModifyReturnValue(method = "get(Ljava/util/UUID;)Lnet/minecraft/world/entity/player/PlayerSkin;", at = @At("RETURN"))
    private static PlayerSkin onGetTexture(PlayerSkin returnValue) {
        return PonyConfig.getInstance().ponyLevel.get() == PonyLevel.PONIES ? DefaultPonySkinHelper.getTextures(returnValue) : returnValue;
    }
}
