package com.voxelmodpack.hdskins;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.minelittlepony.gui.IconicButton;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.hdskins.ducks.INetworkPlayerInfo;
import com.voxelmodpack.hdskins.gui.Feature;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.resources.SkinResourceManager;
import com.voxelmodpack.hdskins.resources.TextureLoader;
import com.voxelmodpack.hdskins.resources.texture.ImageBufferDownloadHD;
import com.voxelmodpack.hdskins.server.SkinServer;
import com.voxelmodpack.hdskins.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public final class HDSkinManager implements IResourceManagerReloadListener {

    public static final Logger logger = LogManager.getLogger();

    public static final ExecutorService skinUploadExecutor = Executors.newSingleThreadExecutor();
    public static final ExecutorService skinDownloadExecutor = Executors.newFixedThreadPool(8);

    public static final HDSkinManager INSTANCE = new HDSkinManager();

    private List<ISkinCacheClearListener> clearListeners = Lists.newArrayList();

    private List<SkinServer> skinServers = Lists.newArrayList();

    private LoadingCache<GameProfile, CompletableFuture<Map<Type, MinecraftProfileTexture>>> skins = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build(CacheLoader.from(this::loadProfileData));

    private List<ISkinModifier> skinModifiers = Lists.newArrayList();
    private List<ISkinParser> skinParsers = Lists.newArrayList();

    private SkinResourceManager resources = new SkinResourceManager();

    private Function<List<SkinServer>, GuiSkins> skinsGuiFunc = GuiSkins::new;

    private HDSkinManager() {
    }

    public void setSkinsGui(Function<List<SkinServer>, GuiSkins> skinsGuiFunc) {
        Preconditions.checkNotNull(skinsGuiFunc, "skinsGuiFunc");
        this.skinsGuiFunc = skinsGuiFunc;
    }

    public GuiSkins createSkinsGui() {
        return skinsGuiFunc.apply(ImmutableList.copyOf(this.skinServers));
    }

    public void displaySkinningGui(GuiScreen screen, Consumer<GuiButton> buttons) {
        if (screen instanceof GuiMainMenu) {
            buttons.accept(new IconicButton(screen.width - 50, screen.height - 50, sender -> {
                Minecraft.getMinecraft().displayGuiScreen(HDSkinManager.INSTANCE.createSkinsGui());
            }).setIcon(new ItemStack(Items.LEATHER_LEGGINGS), 0x3c5dcb));
        }
    }

    private CompletableFuture<Map<Type, MinecraftProfileTexture>> loadProfileData(GameProfile profile) {

        return CompletableFuture.supplyAsync(() -> {
            if (profile.getId() == null) {
                return Collections.emptyMap();
            }

            Map<Type, MinecraftProfileTexture> textureMap = Maps.newEnumMap(Type.class);

            for (SkinServer server : skinServers) {
                try {
                    if (!server.getFeatures().contains(Feature.SYNTHETIC)) {
                        server.loadProfileData(profile).getTextures().forEach((k, v) -> {
                            try {
                                Type t = Type.valueOf(k.toUpperCase(Locale.ROOT));
                                if (t != null) {
                                    textureMap.put(t, v);
                                }
                            } catch (Exception e) {}
                        });
                        if (textureMap.size() == Type.values().length) {
                            break;
                        }
                    }
                } catch (IOException | AuthenticationException e) {
                    logger.trace(e);
                }

            }
            return textureMap;
        }, skinDownloadExecutor);
    }

    public CompletableFuture<Map<Type, MinecraftProfileTexture>> loadProfileTextures(GameProfile profile) {
        try {
            // try to recreate a broken gameprofile
            // happens when server sends a random profile with skin and displayname
            Property textures = Iterables.getFirst(profile.getProperties().get("textures"), null);
            if (textures != null) {
                String json = new String(Base64.getDecoder().decode(textures.getValue()), StandardCharsets.UTF_8);
                MinecraftTexturesPayload texturePayload = MoreHttpResponses.GSON.fromJson(json, MinecraftTexturesPayload.class);
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
        } catch (Exception e) {
            if (profile.getId() == null) {
                // Something broke server-side probably
                logger.warn("{} had a null UUID and was unable to recreate it from texture profile.", profile.getName(), e);
                return CompletableFuture.completedFuture(Collections.emptyMap());
            }
        }
        return skins.getUnchecked(profile);
    }

    public void fetchAndLoadSkins(GameProfile profile, SkinManager.SkinAvailableCallback callback) {
        loadProfileTextures(profile).thenAcceptAsync(m -> m.forEach((type, pp) -> {
            loadTexture(type, pp, (typeIn, location, profileTexture) -> {
                parseSkin(profile, typeIn, location, profileTexture)
                        .thenRun(() -> callback.skinAvailable(typeIn, location, profileTexture));
            });
        }), Minecraft.getMinecraft()::addScheduledTask);
    }

    public ResourceLocation loadTexture(Type type, MinecraftProfileTexture texture, @Nullable SkinManager.SkinAvailableCallback callback) {
        String skinDir = type.toString().toLowerCase() + "s/";

        final ResourceLocation resource = new ResourceLocation("hdskins", skinDir + texture.getHash());
        ITextureObject texObj = Minecraft.getMinecraft().getTextureManager().getTexture(resource);

        //noinspection ConstantConditions
        if (texObj != null) {
            if (callback != null) {
                callback.skinAvailable(type, resource, texture);
            }
        } else {

            // schedule texture loading on the main thread.
            TextureLoader.loadTexture(resource, new ThreadDownloadImageData(
                    new File(LiteLoader.getAssetsDirectory(), "hd/" + skinDir + texture.getHash().substring(0, 2) + "/" + texture.getHash()),
                    texture.getUrl(),
                    DefaultPlayerSkin.getDefaultSkinLegacy(),
                    new ImageBufferDownloadHD(type, () -> {
                        if (callback != null) {
                            callback.skinAvailable(type, resource, texture);
                        }
                    })));
        }

        return resource;
    }

    public Map<Type, ResourceLocation> getTextures(GameProfile profile) {
        Map<Type, ResourceLocation> map = new HashMap<>();

        for (Map.Entry<Type, MinecraftProfileTexture> e : loadProfileTextures(profile).getNow(Collections.emptyMap()).entrySet()) {
            map.put(e.getKey(), loadTexture(e.getKey(), e.getValue(), null));
        }

        return map;
    }

    void addSkinServer(SkinServer skinServer) {
        this.skinServers.add(skinServer);
    }

    public void addClearListener(ISkinCacheClearListener listener) {
        clearListeners.add(listener);
    }

    public void clearSkinCache() {
        LiteLoaderLogger.info("Clearing local player skin cache");

        FileUtils.deleteQuietly(new File(LiteLoader.getAssetsDirectory(), "hd"));

        skins.invalidateAll();
        parseSkins();
        clearListeners.removeIf(this::onSkinCacheCleared);
    }

    private boolean onSkinCacheCleared(ISkinCacheClearListener callback) {
        try {
            return !callback.onSkinCacheCleared();
        } catch (Exception e) {
            logger.warn("Exception encountered calling skin listener '{}'. It will be removed.", callback.getClass().getName(), e);
            return true;
        }
    }

    public void addSkinModifier(ISkinModifier modifier) {
        skinModifiers.add(modifier);
    }

    public void addSkinParser(ISkinParser parser) {
        skinParsers.add(parser);
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

    public void parseSkins() {
        Minecraft mc = Minecraft.getMinecraft();

        Streams.concat(getNPCs(mc), getPlayers(mc))

                // filter nulls
                .filter(Objects::nonNull)
                .map(INetworkPlayerInfo.class::cast)
                .distinct()

                // and clear skins
                .forEach(INetworkPlayerInfo::reloadTextures);

    }

    private Stream<NetworkPlayerInfo> getNPCs(Minecraft mc) {
        return MoreStreams.ofNullable(mc.world)
                .flatMap(w -> w.playerEntities.stream())
                .filter(AbstractClientPlayer.class::isInstance)
                .map(AbstractClientPlayer.class::cast)
                .map(PlayerUtil::getInfo);
    }

    private Stream<NetworkPlayerInfo> getPlayers(Minecraft mc) {
        return MoreStreams.ofNullable(mc.getConnection())
                .flatMap(a -> a.getPlayerInfoMap().stream());
    }

    public CompletableFuture<Void> parseSkin(GameProfile profile, Type type, ResourceLocation resource, MinecraftProfileTexture texture) {

        return CallableFutures.scheduleTask(() -> {

            // grab the metadata object via reflection. Object is live.
            Map<String, String> metadata = ProfileTextureUtil.getMetadata(texture);

            boolean wasNull = metadata == null;

            if (wasNull) {
                metadata = new HashMap<>();
            } else if (metadata.containsKey("model")) {
                // try to reset the model.
                metadata.put("model", VanillaModels.of(metadata.get("model")));
            }

            for (ISkinParser parser : skinParsers) {
                try {
                    parser.parse(profile, type, resource, metadata);
                } catch (Throwable t) {
                    logger.error("Exception thrown while parsing skin: ", t);
                }
            }

            if (wasNull && !metadata.isEmpty()) {
                ProfileTextureUtil.setMetadata(texture, metadata);
            }

        });
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resources.onResourceManagerReload(resourceManager);
    }
}
