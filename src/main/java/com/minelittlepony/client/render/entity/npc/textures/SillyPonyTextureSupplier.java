package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;

public class SillyPonyTextureSupplier<T extends LivingEntity & VillagerDataContainer> implements TextureSupplier<T> {

    private final TextureSupplier<T> fallback;

    private final Identifier egg;
    private final Identifier egg2;

    public SillyPonyTextureSupplier(TextureSupplier<T> fallback, TextureSupplier<String> formatter) {
        this.fallback = fallback;
        this.egg = formatter.apply("silly_pony");
        this.egg2 = formatter.apply("tiny_silly_pony");
    }

    @Override
    public Identifier apply(T entity) {
        return isBestPony(entity) ? (entity.isBaby() ? egg2 : egg) : fallback.apply(entity);
    }

    public static boolean isBestPony(LivingEntity entity) {
        if (!entity.hasCustomName()) {
            return false;
        }
        String name = entity.getCustomName().getString();
        return "Derpy".equals(name) || (entity.isBaby() && "Dinky".equals(name));
    }

    public static boolean isCrownPony(LivingEntity entity) {
        return isBestPony(entity) && entity.getUuid().getLeastSignificantBits() % 20 == 0;
    }
}
