package com.minelittlepony.client.mixin;

import com.minelittlepony.api.pony.DefaultPonySkinHelper;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.settings.PonyLevel;

import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(DefaultSkinHelper.class)
abstract class MixinDefaultSkinHelper {
    @Inject(method = "getTexture()Lnet/minecraft/util/Identifier;",
            at = @At("HEAD"),
            cancellable = true)
    private static void onGetTexture(CallbackInfoReturnable<Identifier> cir) {
        if (MineLittlePony.getInstance().getConfig().ponyLevel.get() == PonyLevel.PONIES) {
            cir.setReturnValue(DefaultPonySkinHelper.getPonySkin(cir.getReturnValue()));
        }
    }

    @Inject(method = "getTexture(Ljava/util/UUID;)Lnet/minecraft/util/Identifier;",
            at = @At("RETURN"),
            cancellable = true)
    private static void onGetTexture(UUID uuid, CallbackInfoReturnable<Identifier> cir) {
        if (MineLittlePony.getInstance().getConfig().ponyLevel.get() == PonyLevel.PONIES) {
            cir.setReturnValue(DefaultPonySkinHelper.getPonySkin(cir.getReturnValue()));
        }
    }

    @Inject(method = "getModel(Ljava/util/UUID;)Ljava/lang/String;",
            at = @At("RETURN"),
            cancellable = true)
    private static void onGetModel(UUID uuid, CallbackInfoReturnable<String> cir) {
        if (MineLittlePony.getInstance().getConfig().ponyLevel.get() == PonyLevel.PONIES) {
            cir.setReturnValue(MineLittlePony.getInstance().getManager()
                    .getPony(DefaultSkinHelper.getTexture(uuid), uuid)
                    .race()
                    .getModelId("slim".equalsIgnoreCase(cir.getReturnValue())));
        }
    }
}
