package com.minelittlepony.util.resources;

import net.minecraft.util.ResourceLocation;

/**
 * Supplies new resource locations based on a pre-defined domain and formatted path.
 */
public class FormattedTextureSupplier implements ITextureSupplier<String> {

    private final String domain;
    private final String path;

    public FormattedTextureSupplier(String domain, String path) {
        this.domain = domain;
        this.path = path;
    }

    @Override
    public ResourceLocation supplyTexture(String key) {
        return new ResourceLocation(domain, String.format(path, key));
    }

}
