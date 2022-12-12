package com.minelittlepony.client.pony;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.util.MathUtil;

import java.util.*;

public class VariatedTextureSupplier implements SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = new Identifier("minelittlepony", "variated_textures");

    private final Map<Identifier, SkinList> entries = new HashMap<>();

    @Override
    public void reload(ResourceManager manager) {
        entries.clear();
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    public SkinList get(Identifier id) {
        return entries.computeIfAbsent(id, SkinList::new);
    }

    public Optional<Identifier> get(Identifier poolId, UUID seed) {
        return get(poolId).getId(seed);
    }

    public Optional<Identifier> get(Identifier poolId, Entity entity) {
        return get(poolId, entity.getUuid());
    }

    public static final class SkinList {
        private final List<Identifier> textures = new ArrayList<>();
        private final Map<String, List<Identifier>> names = new HashMap<>();

        private final Identifier id;

        public SkinList(Identifier id) {
            this.id = id;
            reloadAll(MinecraftClient.getInstance().getResourceManager());
        }

        public Optional<Identifier> getId(UUID uuid) {
            if (textures.isEmpty() || isUser(uuid)) {
                return Optional.empty();
            }

            return Optional.ofNullable(textures.get(MathUtil.mod(uuid.hashCode(), textures.size())));
        }

        public Optional<Identifier> getByName(String name, UUID uuid) {
            List<Identifier> options = names.computeIfAbsent(name.toLowerCase(Locale.ROOT).replace(' ', '_') + ".png", id -> {
                return textures.stream().filter(texture -> {
                    return texture.getPath().endsWith(id);
                }).toList();
            });

            if (options.isEmpty()) {
                return Optional.empty();
            }

            return Optional.ofNullable(options.get(MathUtil.mod(uuid.hashCode(), options.size())));
        }

        public void reloadAll(ResourceManager resourceManager) {
            textures.clear();
            textures.addAll(resourceManager.findResources(id.getPath(), path -> path.getPath().endsWith(".png")).keySet());
            MineLittlePony.logger.info("Detected {} ponies installed at {}.", textures.size(), id);
        }

        static boolean isUser(UUID uuid) {
            return MinecraftClient.getInstance().player != null
                && MinecraftClient.getInstance().player.getUuid().equals(uuid);
        }
    }

}
