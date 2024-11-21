package com.minelittlepony.client;

import com.google.common.base.MoreObjects;
import com.google.common.cache.*;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.events.PonySkinResolver;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PonyManagerImpl implements PonyManager, SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = MineLittlePony.id("background_ponies");

    private final PonyConfig config;

    private final LoadingCache<Key, Pony> poniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(key -> new Pony(key.texture(), PonyDataLoader.parse(key.texture(), key.defaulted()))));
    private final WeakHashMap<UUID, Pony> playerPonies = new WeakHashMap<>();

    record Key(Identifier texture, boolean defaulted) {}

    public PonyManagerImpl(PonyConfig config) {
        this.config = config;
        Instance.instance = this;
    }

    private Pony loadPony(Identifier resource, boolean defaulted) {
        try {
            return poniesCache.get(new Key(resource, defaulted));
        } catch (ExecutionException e) {
            return new Pony(resource, PonyDataLoader.NULL);
        }
    }

    @Override
    public Pony getPony(PlayerEntity player) {
        final UUID id = player instanceof ForcedPony ? null : player.getGameProfile() == null || player.getGameProfile().getId() == null ? player.getUuid() : player.getGameProfile().getId();

        Pony pony;
        if (player instanceof ServerPlayerEntity && id != null) {
            pony = playerPonies.get(id);
            if (pony != null) {
                return pony;
            }
        }

        @Nullable
        Identifier skin = getSkin(player);
        if (skin != null) {
            skin = MoreObjects.firstNonNull(PonySkinResolver.EVENT.invoker().onPonySkinResolving(player, s -> getPony(s, id), skin), skin);
        }
        pony = getPony(skin, id);
        if (!(player instanceof ServerPlayerEntity) && id != null) {
            playerPonies.put(id, pony);
        }
        return pony;
    }

    @Override
    public Optional<Pony> getPony(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            return Optional.of(getPony(player));
        }
        @Nullable
        Identifier skin = getSkin(entity);
        if (skin != null) {
            skin = MoreObjects.firstNonNull(PonySkinResolver.EVENT.invoker().onPonySkinResolving(entity, s -> getPony(s, null), skin), skin);
        }
        return skin == null ? Optional.empty() : Optional.of(getPony(skin, null));
    }

    @Override
    public Pony getPony(@Nullable Identifier resource, @Nullable UUID uuid) {
        if (resource == null) {
            return uuid == null ? loadPony(DefaultSkinHelper.getTexture(), true) : getBackgroundPony(uuid);
        }

        Pony pony = loadPony(resource, false);

        if (uuid != null && PonyConfig.getInstance().ponyLevel.get() == PonyLevel.PONIES && pony.metadata().race().isHuman()) {
            return getBackgroundPony(uuid);
        }
        return pony;
    }

    @Override
    public Pony getBackgroundPony(@Nullable UUID uuid) {
        if (config.ponyLevel.get() == PonyLevel.PONIES) {
            return loadPony(MineLittlePony.getInstance().getVariatedTextures().get(VariatedTextureSupplier.BACKGROUND_PONIES_POOL, uuid).orElse(DefaultSkinHelper.getSkinTextures(uuid).texture()), true);
        }
        return loadPony(DefaultSkinHelper.getSkinTextures(uuid).texture(), true);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private Identifier getSkin(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            if (player.getGameProfile() != null && player instanceof AbstractClientPlayerEntity clientPlayer) {
                return clientPlayer.getSkinTextures().texture();
            }
        } else {
            if (MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(entity) instanceof LivingEntityRenderer renderer) {
                return renderer.getTexture((LivingEntityRenderState)renderer.getAndUpdateRenderState(entity, 1));
            }
        }

        return null;
    }

    public void removePony(Identifier resource) {
        poniesCache.invalidate(resource);
    }

    public void clearCache() {
        MineLittlePony.LOGGER.info("Turned {} cached ponies into cupcakes.", poniesCache.size());
        poniesCache.invalidateAll();
    }

    @Override
    public void reload(ResourceManager manager) {
        clearCache();
        PonySkullRenderer.INSTANCE.reload();
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
