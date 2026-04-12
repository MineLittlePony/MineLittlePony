package com.minelittlepony.client.model.entity.race;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import com.minelittlepony.mson.api.ModelView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.minelittlepony.api.model.*;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class SeaponyModel<T extends PonyRenderState> extends UnicornModel<T> {

    private static final float FIN_Y_ANGLE = Mth.PI / 6;

    private final ModelPart leftFin;
    private final ModelPart centerFin;
    private final ModelPart rightFin;

    public SeaponyModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        leftFin = tree.getChild("left_fin");
        rightFin = tree.getChild("right_fin");
        centerFin = tree.getChild("center_fin");

        jacket.skipDraw = true;

        leftPants.skipDraw = true;
        rightPants.skipDraw = true;
        leftLeg.skipDraw = true;
        rightLeg.skipDraw = true;
    }

    public SeaponyModel(ModelPart tree) {
        this(tree, false);
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        bodyRenderList.clear();
        bodyRenderList.add(body).add(body::translateAndRotate).add(tail).add(leftFin, centerFin, rightFin);
    }

    @Override
    protected void setModelAngles(T entity) {
        super.setModelAngles(entity);

        float flapMotion = Mth.cos(entity.ageInTicks / 10) * 0.2F;

        if (entity.attributes.isLyingDown) {
            flapMotion *= 0.5F;
        }

        float finAngle = FIN_Y_ANGLE + flapMotion;

        leftFin.yRot = finAngle;
        rightFin.yRot = -finAngle;
        centerFin.zRot = flapMotion;
    }

    @Override
    protected void rotateLegs(T state) {
        walkSeapony(state, leftArm, rightArm, leftLeg, rightLeg);
    }

    @Override
    public void transform(T state, BodyPart part, PoseStack stack) {
        stack.translate(0, 0.6F, 0);
        super.transform(state, part, stack);
    }

    static <T extends PonyRenderState> void walkSeapony(T state,
            ModelPart frontLeftLeg, ModelPart frontRightLeg,
            ModelPart backLeftLeg, ModelPart backRightLeg
    ) {
        QuadrupedLegPosing.walk(state, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
        frontLeftLeg.xRot -= 1.4F;
        frontLeftLeg.yRot -= 0.3F;
        frontRightLeg.xRot -= 1.4F;
        frontRightLeg.yRot += 0.3F;

        if (!state.submergedInWater) {
            frontLeftLeg.xRot -= 0.5F;
            frontRightLeg.xRot -= 0.5F;
        }

        if (!state.submergedInWater || state.onGround) {
            frontLeftLeg.yRot -= 0.5F;
            frontRightLeg.yRot += 0.5F;
        }
    }

    public static class Armour<T extends PonyRenderState> extends PonyArmourModel<T> {
        public Armour(ModelPart tree) {
            super(tree);
            rightLeg.skipDraw = true;
            leftLeg.skipDraw = true;
        }

        @Override
        protected void rotateLegs(T state) {
            walkSeapony(state, leftArm, rightArm, leftLeg, rightLeg);
        }

        @Override
        public void transform(T state, BodyPart part, PoseStack stack) {
            stack.translate(0, 0.6F, 0);
            super.transform(state, part, stack);
        }
    }
}
