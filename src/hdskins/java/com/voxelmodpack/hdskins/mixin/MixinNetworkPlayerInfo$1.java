package com.voxelmodpack.hdskins.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.INetworkPlayerInfo;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(targets = "net.minecraft.client.network.NetworkPlayerInfo$1")
public abstract class MixinNetworkPlayerInfo$1 implements SkinManager.SkinAvailableCallback {

    @Shadow(remap = false, aliases = {"this$0", "field_177224_a", "a"})
    @Final
    private NetworkPlayerInfo player;

    @Inject(method = "skinAvailable("
            + "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;"
            + "Lnet/minecraft/util/ResourceLocation;"
            + "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;"
            + ")V",
            at = @At(value = "HEAD"))
    private void skinAvailable(MinecraftProfileTexture.Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture, CallbackInfo ci) {
        CompletableFuture.runAsync(() -> {
            // schedule parsing next tick, texture may not be uploaded at this point
            HDSkinManager.INSTANCE.parseSkin(player.getGameProfile(), typeIn, location, profileTexture);

            // reset the skin type because vanilla has already set it
            String model = profileTexture.getMetadata("model");
            ((INetworkPlayerInfo) player).setSkinType(model != null ? model : "default");
        });
    }
}
