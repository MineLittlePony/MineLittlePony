package com.voxelmodpack.hdskins.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.voxelmodpack.hdskins.gui.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.Set;

public interface SkinServer extends Exposable {
    /**
     * Returns the set of features that this skin server supports.
     */
    Set<Feature> getFeatures();

    /**
     * Synchronously loads texture information for the provided profile.
     *
     * @return The parsed server response as a textures payload.
     *
     * @throws IOException  If any authentication or network error occurs.
     */
    TexturePayload loadProfileData(GameProfile profile) throws IOException, AuthenticationException;

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
    default TexturePayload getPreviewTextures(GameProfile profile) throws IOException, AuthenticationException {
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
