package com.voxelmodpack.hdskins.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.voxelmodpack.hdskins.gui.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.UUID;

public interface SkinServer extends Exposable {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .create();

    /**
     * Returns true for any features that this skin server supports.
     */
    boolean supportsFeature(Feature feature);

    /**
     * Synchronously loads texture information for the provided profile.
     *
     * @return The parsed server response as a textures payload.
     *
     * @throws IOException  If any authentication or network error occurs.
     */
    MinecraftTexturesPayload loadProfileData(GameProfile profile) throws IOException, AuthenticationException;

    /**
     * Synchronously uploads a skin to this server.
     *
     * @param upload The payload to send.
     *
     * @return A server response object.
     *
     * @throws IOException  If any authentication or network error occurs.
     * @throws AuthenticationException
     */
    void performSkinUpload(SkinUpload upload) throws IOException, AuthenticationException;

    /**
     * Asynchronously loads texture information for the provided profile.
     *
     * Returns an incomplete future for chaining other actions to be performed after this method completes.
     * Actions are dispatched to the default skinDownloadExecutor
     * @throws AuthenticationException
     * @throws IOException
     */
    default MinecraftTexturesPayload getPreviewTextures(GameProfile profile) throws IOException, AuthenticationException {
        return loadProfileData(profile);
    }

    /**
     * Joins with the Mojang API to verify the current user's session.

     * @throws AuthenticationException if authentication failed or the session is invalid.
     */
    static void verifyServerConnection(Session session, String serverId) throws AuthenticationException {
        MinecraftSessionService service = Minecraft.getMinecraft().getSessionService();
        service.joinServer(session.getProfile(), session.getToken(), serverId);
    }
}
