package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.EntityType;

@Mixin(EntityRenderers.class)
public interface MixinEntityRenderers {
    @Accessor("RENDERER_FACTORIES")
    public static Map<EntityType<?>, EntityRendererFactory<?>> getRendererFactories() {
        return null;
    }
}
