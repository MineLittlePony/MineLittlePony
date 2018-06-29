package com.voxelmodpack.hdskins.skins;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import net.minecraft.util.Session;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public interface SkinServer {

    List<String> defaultServers = Lists.newArrayList("legacy:http://skins.voxelmodpack.com;http://skinmanager.voxelmodpack.com");

    Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile);

    Optional<MinecraftProfileTexture> getPreviewTexture(MinecraftProfileTexture.Type type, GameProfile profile);

    ListenableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable Path image, MinecraftProfileTexture.Type type, boolean thinArmType);

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
