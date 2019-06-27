package com.minelittlepony.mixin;

import com.minelittlepony.render.LevitatingItemRenderer;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem implements IResourceManagerReloadListener {

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At("HEAD"))
    private void onRenderItem(ItemStack stack, IBakedModel model, CallbackInfo info) {
        LevitatingItemRenderer.enableItemGlowRenderProfile();
    }

    @ModifyArg(method = "renderQuads(Lnet/minecraft/client/renderer/BufferBuilder;Ljava/util/List;ILnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderItem;renderQuad(Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/client/renderer/block/model/BakedQuad;I)V"),
            index = 2)
    private int modifyItemRenderTint(int color) {
        return LevitatingItemRenderer.usesTransparency() ? -1 : color;
    }

    @Inject(method = "renderEffect(Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At("HEAD"), cancellable = true)
    private void renderEffect(IBakedModel model, CallbackInfo info) {
        if (LevitatingItemRenderer.usesTransparency()) {
            info.cancel();
        }
    }
}
