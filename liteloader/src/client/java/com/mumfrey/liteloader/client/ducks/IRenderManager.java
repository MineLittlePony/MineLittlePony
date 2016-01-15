package com.mumfrey.liteloader.client.ducks;

import java.util.Map;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public interface IRenderManager
{
    public abstract Map<Class<? extends Entity>, Render> getRenderMap();
}
