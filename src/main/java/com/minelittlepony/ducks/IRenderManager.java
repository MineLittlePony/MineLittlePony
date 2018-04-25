package com.minelittlepony.ducks;

import net.minecraft.client.renderer.entity.RenderPlayer;

public interface IRenderManager {
    /**
     * Registers a new player model to the underlying skinMap object.
     * @param key The key to identify it by.
     * @param render The renderer to add.
     */
    void addPlayerSkin(String key, RenderPlayer render);
}
