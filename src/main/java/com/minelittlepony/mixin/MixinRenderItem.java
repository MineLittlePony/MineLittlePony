package com.minelittlepony.mixin;

import com.minelittlepony.ducks.IRenderItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem implements IResourceManagerReloadListener, IRenderItem {

    private boolean transparency;

    @Override
    public void useTransparency(boolean transparency) {
        this.transparency = transparency;
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At("HEAD"))
    private void onRenderItem(ItemStack stack, IBakedModel model, CallbackInfo info) {
        if (transparency) {
            GlStateManager.tryBlendFuncSeparate(SourceFactor.CONSTANT_COLOR, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
        }
    }
}
