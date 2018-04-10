package com.voxelmodpack.hdskins.skins;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import net.minecraft.util.Session;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class ValhallaSkinServer implements SkinServer {

    @SuppressWarnings("unused")
	private final String baseURL;

    public ValhallaSkinServer(String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile) {
        return Optional.empty();
    }

    @Override
    public Optional<MinecraftProfileTexture> getPreviewTexture(MinecraftProfileTexture.Type type, GameProfile profile) {
        return null;
    }

    @Override
    public ListenableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable Path image, MinecraftProfileTexture.Type type) {
        return null;
    }

    static ValhallaSkinServer from(String server) {
        Matcher matcher = Pattern.compile("^valhalla:(.*)$").matcher(server);
        if (matcher.find())
            return new ValhallaSkinServer(matcher.group(1));
        throw new IllegalArgumentException();
    }
}
