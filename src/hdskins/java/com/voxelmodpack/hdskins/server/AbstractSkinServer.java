package com.voxelmodpack.hdskins.server;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.util.CallableFutures;
import com.voxelmodpack.hdskins.util.IndentedToStringStyle;

import net.minecraft.util.Session;

public abstract class AbstractSkinServer implements SkinServer {


    @Override
    public final Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile) {
        return Optional.ofNullable(getProfileData(profile));
    }

    @Override
    public Map<Type, MinecraftProfileTexture> getPreviewTextures(GameProfile profile) {
        return loadProfileData(profile)
                .map(MinecraftTexturesPayload::getTextures)
                .orElse(Collections.emptyMap());
    }

    @Override
    public final CompletableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable URI image, Type type, Map<String, String> metadata) {
        return CallableFutures.asyncFailableFuture(() -> {
            return doUpload(session, image, type, metadata);
        }, HDSkinManager.skinUploadExecutor);
    }

    protected abstract MinecraftTexturesPayload getProfileData(GameProfile profile);

    protected abstract SkinUploadResponse doUpload(Session session, URI image, Type type, Map<String, String> metadata) throws AuthenticationException, IOException;

    protected abstract ToStringBuilder addFields(ToStringBuilder builder);

    @Override
    public String toString() {
        return addFields(new ToStringBuilder(this, IndentedToStringStyle.INSTANCE)).build();
    }
}
