package com.minelittlepony.client.pony;

import com.google.common.cache.*;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.client.render.PonyRenderDispatcher;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;
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

    private final Cache<Identifier, IPony> defaultedPoniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build();

    private final LoadingCache<Identifier, IPony> poniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(Pony::new));

    public PonyManager(PonyConfig config) {
        this.config = config;
    }

    @Override
    public Optional<IPony> getPony(@Nullable Entity entity) {
        if (entity instanceof PlayerEntity player) {
            return Optional.of(getPony(player));
        }

        if (entity instanceof LivingEntity living) {
            IPonyRenderContext<LivingEntity, ?> dispatcher = PonyRenderDispatcher.getInstance().getPonyRenderer(living);
            if (dispatcher != null) {
                return Optional.of(dispatcher.getEntityPony(living));
            }
        }

        return Optional.empty();
    }

    @Override
    public IPony getPony(Identifier resource) {
        try {
            return poniesCache.get(resource);
        } catch (ExecutionException e) {
            return new Pony(resource, PonyData.MEM_NULL, true);
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

        if (config.ponyLevel.get() == PonyLevel.PONIES && pony.metadata().getRace().isHuman()) {
            return getBackgroundPony(uuid);
        }

        return pony;
    }

    private IPony getAsDefaulted(IPony pony) {
        try {
            return defaultedPoniesCache.get(pony.texture(), () -> new Pony(pony.texture(), ((Pony)pony).memoizedData(), true));
        } catch (ExecutionException e) {
            return pony;
        }
    }

    @Override
    public IPony getDefaultPony(UUID uuid) {
        if (config.ponyLevel.get() != PonyLevel.PONIES) {
            return getAsDefaulted(getPony(DefaultSkinHelper.getTexture(uuid)));
        }

        return getBackgroundPony(uuid);
    }

    @Override
    public IPony getBackgroundPony(UUID uuid) {
        return getAsDefaulted(getPony(MineLittlePony.getInstance().getVariatedTextures().get(BACKGROUND_PONIES, uuid)));
    }

    @Override
    public void removePony(Identifier resource) {
        poniesCache.invalidate(resource);
        defaultedPoniesCache.invalidate(resource);
    }

    public void clearCache() {
        MineLittlePony.logger.info("Flushed {} cached ponies.", poniesCache.size());
        poniesCache.invalidateAll();
        defaultedPoniesCache.invalidateAll();
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
