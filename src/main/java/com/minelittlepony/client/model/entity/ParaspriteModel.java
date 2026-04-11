package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

import com.minelittlepony.client.render.entity.VexRenderer;

public class ParaspriteModel extends EntityModel<VexRenderer.State> {
    private final ModelPart body;
    private final ModelPart jaw;
    private final ModelPart lips;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    private final ModelPart leftWing2;
    private final ModelPart rightWing2;

    public ParaspriteModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        body = root.getChild("body");
        jaw = body.getChild("jaw");
        lips = body.getChild("lips");
        leftWing = root.getChild("leftWing");
        rightWing = root.getChild("rightWing");
        leftWing2 = root.getChild("leftWing2");
        rightWing2 = root.getChild("rightWing2");
    }

    @Override
    public void setupAnim(VexRenderer.State state) {
        root.xRot = state.bodyPitch;
        body.xRot = 0;
        root.xRot = state.xRot * Mth.DEG_TO_RAD;
        root.yRot = state.yRot * Mth.DEG_TO_RAD;

        jaw.y = Math.max(0, 1.2F * state.jawOpenAmount);
        lips.y = jaw.y - 0.9F;
        lips.visible = state.jawOpenAmount > 0;
        body.xRot += 0.3F * state.jawOpenAmount;
        jaw.xRot = 0.4F * state.jawOpenAmount;
        lips.xRot = 0.2F * state.jawOpenAmount;

        leftWing.setRotation(0, state.wingYaw, state.wingRoll);
        rightWing.setRotation(0, -state.wingYaw, -state.wingRoll);

        leftWing2.setRotation(0, state.innerWingPitch, state.innerWingRoll);
        rightWing2.setRotation(0, -state.innerWingPitch, -state.innerWingRoll);
    }
}
