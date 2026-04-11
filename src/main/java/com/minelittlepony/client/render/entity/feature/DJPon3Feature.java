package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.Avatar;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

public class DJPon3Feature<
        T extends Avatar & ClientAvatarEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    private final DJPon3EarsModel deadMau5 = ModelType.DJ_PON_3.createModel();

    public DJPon3Feature(PonyRenderContext<T, S, M> context) {
        super(context);
    }

    @Override
    public void submit(PoseStack stack, SubmitNodeCollector queue, int light, S state, float limbAngle, float limbDistance) {
        if (state.nameTag != null && "deadmau5".equals(state.nameTag.getString())) {
            stack.pushPose();

            M body = getContext().getEquineManager().lookupModel(state).body();

            body.transform(state, BodyPart.HEAD, stack);
            body.getHead().translateAndRotate(stack);

            stack.scale(1.3333334F, 1.3333334F, 1.3333334F);
            stack.translate(0, 0.3F, 0);

            deadMau5.setVisible(true);

            SkullModelBase.State skullState = new SkullModelBase.State();
            skullState.xRot = state.xRot;
            skullState.yRot = state.yRot;

            queue.order(1).submitModel(deadMau5, skullState, stack, deadMau5.renderType(state.skin.body().texturePath()), light, OverlayTexture.NO_OVERLAY, CommonColors.WHITE, null, state.outlineColor, null);

            stack.popPose();
        }
    }
}
