package com.minelittlepony.client.mixin;

import net.minecraft.client.renderer.special.*;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.client.render.blockentity.skull.PonyHeadModelRenderer;

@Mixin(value = { SkullSpecialRenderer.Unbaked.class, PlayerHeadSpecialRenderer.Unbaked.class })
abstract class MixinHeadModelRenderer_Unbaked {
    @ModifyReturnValue(method = "bake(Lnet/minecraft/client/renderer/special/SpecialModelRenderer$BakingContext;)Lnet/minecraft/client/renderer/special/SpecialModelRenderer;", at = @At("RETURN"))
    private /*synthetic bridge*/ SpecialModelRenderer<?> onBake(@Nullable SpecialModelRenderer<?> renderer) {
        return PonyHeadModelRenderer.create((SpecialModelRenderer.Unbaked<?>)this, renderer);
    }
}
