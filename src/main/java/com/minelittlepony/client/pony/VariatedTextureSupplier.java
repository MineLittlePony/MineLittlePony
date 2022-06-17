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

    public static final Identifier BACKGROUND_PONIES = new Identifier("minelittlepony", "textures/entity/pony");
    public static final Identifier BREEZIE_PONIES = new Identifier("minelittlepony", "textures/entity/allay/pony");
    public static final Identifier PARASPRITE_PONIES = new Identifier("minelittlepony", "textures/entity/illager/vex_pony");

    private final Map<Identifier, BackgroundPonyList> entries = new HashMap<>();

    public VariatedTextureSupplier() {
        get(BACKGROUND_PONIES);
        get(BREEZIE_PONIES);
        get(PARASPRITE_PONIES);
    }

    @Override
    public void reload(ResourceManager manager) {
        entries.forEach((key, value) -> value.reloadAll(manager));
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    public VariatedTexture get(Identifier id) {
        return entries.computeIfAbsent(id, BackgroundPonyList::new);
    }

    public interface VariatedTexture {
        Identifier get(UUID uuid);

        default Identifier get(Entity entity) {
            return get(entity.getUuid());
        }
    }
}
