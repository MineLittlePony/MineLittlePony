package com.minelittlepony.client.render.entities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.util.resources.ITextureSupplier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

        if (entity.getVillagerData().getProfession() == VillagerProfession.NONE) {
            return fallback;
        }

        return cache.computeIfAbsent(formatTexture(entity), this::getTexture);
    }

    public String formatTexture(T entity) {
        VillagerData t = entity.getVillagerData();
        VillagerType type = t.getType();
        VillagerProfession profession = t.getProfession();

        return String.format("pony/%s/%s", type, profession.toString());
    }

    private Identifier getTexture(String professionId) {
        Identifier generated = formatter.supplyTexture(professionId);

        try {
            MinecraftClient.getInstance().getResourceManager().getResource(generated);
        } catch (IOException e) {
            MineLittlePony.logger.error("Error loading villager texture `" + generated + "`.", e);

            // if texture loading fails, use the fallback.
            return fallback;
        }

        return generated;
    }
}
