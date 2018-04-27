package com.minelittlepony.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.minelittlepony.ducks.IRenderManager;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager implements IRenderManager {
    @Shadow @Final
    private Map<String, RenderPlayer> skinMap;

    @Override
    public void addPlayerSkin(String key, RenderPlayer render) {
        skinMap.put(key, render);
    }
}
