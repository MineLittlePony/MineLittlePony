package com.voxelmodpack.hdskins.skins;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.mumfrey.liteloader.modconfig.Exposable;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

public interface SkinServer extends Exposable {

    static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .create();

    List<SkinServer> defaultServers = Lists.newArrayList(new LegacySkinServer(
            "http://skins.voxelmodpack.com",
            "http://skinmanager.voxelmodpack.com"));

    Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile);

    default Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getPreviewTextures(GameProfile profile) {
        return loadProfileData(profile).map(MinecraftTexturesPayload::getTextures).orElse(Collections.emptyMap());
    }

    CompletableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable URI image, MinecraftProfileTexture.Type type, Map<String, String> metadata);


    public static void verifyServerConnection(Session session, String serverId) throws AuthenticationException {
        MinecraftSessionService service = Minecraft.getMinecraft().getSessionService();
        service.joinServer(session.getProfile(), session.getToken(), serverId);
    }
}
