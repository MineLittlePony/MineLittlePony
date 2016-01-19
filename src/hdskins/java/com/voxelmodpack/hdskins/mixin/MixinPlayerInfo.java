package com.voxelmodpack.hdskins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinPlayerInfo {

    @Shadow
    private GameProfile gameProfile;

    @Inject(method = "hasLocationSkin",
            cancellable = true,
            at = @At("RETURN") )
    private void hasLocationSkin(CallbackInfoReturnable<Boolean> ci) {
        boolean has = ci.getReturnValueZ();
        if (!has) {
            // in case has no skin
            ci.setReturnValue(HDSkinManager.getSkin(gameProfile).isPresent());
        }
    }

    @Inject(method = "getLocationSkin",
            cancellable = true,
            at = @At("RETURN") )
    private void getLocationSkin(CallbackInfoReturnable<ResourceLocation> ci) {
        Optional<ResourceLocation> skin = HDSkinManager.getSkin(gameProfile);
        if (skin.isPresent()) {
            // set the skin
            ci.setReturnValue(skin.get());
        }
    }
}
