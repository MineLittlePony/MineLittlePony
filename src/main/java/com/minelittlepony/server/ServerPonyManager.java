package com.minelittlepony.server;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;

import org.jetbrains.annotations.Nullable;

import com.google.common.cache.*;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.Mats;
import com.mojang.authlib.GameProfile;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ServerPonyManager implements PonyManager {
    static final Pony NULL_PONY = new Pony(Identifier.withDefaultNamespace("null"), () -> Optional.of(PonyData.NULL));

    private final LoadingCache<Identifier, Pony> poniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofSeconds(30))
            .build(CacheLoader.from(resource -> {
                return new Pony(resource, load(consumer -> {
                    CompletableFuture.runAsync(() -> {
                        try {
                            consumer.accept(new PonyData(Mats.createMat(URI.create(resource.toString()).toURL()), false));
                        } catch (IOException e) {
                            consumer.accept(PonyData.NULL);
                        }
                    });
                }));
            }));

    private static <T> Supplier<Optional<T>> load(Consumer<Consumer<T>> factory) {
        return new Supplier<Optional<T>>() {
            Optional<T> value = Optional.empty();
            boolean loadRequested;
            @Override
            public Optional<T> get() {
                synchronized (this) {
                    if (!loadRequested) {
                        loadRequested = true;
                        factory.accept(value -> {
                            this.value = Optional.ofNullable(value);
                        });
                    }
                }
                return value;
            }
        };
    }

    @Override
    public Optional<Pony> getPony(LivingEntity entity) {
        if (entity instanceof Player player) {
            return Optional.ofNullable(getPony(player));
        }
        return Optional.empty();
    }

    @Override
    public Pony getBackgroundPony(UUID uuid) {
        return NULL_PONY;
    }

    @Override
    public Pony getPony(Avatar player) {
        return getPony(SkinsProxy.getInstance().getSkinTexture(getProfile(player)), null);
    }

    @Override
    public Pony getPony(@Nullable Identifier resource, @Nullable UUID uuid) {
        if (resource != null && (resource.getNamespace().equals("http") || resource.getNamespace().equals("https"))) {
            return poniesCache.getUnchecked(resource);
        }
        return NULL_PONY;
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
