package com.voxelmodpack.hdskins.skins;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import net.minecraft.util.Session;

import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;

public class YggSkinServer extends AbstractSkinServer {

    private final String baseURL;

    public YggSkinServer(String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    protected Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile) {
        return Optional.empty();
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public String getGateway() {
        return null;
    }

    @Override
    public MinecraftProfileTexture getPreviewTexture(MinecraftProfileTexture.Type type, GameProfile profile) {
        return null;
    }

    @Override
    public ListenableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable Path image, MinecraftProfileTexture.Type type) {
        return null;
    }
}
