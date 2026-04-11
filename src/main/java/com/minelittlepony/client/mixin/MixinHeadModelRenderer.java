package com.minelittlepony.client.mixin;

import net.minecraft.client.renderer.special.*;
import net.minecraft.world.level.block.SkullBlock;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.client.render.blockentity.skull.PonyHeadModelRenderer;

import java.util.Optional;

@Mixin(value = { SkullSpecialRenderer.Unbaked.class, PlayerHeadSpecialRenderer.Unbaked.class })
abstract class MixinHeadModelRenderer_Unbaked {
    @ModifyReturnValue(method = "bake", at = @At("RETURN"))
    private /*synthetic bridge*/ SpecialModelRenderer<?> onBake(@Nullable SpecialModelRenderer<?> renderer) {
        Object self = this;
        if (self instanceof SkullSpecialRenderer.Unbaked a) {
            return renderer instanceof SkullSpecialRenderer r ? new PonyHeadModelRenderer(r, a.kind(), a.textureOverride(), a.animation()) : renderer;
        }
        return renderer instanceof PlayerHeadSpecialRenderer r ? new PonyHeadModelRenderer(r, SkullBlock.Types.PLAYER, Optional.empty(), 0F) : renderer;
    }
}
