package com.minelittlepony.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;

import com.minelittlepony.util.MathUtil;

import java.util.*;

public class VariatedTextureSupplier implements ResourceManagerReloadListener {
    public static final Identifier ID = MineLittlePony.id("variated_textures");
    public static final Identifier BACKGROUND_PONIES_POOL = MineLittlePony.id("textures/entity/pony");
    public static final Identifier BACKGROUND_ZOMPONIES_POOL = MineLittlePony.id("textures/entity/zompony");

    private final Map<Identifier, SkinList> entries = new TreeMap<>();

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        synchronized (entries) {
            entries.values().removeIf(i -> !i.reloadAll(manager));
        }
    }

    public SkinList get(Identifier poolId) {
        synchronized (entries) {
            return entries.computeIfAbsent(poolId, SkinList::new);
        }
    }

    public static final class SkinList {
        private SkinSet textures = SkinSet.EMPTY;
        private Map<String, SkinSet> names;

        private final Identifier id;

        private SkinList(Identifier id) {
            this.id = id;
            reloadAll(Minecraft.getInstance().getResourceManager());
        }

        public Optional<Identifier> getId(Entity entity) {
            return getId(entity.getUUID());
        }

        public Optional<Identifier> getId(UUID uuid) {
            return textures.pick(uuid);
        }

        public Optional<Identifier> getByName(String name, UUID uuid) {
            if (textures.size() == 0) {
                return Optional.empty();
            }

            synchronized (textures) {
                return names.computeIfAbsent(name.toLowerCase(Locale.ROOT).replace(' ', '_') + ".png", id -> {
                    return new SkinSet(Arrays.stream(textures.options()).filter(texture -> texture.getPath().endsWith(id)).toArray(Identifier[]::new));
                }).pick(uuid);
            }
        }

        public boolean reloadAll(ResourceManager resourceManager) {
            synchronized (textures) {
                textures = new SkinSet(resourceManager.listResources(id.getPath(), path -> path.getPath().endsWith(".png")).keySet().toArray(Identifier[]::new));
                names = new TreeMap<>();
                MineLittlePony.LOGGER.info("Detected {} ponies installed at {}.", textures.size(), id);
                return textures.size() > 0;
            }
        }

        private record SkinSet(int size, Optional<Identifier> first, Identifier[] options) {
            static final SkinSet EMPTY = new SkinSet(0, Optional.empty(), new Identifier[0]);

            private SkinSet(Identifier[] options) {
                this(options.length, options.length > 0 ? Optional.of(options[0]) : Optional.empty(), options);
            }

            public Optional<Identifier> pick(UUID uuid) {
                if (size < 2) {
                    return first;
                }
                synchronized (this) {
                    return Optional.ofNullable(options[MathUtil.mod(uuid.hashCode(), size)]);
                }
            }
        }
    }
}
