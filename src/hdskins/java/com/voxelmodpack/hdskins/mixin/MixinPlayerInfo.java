package com.voxelmodpack.hdskins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.INetworkPlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinPlayerInfo implements INetworkPlayerInfo {


    private Map<Type, ResourceLocation> customTextures = new HashMap<>();
    private Map<Type, MinecraftProfileTexture> customProfiles = new HashMap<>();

    @Shadow @Final private GameProfile gameProfile;

    @Shadow public abstract String getSkinType();

    @SuppressWarnings("InvalidMemberReference") // mc-dev bug?
    @Redirect(
            method = {
                    "getLocationSkin",
                    "getLocationCape",
                    "getLocationElytra"
            }, at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    // synthetic
    private Object getSkin(Map<Type, ResourceLocation> playerTextures, Object key) {
        return getSkin(playerTextures, (Type) key);
    }

    // with generics
    private ResourceLocation getSkin(Map<Type, ResourceLocation> playerTextures, Type type) {
        return getResourceLocation(type).orElseGet(() -> playerTextures.get(type));
    }

    @Inject(method = "getSkinType", at = @At("RETURN"), cancellable = true)
    private void getTextureModel(CallbackInfoReturnable<String> cir) {
        getProfileTexture(Type.SKIN).ifPresent(profile -> {
            String model = profile.getMetadata("model");
            cir.setReturnValue(model != null ? model : "default");
        });
    }

    @Inject(method = "loadPlayerTextures",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/SkinManager;loadProfileTextures("
                            + "Lcom/mojang/authlib/GameProfile;"
                            + "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"
                            + "Z)V",
                    shift = At.Shift.BEFORE))
    private void onLoadTexture(CallbackInfo ci) {
        HDSkinManager.INSTANCE.loadProfileTextures(this.gameProfile)
                .thenAcceptAsync(m -> m.forEach((type, profile) -> {
                    HDSkinManager.INSTANCE.loadTexture(type, profile, (typeIn, location, profileTexture) -> {
                        customTextures.put(type, location);
                        customProfiles.put(type, profileTexture);
                    });
                }), Minecraft.getMinecraft()::addScheduledTask);
    }

    @Override
    public Optional<ResourceLocation> getResourceLocation(Type type) {
        return Optional.ofNullable(this.customTextures.get(type));
    }

    @Override
    public Optional<MinecraftProfileTexture> getProfileTexture(Type type) {
        return Optional.ofNullable(this.customProfiles.get(type));
    }

    @Override
    public void deleteTextures() {
        TextureManager tm = Minecraft.getMinecraft().getTextureManager();
        this.customTextures.values().forEach(tm::deleteTexture);
        this.customTextures.clear();
        this.customProfiles.clear();
    }
}
