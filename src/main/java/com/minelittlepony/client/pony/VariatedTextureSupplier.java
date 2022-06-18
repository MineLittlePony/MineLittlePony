package com.minelittlepony.client.pony;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VariatedTextureSupplier implements SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = new Identifier("minelittlepony", "variated_textures");

    private final Map<Identifier, BackgroundPonyList> entries = new HashMap<>();

    @Override
    public void reload(ResourceManager manager) {
        entries.clear();
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    private BackgroundPonyList get(Identifier id) {
        return entries.computeIfAbsent(id, BackgroundPonyList::new);
    }

    public Identifier get(Identifier poolId, UUID seed) {
        return get(poolId).getId(seed);
    }

    public Identifier get(Identifier poolId, Entity entity) {
        return get(poolId, entity.getUuid());
    }
}
