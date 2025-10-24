package com.minelittlepony.client.mixin;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.HeadModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.Data;

@Mixin(HeadModelRenderer.class)
abstract class MixinHeadModelRenderer implements PonySkullRenderer.Proxy {
    @Nullable
    private PonySkullRenderer.Data data;

    public void setPonySkullData(Data data) {
        this.data = data;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i, CallbackInfo info) {
        if (data != null && data.render(null, 180, 0, matrices, queue, light, 0, null)) {
            info.cancel();
        }
    }
}

@Mixin(HeadModelRenderer.Unbaked.class)
abstract class MixinHeadModelRenderer_Unbaked {
    @Shadow
    private @Final SkullBlock.SkullType kind;

    @Inject(method = "bake", at = @At("RETURN"), cancellable = true)
    private void onBake(SpecialModelRenderer.BakeContext context, CallbackInfoReturnable<SpecialModelRenderer<?>> info) {
        if (info.getReturnValue() instanceof PonySkullRenderer.Proxy p) {
            p.setPonySkullData(PonySkullRenderer.INSTANCE.getSkullState(kind, null));
        }
    }
}
