package com.minelittlepony.mixin;

import com.minelittlepony.ducks.IRenderItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderItem.class)
@Implements( value = @Interface(iface = IRenderItem.class, prefix = "mlp$") )
public abstract class MixinRenderItem implements IResourceManagerReloadListener, IRenderItem {

    private static final String ItemStack = "Lnet/minecraft/item/ItemStack;";
    private static final String IBakedModel = "Lnet/minecraft/client/renderer/block/model/IBakedModel;";
    private static final String ItemCameraTransform$TransformType = "Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;";
    private static final String GlStateManager$SourceFactor = "Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;";
    private static final String GlStateManager$DestFactor = "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;";

    private boolean transparency;

    public void mlp$useTransparency(boolean transparency) {
        this.transparency = transparency;
    }

    @Redirect(method = "renderItemModel(" + ItemStack + IBakedModel + ItemCameraTransform$TransformType + "Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;tryBlendFuncSeparate("
                            + GlStateManager$SourceFactor + GlStateManager$DestFactor
                            + GlStateManager$SourceFactor + GlStateManager$DestFactor + ")V"))
    private void redirectBlendFunc(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
                                   GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha) {
        if (transparency) {
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.CONSTANT_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        } else {
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
    }
}
