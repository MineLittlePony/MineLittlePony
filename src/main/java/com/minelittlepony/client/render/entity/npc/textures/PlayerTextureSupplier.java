package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.ResolvableProfile;

import com.minelittlepony.api.pony.Pony;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PlayerTextureSupplier {
    public static <T extends LivingEntity> TextureSupplier<T> create(TextureSupplier<T> fallback) {
        Function<String, CompletableFuture<Identifier>> customNameCache = Util.memoize(name -> {
            return Minecraft.getInstance().playerSkinRenderCache().lookup(ResolvableProfile.createUnresolved(name)).thenApply(entry -> {
                return entry
                        .map(i -> i.playerSkin().body().texturePath())
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
