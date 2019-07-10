package com.minelittlepony.client.render.entities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.util.resources.ITextureSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Cached pool of villager textures.
 */
class VillagerProfessionTextureCache<T extends LivingEntity & VillagerDataContainer> implements ITextureSupplier<T> {

    private final ITextureSupplier<String> formatter;

    private final Identifier fallback;

    private final Map<String, Identifier> cache = new HashMap<>();

    private final Identifier egg;
    private final Identifier egg2;

    /**
     * Creates a new profession cache
     *
     * @param formatter Formatter used when creating new textures
     * @param keyMapper Mapper to convert integer ids into a string value for format insertion
     * @param fallback  The default if any generated textures fail to load. This is stored in place of failing textures.
     */
    public VillagerProfessionTextureCache(ITextureSupplier<String> formatter) {
        this.formatter = formatter;
        this.fallback = formatter.supplyTexture("villager_pony");
        this.egg = formatter.supplyTexture("silly_pony");
        this.egg2 = formatter.supplyTexture("tiny_silly_pony");
    }

    @Override
    public Identifier supplyTexture(T entity) {
        if (entity.hasCustomName()) {
            String name = entity.getCustomName().getString();
            if ("Derpy".equals(name) || (entity.isBaby() && "Dinky".equals(name))) {
                if (entity.isBaby()) {
                    return egg2;
                }
                return egg;
            }
        }

        VillagerData t = entity.getVillagerData();

        return getTexture(t.getType(), t.getProfession());
    }

    private Identifier getTexture(final VillagerType type, final VillagerProfession profession) {

        if (profession == VillagerProfession.NONE) {
            return fallback;
        }

        String key = String.format("pony/%s/%s", type, profession);

        if (cache.containsKey(key)) {
            return cache.get(key); // People often complain that villagers cause lag,
                                   // so let's do better than Mojang and rather NOT go
                                   // through all the lambda generations if we can avoid it.
        }

        Identifier result = verifyTexture(formatter.supplyTexture(key)).orElseGet(() -> {
            if (type == VillagerType.PLAINS) {
                // if texture loading fails, use the fallback.
                return fallback;
            }

            return getTexture(VillagerType.PLAINS, profession);
        });

        cache.put(key, result);
        return result;
    }

    protected Optional<Identifier> verifyTexture(Identifier texture) {
        if (!MinecraftClient.getInstance().getResourceManager().containsResource(texture)) {
            MineLittlePony.logger.warn("Villager texture `" + texture + "` was not found.");
            return Optional.empty();
        }

        return Optional.of(texture);
    }
}
