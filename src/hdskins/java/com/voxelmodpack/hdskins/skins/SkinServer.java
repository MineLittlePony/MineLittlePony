package com.voxelmodpack.hdskins.skins;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import net.minecraft.util.Session;

import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;

public interface SkinServer {

    String getAddress();

    String getGateway();

    Optional<MinecraftTexturesPayload> getProfileData(GameProfile profile);

    MinecraftProfileTexture getPreviewTexture(MinecraftProfileTexture.Type type, GameProfile profile);

    ListenableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable Path image, MinecraftProfileTexture.Type type);

    void clearCache();


}
