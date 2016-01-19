package com.voxelmodpack.hdskins;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Optional;
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

    private Map<GameProfile, ResourceLocation> skinCache = Maps.newHashMap();
    private List<ISkinModifier> skinModifiers = Lists.newArrayList();

    private HDSkinManager() {}

    public static Optional<ResourceLocation> getSkin(final GameProfile profile) {
        return INSTANCE.getSkinLocation(profile);
    }

    private Optional<ResourceLocation> getSkinLocation(final GameProfile profile) {
        if (!enabled)
            return Optional.absent();
        ResourceLocation skin = skinCache.get(profile);
        if (skin == null) {
            skinCache.put(profile, LOADING);
            loadTexture(profile, Type.SKIN, new SkinAvailableCallback() {
                @Override
                public void skinAvailable(Type p_180521_1_, ResourceLocation location, MinecraftProfileTexture profileTexture) {
                    skinCache.put(profile, location);
                }
            });
            return Optional.absent();
        }
        return skin == LOADING ? Optional.<ResourceLocation> absent() : Optional.of(skin);
    }

    private void loadTexture(GameProfile profile, final Type type, final SkinAvailableCallback callback) {
        if (profile != null && profile.getId() != null) {
            String uuid = UUIDTypeAdapter.fromUUID(profile.getId());
            String url = getCustomSkinURLForId(uuid, true);
            // TODO use cache
            final MinecraftProfileTexture texture = new MinecraftProfileTexture(url, null);
            final ResourceLocation skin = new ResourceLocation("skins/" + texture.getHash());
            File file1 = new File(skinCacheDir, texture.getHash().substring(0, 2));
            @SuppressWarnings("unused")
            File file2 = new File(file1, texture.getHash());
            final IImageBuffer imagebufferdownload = new ImageBufferDownloadHD();
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(null, url,
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

        if (skinTexture == null) {
            Map<Type, MinecraftProfileTexture> textures = getTexturesForProfile(profile);
            MinecraftProfileTexture skin = textures.get(Type.SKIN);
            if (skin != null) {
                String url = skin.getUrl();
                skinTexture = new PreviewTexture(url, DefaultPlayerSkin.getDefaultSkin(profile.getId()), new ImageBufferDownloadHD());
                textureManager.loadTexture(skinResource, skinTexture);
            }
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
