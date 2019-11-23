package com.minelittlepony.client.render.layer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelDeadMau5Ears;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;

public class LayerDJPon3Head<T extends AbstractClientPlayerEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyLayer<T, M> {

    private final ModelDeadMau5Ears deadMau5 = new ModelDeadMau5Ears();

    public LayerDJPon3Head(IPonyRender<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        if ("deadmau5".equals(entity.getName().getString())) {
            stack.push();
            getPlayerModel().transform(BodyPart.HEAD, stack);
            getPlayerModel().getHead().rotate(stack);

            stack.scale(1.3333334F, 1.3333334F, 1.3333334F);
            stack.translate(0, 0.3F, 0);

            deadMau5.setVisible(true);

            VertexConsumer vertices = renderContext.getBuffer(deadMau5.getLayer(entity.getSkinTexture()));

            deadMau5.render(stack, vertices, OverlayTexture.DEFAULT_UV, lightUv, limbDistance, limbAngle, tickDelta, 1);

            stack.pop();
        }
    }
}
