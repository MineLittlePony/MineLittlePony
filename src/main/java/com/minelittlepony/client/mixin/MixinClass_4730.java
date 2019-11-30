package com.minelittlepony.client.mixin;

import net.minecraft.class_4730;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.LevitatingItemRenderer;

import java.util.function.Function;

@Mixin(class_4730.class)
abstract class MixinClass_4730 {

    @Inject(method = "method_24145("
                + "Lnet/minecraft/client/render/VertexConsumerProvider;"
                + "Ljava/util/function/Function;"
            + ")"
            + "Lnet/minecraft/client/render/VertexConsumer;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onGetBuffer(VertexConsumerProvider provider, Function<Identifier, RenderLayer> layerFunction, CallbackInfoReturnable<VertexConsumer> info) {
        if (LevitatingItemRenderer.usesTransparency()) {
            class_4730 self = (class_4730)(Object)this;

            info.setReturnValue(self.method_24148().method_24108(provider.getBuffer(LevitatingItemRenderer.getRenderLayer(self.method_24144()))));
        }
    }
}
