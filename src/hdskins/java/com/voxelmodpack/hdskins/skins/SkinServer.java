package com.voxelmodpack.hdskins.skins;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SkinServer extends Exposable {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .create();

    List<SkinServer> defaultServers = Lists.newArrayList(new LegacySkinServer(
            "http://skins.voxelmodpack.com",
            "http://skinmanager.voxelmodpack.com"));

    MinecraftTexturesPayload loadProfileData(GameProfile profile) throws IOException;

    CompletableFuture<SkinUploadResponse> uploadSkin(Session session, SkinUpload upload);

    default CompletableFuture<MinecraftTexturesPayload> getPreviewTextures(GameProfile profile) {
        return CallableFutures.asyncFailableFuture(() -> loadProfileData(profile), HDSkinManager.skinDownloadExecutor);
    }

    default boolean verifyGateway() {
        return true;
    }

    static void verifyServerConnection(Session session, String serverId) throws AuthenticationException {
        MinecraftSessionService service = Minecraft.getMinecraft().getSessionService();
        service.joinServer(session.getProfile(), session.getToken(), serverId);
    }
}
