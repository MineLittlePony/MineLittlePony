package com.voxelmodpack.hdskins;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.minelittlepony.avatar.texture.TextureData;
import com.minelittlepony.avatar.texture.TextureProfile;
import com.minelittlepony.avatar.texture.TextureService;
import com.minelittlepony.avatar.texture.TextureType;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.voxelmodpack.hdskins.skins.CallableFutures;
import com.voxelmodpack.hdskins.skins.SkinServer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

public class HDSkinTextureService implements TextureService {

    private static final Logger logger = LogManager.getLogger();
    private static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private final LoadingCache<GameProfile, CompletableFuture<Map<TextureType, TextureProfile>>> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build(new CacheLoader<GameProfile, CompletableFuture<Map<TextureType, TextureProfile>>>() {
                @Override
                public CompletableFuture<Map<TextureType, TextureProfile>> load(GameProfile key) {
                    return CallableFutures.asyncFailableFuture(() -> loadTexturesFromServer(key), THREAD_POOL)
                            .exceptionally(e -> {
                                logger.catching(e);
                                return Collections.emptyMap();
                            });
                }
            });

    private BiMap<String, Class<? extends SkinServer>> skinServerTypes = HashBiMap.create(2);

    @Override
    public CompletableFuture<Map<TextureType, TextureProfile>> loadProfileTextures(GameProfile profile) {
        // try to recreate a broken gameprofile
        // happens when server sends a random profile with skin and displayname
        Property textures = Iterables.getFirst(profile.getProperties().get("textures"), null);
        if (textures != null) {
            String json = new String(Base64.getDecoder().decode(textures.getValue()), StandardCharsets.UTF_8);
            MinecraftTexturesPayload texturePayload = SkinServer.gson.fromJson(json, MinecraftTexturesPayload.class);
            if (texturePayload != null) {
                // name is optional
                String name = texturePayload.getProfileName();
                UUID uuid = texturePayload.getProfileId();
                // uuid is required
                if (uuid != null) {
                    profile = new GameProfile(uuid, name);
                }

                // probably uses this texture for a reason. Don't mess with it.
                if (!texturePayload.getTextures().isEmpty() && texturePayload.getProfileId() == null) {
                    return CompletableFuture.completedFuture(Collections.emptyMap());
                }
            }
        }
        return cache.getUnchecked(profile);
    }

    private Map<TextureType, TextureProfile> loadTexturesFromServer(GameProfile profile) throws IOException {

        Map<TextureType, TextureProfile> textures = new HashMap<>();
        for (SkinServer server : HDSkinManager.INSTANCE.getSkinServers()) {
            Map<TextureType, TextureProfile> payload = server.loadProfileData(profile).getTextures();
            if (payload != null) {
                payload.forEach(textures::putIfAbsent);
            }
        }
        return textures;
    }

    @Override
    public TextureData loadTexture(TextureType type, TextureProfile texture, @Nullable BiConsumer<TextureType, TextureData> callback) {

        String url = bustCache(texture.getUrl());
        String skinDir = type.toString().toLowerCase() + "s/";
        final ResourceLocation skin = new ResourceLocation("hdskins", skinDir + texture.getHash());
        TextureData data = new TextureData(skin, texture);

        File file2 = new File(Launch.assetsDir, "hd/" + skinDir + texture.getHash().substring(0, 2) + "/" + texture.getHash());

        final IImageBuffer buffer = type == TextureType.SKIN ? new ImageBufferDownloadHD() : null;


        // an actual callback is not needed because that is handled by CompletableFuture
        ITextureObject texObject = new ThreadDownloadImageETag(file2, url, DefaultPlayerSkin.getDefaultSkinLegacy(),
                new IImageBuffer() {
                    @Override
                    public BufferedImage parseUserSkin(BufferedImage image) {
                        BufferedImage image1 = image;
                        if (buffer != null) {
                            image1 = buffer.parseUserSkin(image);
                        }
                        return image1 == null ? image : image1;
                    }

                    @Override
                    public void skinAvailable() {
                        if (callback != null) {
                            callback.accept(type, data);
                        }
                    }

                });
        TextureLoader.loadTexture(skin, texObject);

        return new TextureData(skin, texture);
    }

    private static String bustCache(URI url) {

        long time = Calendar.getInstance().getTimeInMillis();

        return new URIBuilder(url).addParameter(Long.toString(time), null).toString();
    }

}
