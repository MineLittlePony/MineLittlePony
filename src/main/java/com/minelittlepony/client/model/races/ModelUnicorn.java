package com.minelittlepony.client.model.races;

import com.minelittlepony.client.model.components.UnicornHorn;
import com.minelittlepony.client.util.render.Part;
import com.minelittlepony.model.IUnicorn;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

/**
 * Used for both unicorns and alicorns since there's no logical way to keep them distinct and not duplicate stuff.
 */
public class ModelUnicorn<T extends LivingEntity> extends ModelEarthPony<T> implements IUnicorn<Part> {

    public Part unicornArmRight;
    public Part unicornArmLeft;

    public UnicornHorn horn;

    public ModelUnicorn(boolean smallArms) {
        super(smallArms);
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        horn = new UnicornHorn(this, yOffset, stretch);
    }

    @Override
    public float getWobbleAmount() {
        if (isCasting()) {
            return 0;
        }
        return super.getWobbleAmount();
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, T entity) {
        super.rotateLegs(move, swing, ticks, entity);

        unicornArmRight.rotate(0, 0, 0).around(-7, 12, -2);
        unicornArmLeft.rotate(0, 0, 0).around(-7, 12, -2);
    }

    @Override
    protected void animateBreathing(float ticks) {
        if (canCast()) {
            float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
            float sin = MathHelper.sin(ticks * 0.067F) * 0.05F;

            if (rightArmPose != ArmPose.EMPTY) {
                unicornArmRight.roll += cos;
                unicornArmRight.pitch += sin;
            }

            if (leftArmPose != ArmPose.EMPTY) {
                unicornArmLeft.roll += cos;
                unicornArmLeft.pitch += sin;
            }
        } else {
            super.animateBreathing(ticks);
        }
    }

    @Override
    public Part getUnicornArmForSide(Arm side) {
        return side == Arm.LEFT ? unicornArmLeft : unicornArmRight;
    }

    @Override
    public boolean isCasting() {
        return rightArmPose != ArmPose.EMPTY || leftArmPose != ArmPose.EMPTY;
    }

    @Override
    protected void ponyCrouch() {
        super.ponyCrouch();
        unicornArmRight.pitch -= LEG_ROT_X_SNEAK_ADJ;
        unicornArmLeft.pitch -= LEG_ROT_X_SNEAK_ADJ;
    }

    @Override
    protected void renderHead(float scale) {
        super.renderHead(scale);

        if (hasHorn()) {
            head.applyTransform(scale);
            horn.renderPart(scale, attributes.interpolatorId);
            if (canCast() && isCasting()) {
                horn.renderMagic(getMagicColor(), scale);
            }
        }
    }

    @Override
    protected void initLegs(float yOffset, float stretch) {
        super.initLegs(yOffset, stretch);
        unicornArmLeft = new Part(this, 40, 32).size(64, 64);
        unicornArmRight = new Part(this, 40, 32).size(64, 64);

        int armLength = attributes.armLength;
        int armWidth = attributes.armWidth;
        int armDepth = attributes.armDepth;

        float rarmX = attributes.armRotationX;
        float rarmY = attributes.armRotationY;

        float armX = THIRDP_ARM_CENTRE_X;
        float armY = THIRDP_ARM_CENTRE_Y;
        float armZ = BODY_CENTRE_Z / 2 - 1 - armDepth;

        unicornArmLeft .box(armX, armY, armZ, armWidth, armLength, armDepth, stretch + .25F)
                        .around(rarmX, yOffset + rarmY, 0);
        unicornArmRight.box(armX - armWidth, armY, armZ, armWidth, armLength, armDepth, stretch + .25F)
                        .around(-rarmX, yOffset + rarmY, 0);
    }

    @Override
    public ModelPart getArm(Arm side) {
        if (canCast() && getArmPoseForSide(side) != ArmPose.EMPTY) {
            return getUnicornArmForSide(side);
        }
        return super.getArm(side);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        horn.setVisible(visible);
    }
}
