package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.minelittlepony.api.pony.Pony;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PlayerTextureSupplier {
    public static <T extends LivingEntity> TextureSupplier<T> create(TextureSupplier<T> fallback) {
        Function<String, CompletableFuture<Identifier>> customNameCache = Util.memoize(name -> {
            return MinecraftClient.getInstance().getPlayerSkinCache().getFuture(ProfileComponent.ofDynamic(name)).thenApply(entry -> {
                return entry
                        .map(i -> i.getTextures().body().texturePath())
                        .filter(skin -> !Pony.getManager().getPony(skin).race().isHuman())
                        .orElse(null);
            });
        });
        return entity -> {
            Identifier override = entity.hasCustomName() ? customNameCache.apply(entity.getCustomName().getString()).getNow(null) : null;
            if (override != null) {
                return override;
            }
            return fallback.apply(entity);
        };
    }
}
