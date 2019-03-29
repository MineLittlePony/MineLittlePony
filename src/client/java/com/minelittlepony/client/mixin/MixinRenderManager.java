package com.minelittlepony.client.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

@Mixin(RenderManager.class)
public interface MixinRenderManager {
    // There is a method to get it, but it's made immutable my Forge.
    @Accessor("skinMap")
    Map<String, RenderPlayer> getMutableSkinMap();
}
