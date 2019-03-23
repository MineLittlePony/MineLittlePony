package com.minelittlepony.client.mixin;

import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.common.MineLittlePony;
import com.minelittlepony.common.pony.IPonyManager;
import com.minelittlepony.common.settings.PonyLevel;

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

            cir.setReturnValue(PlayerModels.forRace(MineLittlePony.getInstance().getManager()
                    .getPony(IPonyManager.getDefaultSkin(uuid), uuid)
                    .getRace(false))
                    .getId(IPonyManager.isSlimSkin(uuid)));
        }
    }

}
