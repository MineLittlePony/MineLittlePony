package com.minelittlepony.client.mixin;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.pony.DefaultPonySkinHelper;

import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(DefaultSkinHelper.class)
abstract class MixinDefaultSkinHelper {
    @Inject(method = "getTexture()Lnet/minecraft/util/Identifier;",
            at = @At("RETURN"),
            cancellable = true)
    private static void onGetTexture(CallbackInfoReturnable<Identifier> cir) {
        if (PonyConfig.getInstance().ponyLevel.get() == PonyLevel.PONIES) {
            cir.setReturnValue(DefaultPonySkinHelper.STEVE);
        }
    }

    @Inject(method = "getSkinTextures(Ljava/util/UUID;)Lnet/minecraft/client/util/SkinTextures;",
            at = @At("RETURN"),
            cancellable = true)
    private static void onGetTexture(UUID uuid, CallbackInfoReturnable<SkinTextures> cir) {
        if (PonyConfig.getInstance().ponyLevel.get() == PonyLevel.PONIES) {
            cir.setReturnValue(DefaultPonySkinHelper.getTextures(cir.getReturnValue()));
        }
    }
}
