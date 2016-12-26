package com.voxelmodpack.hdskins;

import com.google.common.base.Charsets;
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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HDSkinManager implements IResourceManagerReloadListener {

    public static final HDSkinManager INSTANCE = new HDSkinManager();
    private static final ResourceLocation LOADING = new ResourceLocation("LOADING");

    private String gatewayUrl = "skinmanager.voxelmodpack.com";
    private String skinUrl = "skins.voxelmodpack.com";
    private boolean enabled = true;

    private Map<UUID, Map<Type, MinecraftProfileTexture>> profileTextures = Maps.newHashMap();
    private Map<UUID, Map<Type, ResourceLocation>> skinCache = Maps.newHashMap();
    private List<ISkinModifier> skinModifiers = Lists.newArrayList();

    private SkinResourceManager resources = new SkinResourceManager();
    private ExecutorService executor = Executors.newCachedThreadPool();

    public HDSkinManager() {
    }

    public Optional<ResourceLocation> getSkinLocation(GameProfile profile1, final Type type, boolean loadIfAbsent) {
        if (!enabled)
            return Optional.empty();

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
            this.skinCache.put(profile.getId(), Maps.newHashMap());
        }

        skin = this.skinCache.get(profile.getId()).get(type);
        if (skin == null) {
            if (loadIfAbsent) {
                skinCache.get(profile.getId()).put(type, LOADING);
                //noinspection Convert2Lambda
                executor.submit(() -> loadTexture(profile, type, new SkinAvailableCallback() {
                    @Override
                    public void skinAvailable(Type type1, ResourceLocation location, MinecraftProfileTexture profileTexture) {
                        skinCache.get(profile.getId()).put(type1, location);
                    }
                }));
            }
            return Optional.empty();
        }

        return skin == LOADING ? Optional.empty() : Optional.of(skin);

    }

    private void loadTexture(GameProfile profile, final Type type, final SkinAvailableCallback callback) {
        if (profile != null && profile.getId() != null) {
            Map<Type, MinecraftProfileTexture> data = loadProfileData(profile);
            final MinecraftProfileTexture texture = data.get(type);
            if (texture == null) {
                return;
            }

            String skinDir = type.toString().toLowerCase() + "s/";
            final ResourceLocation skin = new ResourceLocation("hdskins", skinDir + texture.getHash());
            File file2 = new File(LiteLoader.getAssetsDirectory(), "hd" + skinDir + "/" + texture.getHash().substring(0, 2) + "/" + texture.getHash());
            final IImageBuffer imagebufferdownload = type == Type.SKIN ? new ImageBufferDownloadHD() : null;
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file2, texture.getUrl(),
                    DefaultPlayerSkin.getDefaultSkinLegacy(),
                    new IImageBuffer() {
                        @Nonnull
                        @Override
                        public BufferedImage parseUserSkin(@Nonnull BufferedImage image) {
                            if (imagebufferdownload != null)
                                return imagebufferdownload.parseUserSkin(image);
                            return image;
                        }

                        @Override
                        public void skinAvailable() {
                            if (imagebufferdownload != null) {
                                imagebufferdownload.skinAvailable();
                            }
                            if (callback != null) {
                                callback.skinAvailable(type, skin, texture);
                            }
                        }
                    });

            // schedule texture loading on the main thread.
            TextureLoader.loadTexture(skin, threaddownloadimagedata);
        }
    }

    public Optional<Map<Type, MinecraftProfileTexture>> getProfileData(GameProfile profile) {
        if (!enabled)
            return Optional.of(ImmutableMap.of());
        return Optional.ofNullable(this.profileTextures.get(profile.getId()));

    }

    private Map<Type, MinecraftProfileTexture> loadProfileData(final GameProfile profile) {
       return getProfileData(profile).orElseGet(() -> {

            String uuid = UUIDTypeAdapter.fromUUID(profile.getId());

            ImmutableMap.Builder<Type, MinecraftProfileTexture> builder = ImmutableMap.builder();
            for (Type type : Type.values()) {
                String url = getCustomTextureURLForId(type, uuid);
                String hash = getTextureHash(type, uuid);

                builder.put(type, new HDProfileTexture(url, hash, null));
            }

           Map<Type, MinecraftProfileTexture> textures = builder.build();
           this.profileTextures.put(profile.getId(), textures);
           return textures;
        });
    }

    private static Map<Type, MinecraftProfileTexture> getTexturesForProfile(GameProfile profile) {
        LiteLoaderLogger.debug("Get textures for " + profile.getId());

        Minecraft minecraft = Minecraft.getMinecraft();
        MinecraftSessionService sessionService = minecraft.getSessionService();
        Map<Type, MinecraftProfileTexture> textures;

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

    public static PreviewTexture getPreviewTexture(ResourceLocation skinResource, GameProfile profile, Type type, ResourceLocation def) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        String url = INSTANCE.getCustomTextureURLForId(type, UUIDTypeAdapter.fromUUID(profile.getId()), true);
        ITextureObject skinTexture = new PreviewTexture(url, def, type == Type.SKIN ? new ImageBufferDownloadHD() : null);
        textureManager.loadTexture(skinResource, skinTexture);

        return (PreviewTexture) skinTexture;

    }

    public static void clearSkinCache() {
        LiteLoaderLogger.info("Clearing local player skin cache");

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
