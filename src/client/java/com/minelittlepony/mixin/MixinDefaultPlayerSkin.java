package com.minelittlepony.mixin;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.data.IPonyManager;
import com.minelittlepony.pony.data.PonyLevel;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(DefaultPlayerSkin.class)
public abstract class MixinDefaultPlayerSkin {

    @Inject(method = "getDefaultSkinLegacy", at = @At("HEAD"), cancellable = true)
    private static void legacySkin(CallbackInfoReturnable<ResourceLocation> cir) {
        if (MineLittlePony.getInstance().getConfig().getPonyLevel() == PonyLevel.PONIES) {
            cir.setReturnValue(IPonyManager.STEVE);
        }
    }

    @Inject(method = "getDefaultSkin", at = @At("HEAD"), cancellable = true)
    private static void defaultSkin(UUID uuid, CallbackInfoReturnable<ResourceLocation> cir) {
        if (MineLittlePony.getInstance().getConfig().getPonyLevel() == PonyLevel.PONIES) {
            cir.setReturnValue(IPonyManager.getDefaultSkin(uuid));
        }
    }

    @Inject(method = "getSkinType", at = @At("HEAD"), cancellable = true)
    private static void skinType(UUID uuid, CallbackInfoReturnable<String> cir) {
        if (MineLittlePony.getInstance().getConfig().getPonyLevel() == PonyLevel.PONIES) {

            cir.setReturnValue(MineLittlePony.getInstance().getManager()
                    .getPony(IPonyManager.getDefaultSkin(uuid), uuid)
                    .getRace(false)
                    .getModel()
                    .getId(IPonyManager.isSlimSkin(uuid)));
        }
    }

}
