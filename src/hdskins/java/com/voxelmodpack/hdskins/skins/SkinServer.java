package com.voxelmodpack.hdskins.skins;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import net.minecraft.util.Session;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

public interface SkinServer {

    List<String> defaultServers = Lists.newArrayList("legacy:http://skins.voxelmodpack.com;http://skinmanager.voxelmodpack.com");

    Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile);

    default Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getPreviewTextures(GameProfile profile) {
        return loadProfileData(profile).map(MinecraftTexturesPayload::getTextures).orElse(Collections.emptyMap());
    }

    CompletableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable URI image,
            MinecraftProfileTexture.Type type, Map<String, String> metadata);

    static SkinServer from(String server) {
        int i = server.indexOf(':');
        if (i >= 0) {
            String type = server.substring(0, i);
            switch (type) {
                case "legacy":
                    return LegacySkinServer.from(server);
                case "valhalla": {
                    return ValhallaSkinServer.from(server);
                }
            }
        }
        throw new IllegalArgumentException();
    }
}
