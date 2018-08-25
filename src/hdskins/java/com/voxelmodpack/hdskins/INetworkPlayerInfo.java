package com.voxelmodpack.hdskins;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public interface INetworkPlayerInfo {

    Optional<ResourceLocation> getResourceLocation(MinecraftProfileTexture.Type type);

    Optional<MinecraftProfileTexture> getProfileTexture(MinecraftProfileTexture.Type type);

    void deleteTextures();
}
