package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;

public class SillyPonyTextureSupplier {
    public static <T extends LivingEntity & VillagerDataContainer> TextureSupplier<T> create(TextureSupplier<T> fallback, TextureSupplier<String> formatter) {
        Identifier egg = formatter.apply("silly_pony");
        Identifier egg2 = formatter.apply("tiny_silly_pony");
        return entity -> isBestPony(entity) ? (entity.isBaby() ? egg2 : egg) : fallback.apply(entity);
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
