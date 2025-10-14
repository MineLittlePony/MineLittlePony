package com.minelittlepony.server;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.google.common.cache.*;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.Mats;
import com.mojang.authlib.GameProfile;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ServerPonyManager implements PonyManager {
    static final Pony NULL_PONY = new Pony(Identifier.ofVanilla("null"), () -> Optional.of(PonyData.NULL));

    private final LoadingCache<Identifier, Pony> poniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
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
        if (entity instanceof PlayerEntity player) {
            return Optional.ofNullable(getPony(player));
        }
        return Optional.empty();
    }

    @Override
    public Pony getBackgroundPony(UUID uuid) {
        return NULL_PONY;
    }

    @Override
    public Pony getPony(PlayerLikeEntity player) {
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
    private static GameProfile getProfile(PlayerLikeEntity player) {
        if (player instanceof ForcedPony) {
            return null;
        }

        ProfileComponent profile = player.get(DataComponentTypes.PROFILE);
        if (profile != null) {
            return profile.getGameProfile();
        }

        if (player instanceof PlayerEntity p && p.getGameProfile() != null) {
            return p.getGameProfile();
        }

        return null;
    }
}
