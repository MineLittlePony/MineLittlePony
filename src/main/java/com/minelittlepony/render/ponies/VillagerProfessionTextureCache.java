package com.minelittlepony.render.ponies;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.util.render.ITextureSupplier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class VillagerProfessionTextureCache implements ITextureSupplier<Integer> {

    private final ResourceLocation[] pool;
    private final String path;

    private final Map<Integer, ResourceLocation> cache = new HashMap<>();

    public VillagerProfessionTextureCache(String path, ResourceLocation... pool) {
        this.path = path;
        this.pool = pool;
    }

    @Override
    public ResourceLocation supplyTexture(Integer profession) {
        ResourceLocation texture = getVillagerTexture(profession);

        try {
            Minecraft.getMinecraft().getResourceManager().getResource(texture);
        } catch (IOException e) {
            return pool[5];
        }

        return texture;
    }

    private ResourceLocation getVillagerTexture(int profession) {
        if (profession >= pool.length) {
            return cache.computeIfAbsent(profession, this::getModProfessionResource);
        }

        return pool[profession];
    }

    private ResourceLocation getModProfessionResource(int professionId) {
        return new ResourceLocation("minelittlepony", String.format(path, professionId));
    }
}
