package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;

public class SillyPonyTextures<T extends LivingEntity & VillagerDataContainer> implements TextureSupplier<T> {

    private final TextureSupplier<T> fallback;

    private final Identifier egg;
    private final Identifier egg2;

    public SillyPonyTextures(TextureSupplier<T> fallback, TextureSupplier<String> formatter) {
        this.fallback = fallback;
        this.egg = formatter.supplyTexture("silly_pony");
        this.egg2 = formatter.supplyTexture("tiny_silly_pony");
    }

    @Override
    public Identifier supplyTexture(T entity) {
        if (PonyTextures.isBestPony(entity)) {
            return entity.isBaby() ? egg2 : egg;
        }
        return fallback.supplyTexture(entity);
    }
}
