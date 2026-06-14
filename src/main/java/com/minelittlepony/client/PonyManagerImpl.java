package com.minelittlepony.client;

import com.google.common.base.MoreObjects;
import com.google.common.cache.*;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.events.PonySkinResolver;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.mojang.authlib.GameProfile;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PonyManagerImpl implements PonyManager, ResourceManagerReloadListener {
    public static final Identifier ID = MineLittlePony.id("background_ponies");

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
    public Pony getPony(Avatar player) {
        @Nullable
        final UUID id = getProfileId(player);

        Pony pony;
        if (player instanceof ServerPlayer && id != null) {
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
        if (!(player instanceof ServerPlayer) && id != null) {
            playerPonies.put(id, pony);
        }
        return pony;
    }

    @Override
    public Optional<Pony> getPony(LivingEntity entity) {
        if (entity instanceof Avatar player) {
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
            return uuid == null ? loadPony(DefaultPlayerSkin.getDefaultTexture(), true) : getBackgroundPony(uuid);
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
            return loadPony(MineLittlePony.getInstance().getVariatedTextures().get(VariatedTextureSupplier.BACKGROUND_PONIES_POOL).getId(uuid).orElse(DefaultPlayerSkin.get(uuid).body().texturePath()), true);
        }
        return loadPony(DefaultPlayerSkin.get(uuid).body().texturePath(), true);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private Identifier getSkin(LivingEntity entity) {
        if (entity instanceof Avatar player) {
            if (player instanceof ClientAvatarEntity clientPlayer && (player instanceof ForcedPony || getProfile(player) != null)) {
                return clientPlayer.getSkin().body().texturePath();
            }
            return null;
        }

        if (MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(entity) instanceof LivingEntityRenderer renderer) {
            return renderer.getTextureLocation((LivingEntityRenderState)renderer.createRenderState(entity, 1));
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
    public void onResourceManagerReload(ResourceManager manager) {
        clearCache();
        PonySkullRenderer.INSTANCE.reload();
    }

    @Nullable
    private static UUID getProfileId(Avatar player) {
        if (player instanceof ForcedPony) {
            return null;
        }

        ResolvableProfile profile = player.get(DataComponents.PROFILE);
        if (profile != null) {
            if (player instanceof Player p && p.getGameProfile() != null) {
                return MoreObjects.firstNonNull(profile.partialProfile().id(), MoreObjects.firstNonNull(p.getGameProfile().id(), player.getUUID()));
            }
            return MoreObjects.firstNonNull(profile.partialProfile().id(), player.getUUID());
        }

        if (player instanceof Player p && p.getGameProfile() != null) {
            return MoreObjects.firstNonNull(p.getGameProfile().id(), player.getUUID());
        }

        return null;
    }

    @Nullable
    private static GameProfile getProfile(Avatar player) {
        if (player instanceof ForcedPony) {
            return null;
        }

        ResolvableProfile profile = player.get(DataComponents.PROFILE);
        if (profile != null) {
            return profile.partialProfile();
        }

        if (player instanceof Player p && p.getGameProfile() != null) {
            return p.getGameProfile();
        }

        return null;
    }
}
