package com.minelittlepony.client.mixin;

import net.minecraft.block.SkullBlock;

import net.minecraft.client.render.item.model.special.HeadModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.blockentity.skull.PonyHeadModelRenderer;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

@Mixin(HeadModelRenderer.Unbaked.class)
abstract class MixinHeadModelRenderer_Unbaked {
    @Shadow
    private @Final SkullBlock.SkullType kind;

    @Inject(method = "bake", at = @At("RETURN"), cancellable = true)
    private void onBake(SpecialModelRenderer.BakeContext context, CallbackInfoReturnable<SpecialModelRenderer<?>> info) {
        if (info.getReturnValue() instanceof HeadModelRenderer p) {
            info.setReturnValue(new PonyHeadModelRenderer(p, PonySkullRenderer.INSTANCE.getSkullState(kind, null)));
        }
    }
}
