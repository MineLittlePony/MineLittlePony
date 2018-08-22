package com.voxelmodpack.hdskins;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.resource.SkinResourceManager;
import com.voxelmodpack.hdskins.skins.AsyncCacheLoader;
import com.voxelmodpack.hdskins.skins.BethlehemSkinServer;
import com.voxelmodpack.hdskins.skins.LegacySkinServer;
import com.voxelmodpack.hdskins.skins.ServerType;
import com.voxelmodpack.hdskins.skins.SkinServer;
import com.voxelmodpack.hdskins.skins.ValhallaSkinServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class HDSkinManager implements IResourceManagerReloadListener {

    private static final Logger logger = LogManager.getLogger();

    public static final ExecutorService skinUploadExecutor = Executors.newSingleThreadExecutor();
    public static final ExecutorService skinDownloadExecutor = Executors.newFixedThreadPool(8);
    public static final CloseableHttpClient httpClient = HttpClients.createSystem();

    private static final ResourceLocation LOADING = new ResourceLocation("LOADING");

    public static final HDSkinManager INSTANCE = new HDSkinManager();

    private boolean enabled = true;

    private List<ISkinCacheClearListener> clearListeners = Lists.newArrayList();

    private BiMap<String, Class<? extends SkinServer>> skinServerTypes = HashBiMap.create(2);
    private List<SkinServer> skinServers = Lists.newArrayList();

    private Map<UUID, Map<Type, ResourceLocation>> skinCache = Maps.newHashMap();

    private LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skins = CacheBuilder.newBuilder()
            .initialCapacity(20)
            .maximumSize(100)
            .expireAfterWrite(4, TimeUnit.HOURS)
            .build(AsyncCacheLoader.create(CacheLoader.from(this::loadProfileData), Collections.emptyMap(), skinDownloadExecutor));

    private List<ISkinModifier> skinModifiers = Lists.newArrayList();

    private SkinResourceManager resources = new SkinResourceManager();
    //    private ExecutorService executor = Executors.newCachedThreadPool();

    private Function<List<SkinServer>, GuiSkins> skinsGuiFunc = GuiSkins::new;

    private HDSkinManager() {

        // register default skin server types
        addSkinServerType(LegacySkinServer.class);
        addSkinServerType(ValhallaSkinServer.class);
        addSkinServerType(BethlehemSkinServer.class);
    }

    public void setSkinsGui(Function<List<SkinServer>, GuiSkins> skinsGuiFunc) {
        Preconditions.checkNotNull(skinsGuiFunc, "skinsGuiFunc");
        this.skinsGuiFunc = skinsGuiFunc;
    }

    public GuiSkins createSkinsGui() {
        return skinsGuiFunc.apply(ImmutableList.copyOf(this.skinServers));
    }

    public Optional<ResourceLocation> getSkinLocation(GameProfile profile1, final Type type, boolean loadIfAbsent) {
        if (!enabled) {
            return Optional.empty();
        }

        ResourceLocation skin = this.resources.getPlayerTexture(profile1, type);
        if (skin != null) {
            return Optional.of(skin);
        }

        // try to recreate a broken gameprofile
        // happens when server sends a random profile with skin and displayname
        Property textures = Iterables.getFirst(profile1.getProperties().get("textures"), null);
        if (textures != null) {
            String json = new String(Base64.getDecoder().decode(textures.getValue()), StandardCharsets.UTF_8);
            MinecraftTexturesPayload texturePayload = SkinServer.gson.fromJson(json, MinecraftTexturesPayload.class);
            if (texturePayload != null) {
                // name is optional
                String name = texturePayload.getProfileName();
                UUID uuid = texturePayload.getProfileId();
                // uuid is required
                if (uuid != null) {
                    profile1 = new GameProfile(uuid, name);
                }

                // probably uses this texture for a reason. Don't mess with it.
                if (!texturePayload.getTextures().isEmpty() && texturePayload.getProfileId() == null) {
                    return Optional.empty();
                }
            }
        }
        final GameProfile profile = profile1;

        // cannot get texture without id!
        if (profile.getId() == null) {
            return Optional.empty();
        }

        if (!this.skinCache.containsKey(profile.getId())) {
            this.skinCache.put(profile.getId(), Maps.newHashMap());
        }

        skin = this.skinCache.get(profile.getId()).get(type);
        if (skin == null) {
            if (loadIfAbsent && getProfileData(profile).containsKey(type)) {
                skinCache.get(profile.getId()).put(type, LOADING);
                loadTexture(profile, type, (t, loc, tex) -> skinCache.get(profile.getId()).put(t, loc));
            }
            return Optional.empty();
        }
        return skin == LOADING ? Optional.empty() : Optional.of(skin);
    }

    private void loadTexture(GameProfile profile, final Type type, final SkinAvailableCallback callback) {
        if (profile.getId() == null) {
            return;
        }

        String skinDir = type.toString().toLowerCase() + "s/";

        final MinecraftProfileTexture texture = getProfileData(profile).get(type);
        final ResourceLocation resource = new ResourceLocation("hdskins", skinDir + texture.getHash());

        ISkinAvailableCallback buffs = new ImageBufferDownloadHD(type, () -> {
            callback.skinAvailable(type, resource, texture);
        });

        // schedule texture loading on the main thread.
        TextureLoader.loadTexture(resource, new ThreadDownloadImageETag(
                new File(LiteLoader.getAssetsDirectory(), "hd/" + skinDir + texture.getHash().substring(0, 2) + "/" + texture.getHash()),
                texture.getUrl(),
                DefaultPlayerSkin.getDefaultSkinLegacy(),
                buffs));
    }

    private Map<Type, MinecraftProfileTexture> loadProfileData(GameProfile profile) {
        Map<Type, MinecraftProfileTexture> textures = Maps.newEnumMap(Type.class);
        for (SkinServer server : skinServers) {
            try {
                server.loadProfileData(profile).getTextures().forEach(textures::putIfAbsent);
                if (textures.size() == Type.values().length) {
                    break;
                }
            } catch (IOException e) {
                logger.trace(e);
            }

        }
        return textures;
    }

    public Map<Type, MinecraftProfileTexture> getProfileData(GameProfile profile) {
        boolean was = !skins.asMap().containsKey(profile);
        Map<Type, MinecraftProfileTexture> textures = skins.getUnchecked(profile);
        // This is the initial value. Refreshing will load it asynchronously.
        if (was) {
            skins.refresh(profile);
        }
        return textures;
    }

    public void addSkinServerType(Class<? extends SkinServer> type) {
        Preconditions.checkArgument(!type.isInterface(), "type cannot be an interface");
        Preconditions.checkArgument(!Modifier.isAbstract(type.getModifiers()), "type cannot be abstract");
        ServerType st = type.getAnnotation(ServerType.class);
        if (st == null) {
            throw new IllegalArgumentException("class is not annotated with @ServerType");
        }
        this.skinServerTypes.put(st.value(), type);
    }

    public Class<? extends SkinServer> getSkinServerClass(String type) {
        return this.skinServerTypes.get(type);
    }

    public void addSkinServer(SkinServer skinServer) {
        this.skinServers.add(skinServer);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addClearListener(ISkinCacheClearListener listener) {
        clearListeners.add(listener);
    }

    public void clearSkinCache() {
        LiteLoaderLogger.info("Clearing local player skin cache");

        try {
            FileUtils.deleteDirectory(new File(LiteLoader.getAssetsDirectory(), "skins"));
            FileUtils.deleteDirectory(new File(LiteLoader.getAssetsDirectory(), "hd"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextureManager textures = Minecraft.getMinecraft().getTextureManager();
        skinCache.values().stream()
                .flatMap(m -> m.values().stream())
                .forEach(textures::deleteTexture);
        skinCache.clear();
        skins.invalidateAll();

        clearListeners = clearListeners.stream()
                .filter(this::onSkinCacheCleared)
                .collect(Collectors.toList());
    }

    private boolean onSkinCacheCleared(ISkinCacheClearListener callback) {
        try {
            return callback.onSkinCacheCleared();
        } catch (Exception e) {
            logger.warn("Exception encountered calling skin listener '{}'. It will be removed.", callback.getClass().getName(), e);
            return false;
        }
    }

    public void addSkinModifier(ISkinModifier modifier) {
        skinModifiers.add(modifier);
    }

    public ResourceLocation getConvertedSkin(ResourceLocation res) {
        ResourceLocation loc = resources.getConvertedResource(res);
        return loc == null ? res : loc;
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
