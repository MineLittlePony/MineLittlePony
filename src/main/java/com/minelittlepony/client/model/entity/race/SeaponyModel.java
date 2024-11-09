package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.mson.api.ModelView;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
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
        bodyRenderList.add(body).add(body::rotate).add(forPart(tail)).add(leftFin, centerFin, rightFin);
    }

    @Override
    public void updateLivingState(T entity, Pony pony, ModelAttributes.Mode mode) {
        super.updateLivingState(entity, pony, mode);

        // Seaponies can't sneak, silly
        sneaking = false;
        attributes.isCrouching = false;
    }

    @Override
    protected void ponySleep() {}

    @Override
    protected void ponySit() {}

    @Override
    protected void setModelAngles(T entity) {
        super.setModelAngles(entity);

        float flapMotion = MathHelper.cos(entity.age / 10) / 5;

        if (entity.attributes.isLyingDown) {
            flapMotion /= 2;
        }

        float finAngle = FIN_Y_ANGLE + flapMotion;

        leftFin.yaw = finAngle;
        rightFin.yaw = -finAngle;
        centerFin.roll = flapMotion;

        if (!entity.submergedInWater) {
            leftArm.pitch -= 0.5F;
            rightArm.pitch -= 0.5F;
        }

        if (!entity.submergedInWater || entity.onGround) {
            leftArm.yaw -= 0.5F;
            rightArm.yaw += 0.5F;
        }
    }

    @Override
    protected void rotateLegs(T state, float move, float swing, float ticks, T entity) {
        super.rotateLegs(state, move, swing, ticks, entity);
        leftArm.pitch -= 1.4F;
        leftArm.yaw -= 0.3F;
        rightArm.pitch -= 1.4F;
        rightArm.yaw += 0.3F;
    }

    @Override
    protected void rotateLegsSwimming(T state, float move, float swing, float ticks, T entity) {
        super.rotateLegsOnGround(state, move, swing, ticks, entity);
    }

    @Override
    public void transform(BodyPart part, MatrixStack stack) {
        stack.translate(0, 0.6F, 0);
        super.transform(part, stack);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        leftFin.visible = visible;
        centerFin.visible = visible;
        rightFin.visible = visible;
    }

    public static class Armour<T extends PonyRenderState> extends PonyArmourModel<T> {

        public Armour(ModelPart tree) {
            super(tree);
            rightLeg.hidden = true;
            leftLeg.hidden = true;
        }

        @Override
        public void updateLivingState(T entity, Pony pony, ModelAttributes.Mode mode) {
            super.updateLivingState(entity, pony, mode);

            // Seaponies can't sneak, silly
            sneaking = false;
        }

        @Override
        protected void rotateLegsSwimming(T state, float move, float swing, float ticks, T entity) {
            super.rotateLegsOnGround(state, move, swing, ticks, entity);
        }

        @Override
        public void transform(BodyPart part, MatrixStack stack) {
            stack.translate(0, 0.6F, 0);
            super.transform(part, stack);
        }
    }
}
