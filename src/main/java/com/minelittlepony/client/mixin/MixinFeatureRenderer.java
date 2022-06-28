package com.minelittlepony.client.mixin;


import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderer.class)
public class  MixinFeatureRenderer {


    @Inject(method = {
            "renderModel(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFF)V"
    }, at = @At("HEAD"), cancellable = true)

    private static void test2(EntityModel<LivingEntity> model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float red, float green, float blue, CallbackInfo ci){



        if(entity instanceof VillagerEntity){

//            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer( RenderLayer.getArmorCutoutNoCull(texture));
            model.render(matrices, vertexConsumer, light, OverlayTexture.field_32954, red, green, blue, 1.0F);

            ci.cancel();
        }

    }
}
