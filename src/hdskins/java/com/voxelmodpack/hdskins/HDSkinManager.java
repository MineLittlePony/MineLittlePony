package com.voxelmodpack.hdskins;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.util.UUIDTypeAdapter;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public final class HDSkinManager {

    public static final HDSkinManager INSTANCE = new HDSkinManager();
    private static final ResourceLocation LOADING = new ResourceLocation("LOADING");
    private static final File skinCacheDir = new File("assets/skins");

    private String gatewayUrl = "skinmanager.voxelmodpack.com";
    private String skinUrl = "skins.voxelmodpack.com";
    private boolean enabled = true;

    private Map<GameProfile, Map<Type, MinecraftProfileTexture>> profileTextures = Maps.newHashMap();
    private Map<GameProfile, Map<Type, ResourceLocation>> skinCache = Maps.newHashMap();
    private List<ISkinModifier> skinModifiers = Lists.newArrayList();

    private HDSkinManager() {}

    public Optional<ResourceLocation> getSkinLocation(final GameProfile profile, Type type, boolean loadIfAbsent) {
        if (!enabled)
            return Optional.absent();
        if (!this.skinCache.containsKey(profile)) {
            this.skinCache.put(profile, Maps.<Type, ResourceLocation> newHashMap());
        }
        ResourceLocation skin = this.skinCache.get(profile).get(type);
        if (skin == null) {
            if (loadIfAbsent) {
                skinCache.get(profile).put(type, LOADING);
                loadTexture(profile, type, new SkinAvailableCallback() {
                    @Override
                    public void skinAvailable(Type type, ResourceLocation location, MinecraftProfileTexture profileTexture) {
                        skinCache.get(profile).put(type, location);
                        if (!profileTextures.containsKey(profile)) {
                            profileTextures.put(profile, Maps.<Type, MinecraftProfileTexture> newHashMap());
                        }
                        profileTextures.get(profile).put(type, profileTexture);
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
            final ResourceLocation skin = new ResourceLocation("skins/" + texture.getHash());
            File file1 = new File(skinCacheDir, texture.getHash().substring(0, 2));
            @SuppressWarnings("unused")
            File file2 = new File(file1, texture.getHash());
            final IImageBuffer imagebufferdownload = new ImageBufferDownloadHD();
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(null, texture.getUrl(),
                    DefaultPlayerSkin.getDefaultSkinLegacy(),
                    new IImageBuffer() {
                        public BufferedImage parseUserSkin(BufferedImage image) {
                            return imagebufferdownload.parseUserSkin(image);
                        }

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
        Map<Type, MinecraftProfileTexture> textures = this.profileTextures.get(profile);
        if (textures == null) {
            String uuid = UUIDTypeAdapter.fromUUID(profile.getId());
            String skinUrl = getCustomSkinURLForId(uuid, false);
            String capeUrl = getCustomCloakURLForId(uuid);

            // TODO metadata (needs server support)
            textures = ImmutableMap.of(
                    Type.SKIN, new MinecraftProfileTexture(skinUrl, null),
                    Type.CAPE, new MinecraftProfileTexture(capeUrl, null));
            // this.profileTextures.put(profile, textures);
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

    public String getCustomSkinURLForId(String uuid, boolean gateway) {
        uuid = StringUtils.stripControlCodes(uuid);
        return String.format("http://%s/skins/%s.png", gateway ? gatewayUrl : skinUrl, uuid);
    }

    public String getCustomCloakURLForId(String uuid) {
        return String.format("http://%s/capes/%s.png", skinUrl, StringUtils.stripControlCodes(uuid));
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
            String url = INSTANCE.getCustomSkinURLForId(UUIDTypeAdapter.fromUUID(profile.getId()), true);
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

    public void convertSkin(BufferedImage image, Graphics dest) {
        for (ISkinModifier skin : skinModifiers) {
            skin.convertSkin(image, dest);
        }
    }
}
