package com.minelittlepony.client.pony;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The PonyManager is responsible for reading and recoding all the pony data associated with an entity of skin.
 *
 */
public class PonyManager implements IPonyManager, SimpleSynchronousResourceReloadListener {

    private static final Identifier ID = new Identifier("minelittlepony", "background_ponies");
    public static final Identifier BACKGROUND_PONIES = new Identifier("minelittlepony", "textures/entity/pony");

    private final PonyConfig config;

    private final LoadingCache<Identifier, IPony> poniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(Pony::new));

    public PonyManager(PonyConfig config) {
        this.config = config;
    }

    @Override
    public IPony getPony(Identifier resource) {
        try {
            return poniesCache.get(resource);
        } catch (ExecutionException e) {
            return new Pony(resource, Memoize.of(PonyData.NULL));
        }
    }

    @Override
    public IPony getPony(PlayerEntity player) {
        if (player.getGameProfile() == null) {
            return getDefaultPony(player.getUuid());
        }

        Identifier skin = getSkin(player);
        UUID uuid = player.getGameProfile().getId();

        if (skin == null) {
            return getDefaultPony(uuid);
        }

        if (player instanceof IPonyManager.ForcedPony) {
            return getPony(skin);
        }

        return getPony(skin, uuid);
    }

    @Nullable
    private Identifier getSkin(PlayerEntity player) {
        if (player instanceof AbstractClientPlayerEntity) {
            return ((AbstractClientPlayerEntity)player).getSkinTexture();
        }

        return null;
    }

    @Override
    public IPony getPony(Identifier resource, UUID uuid) {
        IPony pony = getPony(resource);

        if (config.ponyLevel.get() == PonyLevel.PONIES && pony.getMetadata().getRace().isHuman()) {
            return getBackgroundPony(uuid);
        }

        return pony;
    }

    @Override
    public IPony getDefaultPony(UUID uuid) {
        if (config.ponyLevel.get() != PonyLevel.PONIES) {
            return ((Pony)getPony(DefaultSkinHelper.getTexture(uuid))).defaulted();
        }

        return getBackgroundPony(uuid);
    }

    @Override
    public IPony getBackgroundPony(UUID uuid) {
        return ((Pony)getPony(MineLittlePony.getInstance().getVariatedTextures().get(BACKGROUND_PONIES, uuid))).defaulted();
    }

    @Override
    public void removePony(Identifier resource) {
        poniesCache.invalidate(resource);
    }

    public void clearCache() {
        MineLittlePony.logger.info("Flushed {} cached ponies.", poniesCache.size());
        poniesCache.invalidateAll();
    }

    @Override
    public void reload(ResourceManager var1) {
        clearCache();
        PonySkullRenderer.reload();
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

}
