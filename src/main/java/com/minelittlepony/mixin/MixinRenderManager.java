package com.minelittlepony.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

@Mixin(RenderManager.class)
public interface MixinRenderManager {
    @Accessor
    Map<String, RenderPlayer> getSkinMap();
}
