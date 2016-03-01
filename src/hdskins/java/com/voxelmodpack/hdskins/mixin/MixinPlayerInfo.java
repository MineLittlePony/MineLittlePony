package com.voxelmodpack.hdskins.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinPlayerInfo {

    @Final
    @Shadow
    private GameProfile gameProfile;
    @Shadow
    private ResourceLocation locationSkin;
    @Shadow
    public String skinType;

    @Inject(method = "hasLocationSkin",
            cancellable = true,
            at = @At("RETURN") )
    private void hasLocationSkin(CallbackInfoReturnable<Boolean> ci) {
        if (locationSkin == null) {
            // in case has no skin
            Optional<ResourceLocation> skin = HDSkinManager.INSTANCE.getSkinLocation(gameProfile, Type.SKIN, false);
            ci.setReturnValue(skin.isPresent());
        }
    }

    @Inject(method = "getLocationSkin",
            cancellable = true,
            at = @At("RETURN") )
    private void getLocationSkin(CallbackInfoReturnable<ResourceLocation> ci) {
        Optional<ResourceLocation> skin = HDSkinManager.INSTANCE.getSkinLocation(gameProfile, Type.SKIN, true);
        if (skin.isPresent()) {
            // set the skin
            ci.setReturnValue(skin.get());
        }
    }

    @Inject(method = "getLocationCape",
            cancellable = true,
            at = @At("RETURN") )
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> ci) {
        Optional<ResourceLocation> cape = HDSkinManager.INSTANCE.getSkinLocation(gameProfile, Type.CAPE, true);
        if (cape.isPresent()) {
            // set the cape
            ci.setReturnValue(cape.get());
        }
    }

    @Inject(method = "getSkinType",
            cancellable = true,
            at = @At("RETURN") )
    private void getSkinType(CallbackInfoReturnable<String> ci) {
        MinecraftProfileTexture data = HDSkinManager.INSTANCE.getProfileData(gameProfile).get(Type.SKIN);
        if (data != null) {
            String type = data.getMetadata("model");
            boolean hasSkin = HDSkinManager.INSTANCE.getSkinLocation(gameProfile, Type.SKIN, false).isPresent();
            if (hasSkin) {
                if (type == null)
                    type = "default";
                ci.setReturnValue(type);
            }
        }
    }
}
