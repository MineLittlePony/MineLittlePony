package com.minelittlepony.client.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.LevitatingItemRenderer;

import java.util.function.Function;

@Mixin(SpriteIdentifier.class)
abstract class MixinSpriteIdentifier {

    @Inject(method = "getVertexConsumer("
                + "Lnet/minecraft/client/render/VertexConsumerProvider;"
                + "Ljava/util/function/Function;"
            + ")"
            + "Lnet/minecraft/client/render/VertexConsumer;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onGetBuffer(VertexConsumerProvider provider, Function<Identifier, RenderLayer> layerFunction, CallbackInfoReturnable<VertexConsumer> info) {
        if (LevitatingItemRenderer.isEnabled()) {
            SpriteIdentifier self = (SpriteIdentifier)(Object)this;

            info.setReturnValue(self.getSprite().getTextureSpecificVertexConsumer(provider.getBuffer(LevitatingItemRenderer.getRenderLayer(self.getAtlasId()))));
        }
    }
}
