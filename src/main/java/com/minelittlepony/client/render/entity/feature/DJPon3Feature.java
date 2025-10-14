package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel.SkullModelState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.Colors;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class DJPon3Feature<
        T extends PlayerLikeEntity & ClientPlayerLikeEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    private final DJPon3EarsModel deadMau5 = ModelType.DJ_PON_3.createModel();

    public DJPon3Feature(PonyRenderContext<T, S, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        if (state.displayName != null && "deadmau5".equals(state.displayName.getString())) {
            stack.push();

            M body = getModelWrapper().body();

            body.transform(state, BodyPart.HEAD, stack);
            body.getHead().applyTransform(stack);

            stack.scale(1.3333334F, 1.3333334F, 1.3333334F);
            stack.translate(0, 0.3F, 0);

            deadMau5.setVisible(true);

            SkullModelState skullState = new SkullModelState();
            skullState.pitch = state.pitch;
            skullState.yaw = state.relativeHeadYaw;

            queue.getBatchingQueue(1).submitModel(deadMau5, skullState, stack, deadMau5.getLayer(state.skinTextures.body().texturePath()), light, OverlayTexture.DEFAULT_UV, Colors.WHITE, null, state.outlineColor, null);

            stack.pop();
        }
    }
}
