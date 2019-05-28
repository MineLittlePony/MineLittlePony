package com.minelittlepony.client.mixin;

import com.minelittlepony.client.ducks.IRenderItem;
import com.minelittlepony.client.render.LevitatingItemRenderer;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.SynchronousResourceReloadListener;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer implements SynchronousResourceReloadListener, IRenderItem {

    private boolean transparency;

    @Override
    public void useTransparency(boolean transparency) {
        this.transparency = transparency;
    }

    @Inject(method = "renderItemAndGlow("
            + "Lnet/minecraft/item/ItemStack;"
            + "Lnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"))
    private void onRenderItem(ItemStack stack, BakedModel model, CallbackInfo info) {
        if (transparency) {
            LevitatingItemRenderer.enableItemGlowRenderProfile();
        }
    }

    @ModifyArg(method = "renderQuads("
            + "Lnet/minecraft/client/render/BufferBuilder;"
            + "Ljava/util/List;I"
            + "Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderQuad("
                            + "Lnet/minecraft/client/render/BufferBuilder;"
                            + "Lnet/minecraft/client/render/model/BakedQuad;I)V"),
            index = 2)
    private int modifyItemRenderTint(int color) {
        return transparency ? -1 : color;
    }

    @Inject(method = "renderEffect("
            + "Lnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void renderEffect(BakedModel model, CallbackInfo info) {
        if (transparency) {
            info.cancel();
        }
    }
}
