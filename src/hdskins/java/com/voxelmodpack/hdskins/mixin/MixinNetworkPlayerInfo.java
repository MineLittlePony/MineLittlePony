package com.voxelmodpack.hdskins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.INetworkPlayerInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo implements INetworkPlayerInfo {

    private Map<Type, ResourceLocation> customTextures = new HashMap<>();
    private Map<Type, MinecraftProfileTexture> customProfiles = new HashMap<>();

    @Shadow @Final
    private GameProfile gameProfile;

    @Shadow
    private boolean playerTexturesLoaded;

    @Shadow
    private String skinType;

    @Shadow
    private Map<Type, ResourceLocation> playerTextures;

    @SuppressWarnings("InvalidMemberReference") // mc-dev bug?
    @Redirect(method = { "getLocationSkin", "getLocationCape", "getLocationElytra" },
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
    // synthetic
    private Object getSkin(Map<Type, ResourceLocation> playerTextures, Object key) {
        return getSkin(playerTextures, (Type) key);
    }

    // with generics
    private ResourceLocation getSkin(Map<Type, ResourceLocation> playerTextures, Type type) {
        if (this.customTextures.containsKey(type)) {
            return this.customTextures.get(type);
        }

        return playerTextures.get(type);
    }

    @Redirect(method = "getSkinType",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/NetworkPlayerInfo;skinType:Ljava/lang/String;"))
    private String getTextureModel(NetworkPlayerInfo self) {
        if (customProfiles.containsKey(Type.SKIN)) {
            String model = customProfiles.get(Type.SKIN).getMetadata("model");

            return model != null ? model : "default";
        }

        return skinType;
    }

    @Inject(method = "loadPlayerTextures",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/SkinManager;loadProfileTextures("
                            + "Lcom/mojang/authlib/GameProfile;"
                            + "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"
                            + "Z)V",
                    shift = At.Shift.BEFORE))
    private void onLoadTexture(CallbackInfo ci) {
        HDSkinManager.INSTANCE.fetchAndLoadSkins(gameProfile, (type, location, profileTexture) -> {
            customTextures.put(type, location);
            customProfiles.put(type, profileTexture);
        });
    }

    @Redirect(method = "loadPlayerTextures",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/SkinManager;loadProfileTextures("
                            + "Lcom/mojang/authlib/GameProfile;"
                            + "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"
                            + "Z)V"))
    private void redirectLoadPlayerTextures(SkinManager skinManager, GameProfile profile, SkinManager.SkinAvailableCallback callback,
            boolean requireSecure) {
        skinManager.loadProfileTextures(profile, (typeIn, location, profileTexture) -> {
            HDSkinManager.INSTANCE.parseSkin(profile, typeIn, location, profileTexture, callback);
        }, requireSecure);
    }


    @Override
    public void reloadTextures() {
        synchronized (this) {
            this.playerTexturesLoaded = false;
            if (this.gameProfile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
                // local client skin doesn't have a signature.
                this.gameProfile.getProperties().removeAll("textures");
            }
        }
    }

    @Override
    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }
}
