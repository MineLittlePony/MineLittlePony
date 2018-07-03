package com.voxelmodpack.hdskins.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public final class CubeMapRegistry {

    private static final List<String> RAW_SOURCES = new ArrayList<String>();
    private static final List<ResourceLocation[]> SOURCES = new ArrayList<>();

    public static ResourceLocation[] generatePanoramaResources(String source) {
        return new ResourceLocation[] {
            new ResourceLocation(String.format(source, 0)),
            new ResourceLocation(String.format(source, 1)),
            new ResourceLocation(String.format(source, 2)),
            new ResourceLocation(String.format(source, 3)),
            new ResourceLocation(String.format(source, 4)),
            new ResourceLocation(String.format(source, 5))
        };
    }

    public static void addSource(String source) {
        if (!RAW_SOURCES.contains(source)) {
            SOURCES.add(generatePanoramaResources(source));
        }
    }

    public static int getRandomResourceIndex(boolean includeVanilla) {
        int count = SOURCES.size();

        if (includeVanilla) count++;

        count = (int)Math.floor(Math.random() * count);

        if (count >= SOURCES.size()) {
            return -1;
        }

        return count;
    }

    public static ResourceLocation[] pickResource() {
        return getResource(getRandomResourceIndex(false));
    }

    public static ResourceLocation[] getResource(int index) {
        return SOURCES.get(index % SOURCES.size());
    }
}
