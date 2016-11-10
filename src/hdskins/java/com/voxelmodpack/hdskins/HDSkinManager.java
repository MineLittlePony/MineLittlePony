package com.voxelmodpack.hdskins;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.hdskins.resource.SkinResourceManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;

public final class HDSkinManager implements IResourceManagerReloadListener {

    public static final HDSkinManager INSTANCE = new HDSkinManager();
    private static final ResourceLocation LOADING = new ResourceLocation("LOADING");
    public static final String METADATA_KEY = "hdskins.metadata";

    private String gatewayUrl = "skinmanager.voxelmodpack.com";
    private String skinUrl = "skins.voxelmodpack.com";
    private boolean enabled = true;

    private Map<UUID, Map<Type, MinecraftProfileTexture>> profileTextures = Maps.newHashMap();
    private Map<UUID, Map<Type, ResourceLocation>> skinCache = Maps.newHashMap();
    private List<ISkinModifier> skinModifiers = Lists.newArrayList();

    private SkinResourceManager resources = new SkinResourceManager();
    private ExecutorService executor = Executors.newCachedThreadPool();

    public HDSkinManager() {}

    public Optional<ResourceLocation> getSkinLocation(GameProfile profile1, final Type type, boolean loadIfAbsent) {
        if (!enabled)
            return Optional.absent();

        ResourceLocation skin = this.resources.getPlayerTexture(profile1, type);
        if (skin != null)
            return Optional.of(skin);

        // try to recreate a broken gameprofile
        // happens when server sends a random profile with skin and displayname
        Property prop = Iterables.getFirst(profile1.getProperties().get("textures"), null);
        if (prop != null && Strings.isNullOrEmpty(prop.getValue())) {
            JsonObject obj = new Gson().fromJson(new String(Base64.decodeBase64(prop.getValue())), JsonObject.class);
            // why are plugins sending a json null?
            if (obj != null) {
                String name = null;
                // this should be optional
                if (obj.has("profileName")) {
                    name = obj.get("profileName").getAsString();
                }
                // this is required
                if (obj.has("profileId")) {
                    UUID uuid = UUIDTypeAdapter.fromString(obj.get("profileId").getAsString());
                    profile1 = new GameProfile(uuid, name);
                }
            }
        }
        final GameProfile profile = profile1;

        if (!this.skinCache.containsKey(profile.getId())) {
            this.skinCache.put(profile.getId(), Maps.<Type, ResourceLocation> newHashMap());
        }

        skin = this.skinCache.get(profile.getId()).get(type);
        if (skin == null) {
            if (loadIfAbsent) {
                skinCache.get(profile.getId()).put(type, LOADING);
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        loadTexture(profile, type, new SkinAvailableCallback() {
                            @Override
                            public void skinAvailable(Type type, ResourceLocation location, MinecraftProfileTexture profileTexture) {
                                skinCache.get(profile.getId()).put(type, location);
                            }
                        });
                    }
                });
            }
            return Optional.absent();
        }

        return skin == LOADING ? Optional.<ResourceLocation> absent() : Optional.of(skin);

    }

    private void loadTexture(GameProfile profile, final Type type, final SkinAvailableCallback callback) {
        if (profile != null && profile.getId() != null) {
            Map<Type, MinecraftProfileTexture> data = getProfileData(profile);
            final MinecraftProfileTexture texture = data.get(type);
            if (texture == null) {
                return;
            }

            String skinDir = "hd" + type.toString().toLowerCase() + "s/";
            final ResourceLocation skin = new ResourceLocation(skinDir + texture.getHash());
            File file1 = new File(new File("assets/" + skinDir), texture.getHash().substring(0, 2));
            File file2 = new File(file1, texture.getHash());
            final IImageBuffer imagebufferdownload = new ImageBufferDownloadHD();
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file2, texture.getUrl(),
                    DefaultPlayerSkin.getDefaultSkinLegacy(),
                    new IImageBuffer() {
                        @Override
                        public BufferedImage parseUserSkin(BufferedImage image) {
                            return imagebufferdownload.parseUserSkin(image);
                        }

                        @Override
                        public void skinAvailable() {
                            imagebufferdownload.skinAvailable();
                            if (callback != null) {
                                callback.skinAvailable(type, skin, texture);
                            }
                        }
                    });

            Minecraft.getMinecraft().getTextureManager().loadTexture(skin, threaddownloadimagedata);
        }
    }

    public Map<Type, MinecraftProfileTexture> getProfileData(GameProfile profile) {
        if (!enabled)
            return ImmutableMap.of();
        Map<Type, MinecraftProfileTexture> textures = this.profileTextures.get(profile.getId());
        if (textures == null) {

            String uuid = UUIDTypeAdapter.fromUUID(profile.getId());

            ImmutableMap.Builder<Type, MinecraftProfileTexture> builder = ImmutableMap.builder();
            for (Type type : Type.values()) {
                String url = getCustomTextureURLForId(type, uuid);
                String hash = getTextureHash(type, uuid);

                builder.put(type, new HDProfileTexture(url, hash, null));
            }

            textures = builder.build();
            this.profileTextures.put(profile.getId(), textures);
        }
        return textures;
    }

    private static Map<Type, MinecraftProfileTexture> getTexturesForProfile(GameProfile profile) {
        LiteLoaderLogger.debug("Get textures for " + profile.getId(), new Object[0]);

        Minecraft minecraft = Minecraft.getMinecraft();
        MinecraftSessionService sessionService = minecraft.getSessionService();
        Map<Type, MinecraftProfileTexture> textures = null;

        try {
            textures = sessionService.getTextures(profile, true);
        } catch (InsecureTextureException var6) {
            textures = sessionService.getTextures(profile, false);
        }

        if ((textures == null || textures.isEmpty())
                && profile.getId().equals(minecraft.getSession().getProfile().getId())) {
            textures = sessionService.getTextures(sessionService.fillProfileProperties(profile, false), false);
        }
        return textures;
    }

    public void setSkinUrl(String skinUrl) {
        this.skinUrl = skinUrl;
    }

    public void setGatewayURL(String gatewayURL) {
        this.gatewayUrl = gatewayURL;
    }

    public String getSkinUrl() {
        return String.format("http://%s/", skinUrl);
    }

    public String getGatewayUrl() {
        return String.format("http://%s/", gatewayUrl);
    }

    public String getCustomTextureURLForId(Type type, String uuid, boolean gateway) {
        String server = gateway ? gatewayUrl : skinUrl;
        String path = type.toString().toLowerCase() + "s";
        return String.format("http://%s/%s/%s.png", server, path, uuid);
    }

    public String getCustomTextureURLForId(Type type, String uuid) {
        return getCustomTextureURLForId(type, uuid, false);
    }

    private String getTextureHash(Type type, String uuid) {
        try {
            URL url = new URL(getCustomTextureURLForId(type, uuid) + ".md5");
            return Resources.asCharSource(url, Charsets.UTF_8).readFirstLine();
        } catch (IOException e) {
            return null;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static PreviewTexture getPreviewTexture(ResourceLocation skinResource, GameProfile profile) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject skinTexture = textureManager.getTexture(skinResource);
        Map<Type, MinecraftProfileTexture> textures = getTexturesForProfile(profile);
        MinecraftProfileTexture skin = textures.get(Type.SKIN);
        if (skin != null) {
            String url = INSTANCE.getCustomTextureURLForId(Type.SKIN, UUIDTypeAdapter.fromUUID(profile.getId()), true);
            skinTexture = new PreviewTexture(url, DefaultPlayerSkin.getDefaultSkin(profile.getId()), new ImageBufferDownloadHD());
            textureManager.loadTexture(skinResource, skinTexture);
        }
        return (PreviewTexture) skinTexture;

    }

    public static void clearSkinCache() {
        LiteLoaderLogger.info("Clearing local player skin cache", new Object[0]);

        try {
            FileUtils.deleteDirectory(new File(LiteLoader.getAssetsDirectory(), "skins"));
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    public void addSkinModifier(ISkinModifier modifier) {
        skinModifiers.add(modifier);
    }

    @Nullable
    public ResourceLocation getConvertedSkin(@Nullable ResourceLocation res) {
        return resources.getConvertedResource(res);
    }

    public void convertSkin(BufferedImage image, Graphics dest) {
        for (ISkinModifier skin : skinModifiers) {
            skin.convertSkin(image, dest);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resources.onResourceManagerReload(resourceManager);
    }
}
