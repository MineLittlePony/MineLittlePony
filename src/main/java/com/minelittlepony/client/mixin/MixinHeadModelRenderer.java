package com.minelittlepony.client.mixin;

import net.minecraft.client.render.item.model.special.*;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.client.render.blockentity.skull.PonyHeadModelRenderer;

@Mixin(value = { HeadModelRenderer.Unbaked.class, PlayerHeadModelRenderer.Unbaked.class })
abstract class MixinHeadModelRenderer_Unbaked {
    @ModifyReturnValue(method = "bake(Lnet/minecraft/client/render/item/model/special/SpecialModelRenderer$BakingContext;)Lnet/minecraft/client/render/item/model/special/SpecialModelRenderer;", at = @At("RETURN"))
    private /*synthetic bridge*/ SpecialModelRenderer<?> onBake(@Nullable SpecialModelRenderer<?> renderer) {
        return PonyHeadModelRenderer.create((SpecialModelRenderer.Unbaked)this, renderer);
    }
}
