package com.minelittlepony.client.pony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.util.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * All currently loaded background ponies.
 */
class BackgroundPonyList {
    /**
     * All currently loaded background ponies.
     */
    private final List<Identifier> backgroundPonyList = new ArrayList<>();

    private final Identifier id;

    public BackgroundPonyList(Identifier id) {
        this.id = id;
        reloadAll(MinecraftClient.getInstance().getResourceManager());
    }

    public Identifier getId(UUID uuid) {
        if (backgroundPonyList.isEmpty() || isUser(uuid)) {
            return IPonyManager.getDefaultSkin(uuid);
        }

        int bgi = MathUtil.mod(uuid.hashCode(), backgroundPonyList.size());

        return backgroundPonyList.get(bgi);
    }

    public void reloadAll(ResourceManager resourceManager) {
        backgroundPonyList.clear();
        backgroundPonyList.addAll(resourceManager.findResources(id.getPath(), path -> path.getPath().endsWith(".png")).keySet());
        MineLittlePony.logger.info("Detected {} ponies installed at {}.", backgroundPonyList.size(), id);
    }

    static boolean isUser(UUID uuid) {
        return MinecraftClient.getInstance().player != null
            && MinecraftClient.getInstance().player.getUuid().equals(uuid);
    }
}
