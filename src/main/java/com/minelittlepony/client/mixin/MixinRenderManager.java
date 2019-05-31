package com.minelittlepony.client.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

@Mixin(EntityRenderDispatcher.class)
public interface MixinRenderManager {
    @Accessor("modelRenderers")
    Map<String, PlayerEntityRenderer> getMutableSkinMap();
}
