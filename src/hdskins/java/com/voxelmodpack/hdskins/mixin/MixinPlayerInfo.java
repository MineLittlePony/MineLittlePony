package com.voxelmodpack.hdskins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinPlayerInfo {

    @Shadow
    public abstract GameProfile getGameProfile();

    @Inject(method = "getLocationSkin",
            cancellable = true,
            at = @At("RETURN"))
    private void getLocationSkin(CallbackInfoReturnable<ResourceLocation> ci) {
        getTextureLocation(ci, Type.SKIN);
    }

    @Inject(method = "getLocationCape",
            cancellable = true,
            at = @At("RETURN"))
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> ci) {
        getTextureLocation(ci, Type.CAPE);
    }

    @Inject(method = "getLocationElytra",
            cancellable = true,
            at = @At("RETURN"))
    private void getLocationElytra(CallbackInfoReturnable<ResourceLocation> ci) {
        getTextureLocation(ci, Type.ELYTRA);
    }

    private void getTextureLocation(CallbackInfoReturnable<ResourceLocation> ci, Type type) {
        HDSkinManager.INSTANCE.getSkinLocation(getGameProfile(), type, true).ifPresent(ci::setReturnValue);
    }

    @Inject(method = "getSkinType",
            cancellable = true,
            at = @At("RETURN"))
    private void getSkinType(CallbackInfoReturnable<String> ci) {
        MinecraftProfileTexture skin = HDSkinManager.INSTANCE.getProfileData(getGameProfile()).get(Type.SKIN);
        if (skin != null) {
            HDSkinManager.INSTANCE.getSkinLocation(getGameProfile(), Type.SKIN, false).ifPresent(res -> {
                String type = skin.getMetadata("model");

                ci.setReturnValue(type == null ? "default" : type);
            });
        }
    }
}
