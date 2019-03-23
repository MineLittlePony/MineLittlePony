package com.minelittlepony.client.render.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.common.MineLittlePony;
import com.minelittlepony.util.resources.ITextureSupplier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Cached pool of villager textures.
 */
class VillagerProfessionTextureCache implements ITextureSupplier<Integer> {

    private final ITextureSupplier<String> formatter;

    private final Function<Integer, String> keyMapper;

    private final ResourceLocation fallback;

    private final Map<Integer, ResourceLocation> cache = new HashMap<>();

    /**
     * Creates a new profession cache
     *
     * @param formatter Formatter used when creating new textures
     * @param keyMapper Mapper to convert integer ids into a string value for format insertion
     * @param fallback  The default if any generated textures fail to load. This is stored in place of failing textures.
     */
    public VillagerProfessionTextureCache(ITextureSupplier<String> formatter, Function<Integer, String> keyMapper, ResourceLocation fallback) {
        this.formatter = formatter;
        this.fallback = fallback;
        this.keyMapper = keyMapper;
    }

    @Override
    public ResourceLocation supplyTexture(Integer profession) {
        return cache.computeIfAbsent(profession, this::getModProfessionResource);
    }

    private ResourceLocation getModProfessionResource(int professionId) {
        ResourceLocation generated = formatter.supplyTexture(keyMapper.apply(professionId));

        try {
            Minecraft.getMinecraft().getResourceManager().getResource(generated);
        } catch (IOException e) {
            MineLittlePony.logger.error("Error loading villager texture `" + generated + "`.", e);

            // if texture loading fails, use the fallback.
            return fallback;
        }

        return generated;
    }
}
