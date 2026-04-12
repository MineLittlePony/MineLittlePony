package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.mson.api.ModelView;
import com.minelittlepony.api.model.*;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SeaponyModel<T extends PonyRenderState> extends UnicornModel<T> {

    private static final float FIN_Y_ANGLE = MathHelper.PI / 6;

    private final ModelPart leftFin;
    private final ModelPart centerFin;
    private final ModelPart rightFin;

    public SeaponyModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        leftFin = tree.getChild("left_fin");
        rightFin = tree.getChild("right_fin");
        centerFin = tree.getChild("center_fin");

        jacket.hidden = true;

        leftPants.hidden = true;
        rightPants.hidden = true;
        leftLeg.hidden = true;
        rightLeg.hidden = true;
    }

    public SeaponyModel(ModelPart tree) {
        this(tree, false);
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        setVisible(true);
        bodyRenderList.clear();
        bodyRenderList.add(body).add(body::applyTransform).add(tail).add(leftFin, centerFin, rightFin);
    }

    @Override
    protected void setModelAngles(T entity) {
        super.setModelAngles(entity);

        float flapMotion = MathHelper.cos(entity.age / 10) / 5;

        if (entity.attributes.isLyingDown) {
            flapMotion *= 0.5F;
        }

        float finAngle = FIN_Y_ANGLE + flapMotion;

        leftFin.yaw = finAngle;
        rightFin.yaw = -finAngle;
        centerFin.roll = flapMotion;
    }

    @Override
    protected void rotateLegs(T state) {
        walkSeapony(state, leftArm, rightArm, leftLeg, rightLeg);
    }

    @Override
    public void transform(T state, BodyPart part, MatrixStack stack) {
        stack.translate(0, 0.6F, 0);
        super.transform(state, part, stack);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        leftFin.visible = visible;
        centerFin.visible = visible;
        rightFin.visible = visible;
    }

    static <T extends PonyRenderState> void walkSeapony(T state,
            ModelPart frontLeftLeg, ModelPart frontRightLeg,
            ModelPart backLeftLeg, ModelPart backRightLeg
    ) {
        QuadrupedLegPosing.walk(state, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
        frontLeftLeg.pitch -= 1.4F;
        frontLeftLeg.yaw -= 0.3F;
        frontRightLeg.pitch -= 1.4F;
        frontRightLeg.yaw += 0.3F;

        if (!state.submergedInWater) {
            frontLeftLeg.pitch -= 0.5F;
            frontRightLeg.pitch -= 0.5F;
        }

        if (!state.submergedInWater || state.onGround) {
            frontLeftLeg.yaw -= 0.5F;
            frontRightLeg.yaw += 0.5F;
        }
    }

    public static class Armour<T extends PonyRenderState> extends PonyArmourModel<T> {
        public Armour(ModelPart tree) {
            super(tree);
            rightLeg.hidden = true;
            leftLeg.hidden = true;
        }

        @Override
        protected void rotateLegs(T state) {
            walkSeapony(state, leftArm, rightArm, leftLeg, rightLeg);
        }

        @Override
        public void transform(T state, BodyPart part, MatrixStack stack) {
            stack.translate(0, 0.6F, 0);
            super.transform(state, part, stack);
        }
    }
}
