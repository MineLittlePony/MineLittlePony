package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class DJPon3Feature<
        T extends AbstractClientPlayerEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    private final DJPon3EarsModel deadMau5 = ModelType.DJ_PON_3.createModel();

    public DJPon3Feature(PonyRenderContext<T, S, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int light, S state, float limbAngle, float limbDistance) {
        if ("deadmau5".equals(state.name)) {
            stack.push();

            M body = getModelWrapper().body();

            body.transform(state, BodyPart.HEAD, stack);
            body.getHead().rotate(stack);

            stack.scale(1.3333334F, 1.3333334F, 1.3333334F);
            stack.translate(0, 0.3F, 0);

            deadMau5.setVisible(true);

            VertexConsumer vertices = renderContext.getBuffer(deadMau5.getLayer(state.skinTextures.texture()));

            deadMau5.render(stack, vertices, OverlayTexture.DEFAULT_UV, light, Colors.WHITE);

            stack.pop();
        }
    }
}
