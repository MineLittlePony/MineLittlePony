package com.voxelmodpack.hdskins.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SkinResourceManager implements IResourceManagerReloadListener {
    private final Gson GSON = new Gson();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Map<UUID, Skin> uuidSkins = Maps.newHashMap();
    private Map<String, Skin> namedSkins = Maps.newHashMap();
    private Map<ResourceLocation, Future<ResourceLocation>> inProgress = Maps.newHashMap();
    private Map<ResourceLocation, ResourceLocation> converted = Maps.newHashMap();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        uuidSkins.clear();
        namedSkins.clear();
        executor.shutdownNow();
        executor = Executors.newSingleThreadExecutor();
        inProgress.clear();
        converted.clear();

        for (String domain : resourceManager.getResourceDomains()) {
            try {
                for (IResource res : resourceManager.getAllResources(new ResourceLocation(domain, "textures/skins/skins.json"))) {
                    try {
                        for (Skin s : getSkinData(res.getInputStream())) {
                            if (s.uuid != null) {
                                uuidSkins.put(s.uuid, s);
                            }

                            if (s.name != null) {
                                namedSkins.put(s.name, s);
                            }
                        }
                    } catch (JsonParseException je) {
                        LiteLoaderLogger.warning(je, "Invalid skins.json in %s", res.getResourcePackName());
                    }
                }
            } catch (IOException ignored) { }
        }
    }

    private List<Skin> getSkinData(InputStream stream) {
        try {
            return GSON.fromJson(new InputStreamReader(stream), SkinData.class).skins;
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Nullable
    public ResourceLocation getPlayerTexture(GameProfile profile, Type type) {
        if (type == Type.SKIN) {
            Skin skin = getSkin(profile);
            if (skin != null) {
                return getConvertedResource(skin.getTexture());
            }
        }

        return null; // not supported
    }

    /**
     * Convert older resources to a newer format.
     *
     * @param res The skin resource to convert
     * @return The converted resource
     */
    @Nullable
    public ResourceLocation getConvertedResource(@Nullable ResourceLocation res) {
        loadSkinResource(res);
        return converted.get(res);
    }

    /**
     * read and convert in a new thread
     */
    private void loadSkinResource(@Nullable ResourceLocation res) {
        if (res != null) {
            if (!inProgress.containsKey(res)) {
                inProgress.put(res, scheduleConvertion(res));
            }
        }
    }

    private Future<ResourceLocation> scheduleConvertion(ResourceLocation res) {
        return CompletableFuture.supplyAsync(new ImageLoader(res), executor).whenComplete((result, error) -> {
            if (result == null) {
                result = res;
                LogManager.getLogger().warn("Errored while processing {}. Using original.", res, error);
            }

            converted.put(res, result);
        });
    }

    @Nullable
    private Skin getSkin(GameProfile profile) {
        Skin skin = uuidSkins.get(profile.getId());
        if (skin != null) {
            return skin;
        }

        return namedSkins.get(profile.getName());
    }
}
