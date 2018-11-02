package com.minelittlepony;

import net.minecraft.util.ResourceLocation;

public class CasedResourceLocation extends ResourceLocation {
    protected final String casedPath;

    public CasedResourceLocation(String domain, String path) {
        super(domain, path);

        casedPath = path;
    }

    @Override
    public String getPath() {
        return casedPath;
    }

    @Override
    public String toString() {
        return namespace + ':' + casedPath;
    }
}
