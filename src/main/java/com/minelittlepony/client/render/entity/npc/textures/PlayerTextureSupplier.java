package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.SkinsProxy;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PlayerTextureSupplier {
    public static <T extends PonyRenderState> TextureSupplier<T> create(TextureSupplier<T> fallback) {
        Function<String, CompletableFuture<Identifier>> customNameCache = Util.memoize(name -> {
            return SkullBlockEntity.fetchProfileByName(name).thenApply(profile -> {
                return profile
                        .map(p -> SkinsProxy.getInstance().getSkinTexture(p))
                        .filter(skin -> !Pony.getManager().getPony(skin).race().isHuman())
                        .orElse(null);
            });
        });
        return entity -> {
            Identifier override = entity.customName != null ? customNameCache.apply(entity.customName.getString()).getNow(null) : null;
            if (override != null) {
                return override;
            }
            return fallback.apply(entity);
        };
    }
}
