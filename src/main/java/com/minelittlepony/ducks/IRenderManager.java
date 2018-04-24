package com.minelittlepony.ducks;

import net.minecraft.client.renderer.entity.RenderPlayer;

public interface IRenderManager {
    void addPlayerSkin(String key, RenderPlayer render);
}
