package com.minelittlepony.client.mixin;

import com.minelittlepony.client.ducks.IRenderItem;
import com.minelittlepony.client.render.LevitatingItemRenderer;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("deprecation") // IResourceManagerReloadListener is deprecated by forge but it's part of the signature. We can't remove it.
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer implements IResourceManagerReloadListener, IRenderItem {

    private boolean transparency;

    @Override
    public void useTransparency(boolean transparency) {
        this.transparency = transparency;
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At("HEAD"))
    private void onRenderItem(ItemStack stack, IBakedModel model, CallbackInfo info) {
        if (transparency) {
            LevitatingItemRenderer.enableItemGlowRenderProfile();
        }
    }

    @ModifyArg(method = "renderQuads(Lnet/minecraft/client/renderer/BufferBuilder;Ljava/util/List;ILnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderItem;renderQuad(Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/client/renderer/block/model/BakedQuad;I)V"),
            index = 2)
    private int modifyItemRenderTint(int color) {
        return transparency ? -1 : color;
    }

    @Inject(method = "renderEffect(Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At("HEAD"), cancellable = true)
    private void renderEffect(IBakedModel model, CallbackInfo info) {
        if (transparency) {
            info.cancel();
        }
    }
}
