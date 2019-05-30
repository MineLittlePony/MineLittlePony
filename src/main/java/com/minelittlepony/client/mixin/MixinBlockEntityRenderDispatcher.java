package com.minelittlepony.client.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockEntityRenderDispatcher.class)
public interface MixinBlockEntityRenderDispatcher {

    @Accessor("renderers")
    Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> getRenderers();
}
