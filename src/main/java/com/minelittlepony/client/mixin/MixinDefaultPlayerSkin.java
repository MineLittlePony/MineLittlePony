package com.minelittlepony.client.mixin;

import com.minelittlepony.api.pony.IPonyManager;
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
abstract class MixinDefaultPlayerSkin {
    @Inject(method = "getTexture()Lnet/minecraft/util/Identifier;",
            at = @At("HEAD"),
            cancellable = true)
    private static void legacySkin(CallbackInfoReturnable<Identifier> cir) {
        if (MineLittlePony.getInstance().getConfig().ponyLevel.get() == PonyLevel.PONIES) {
            cir.setReturnValue(IPonyManager.STEVE);
        }
    }

    @Inject(method = "getTexture(Ljava/util/UUID;)Lnet/minecraft/util/Identifier;",
            at = @At("HEAD"),
            cancellable = true)
    private static void defaultSkin(UUID uuid, CallbackInfoReturnable<Identifier> cir) {
        if (MineLittlePony.getInstance().getConfig().ponyLevel.get() == PonyLevel.PONIES) {
            cir.setReturnValue(IPonyManager.getDefaultSkin(uuid));
        }
    }

    @Inject(method = "getModel(Ljava/util/UUID;)Ljava/lang/String;",
            at = @At("HEAD"),
            cancellable = true)
    private static void skinType(UUID uuid, CallbackInfoReturnable<String> cir) {
        if (MineLittlePony.getInstance().getConfig().ponyLevel.get() == PonyLevel.PONIES) {

            cir.setReturnValue(MineLittlePony.getInstance().getManager()
                    .getPony(IPonyManager.getDefaultSkin(uuid), uuid)
                    .getRace()
                    .getModelId(IPonyManager.isSlimSkin(uuid)));
        }
    }
}
