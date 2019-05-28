package com.minelittlepony.client.render.entities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerData;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.util.resources.ITextureSupplier;
import com.minelittlepony.util.resources.ProfessionStringMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Cached pool of villager textures.
 */
class VillagerProfessionTextureCache implements ITextureSupplier<VillagerData> {

    private final ITextureSupplier<String> formatter;

    private final Function<VillagerData, String> keyMapper = new ProfessionStringMapper();

    private final Identifier fallback;

    private final Map<String, Identifier> cache = new HashMap<>();

    /**
     * Creates a new profession cache
     *
     * @param formatter Formatter used when creating new textures
     * @param keyMapper Mapper to convert integer ids into a string value for format insertion
     * @param fallback  The default if any generated textures fail to load. This is stored in place of failing textures.
     */
    public VillagerProfessionTextureCache(ITextureSupplier<String> formatter, Identifier fallback) {
        this.formatter = formatter;
        this.fallback = fallback;
    }

    @Override
    public Identifier supplyTexture(VillagerData data) {
        return cache.computeIfAbsent(keyMapper.apply(data), this::getModProfessionResource);
    }

    private Identifier getModProfessionResource(String professionId) {
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
