package com.minelittlepony.model.player;

import com.minelittlepony.model.components.UnicornHorn;
import com.minelittlepony.render.model.PonyRenderer;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.model.capabilities.IModelUnicorn;

/**
 * Used for both unicorns and alicorns since there's no logical way to keep them distinct and not duplicate stuff.
 */
public class ModelUnicorn extends ModelEarthPony implements IModelUnicorn {

    public PonyRenderer unicornArmRight;
    public PonyRenderer unicornArmLeft;

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
    protected void rotateLegsOnGround(float move, float swing, float ticks, Entity entity) {
        super.rotateLegsOnGround(move, swing, ticks, entity);

        unicornArmRight.rotateAngleY = 0;
        unicornArmLeft.rotateAngleY = 0;
    }

    @Override
    public float getWobbleAmount() {
        if (isCasting()) {
            return 0;
        }
        return super.getWobbleAmount();
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, Entity entity) {
        super.rotateLegs(move, swing, ticks, entity);

        unicornArmRight.setRotationPoint(-7, 12, -2);
        unicornArmLeft.setRotationPoint(-7, 12, -2);

        unicornArmLeft.rotateAngleZ = 0;
        unicornArmRight.rotateAngleZ = 0;

        unicornArmLeft.rotateAngleX = 0;
        unicornArmRight.rotateAngleX = 0;
    }

    @Override
    protected void holdItem(float swing) {
        if (canCast()) {
            boolean both = leftArmPose == ArmPose.ITEM && rightArmPose == ArmPose.ITEM;

            alignArmForAction(unicornArmLeft, leftArmPose, rightArmPose, both, swing, 1);
            alignArmForAction(unicornArmRight, rightArmPose, leftArmPose, both, swing, -1);
        } else {
            super.holdItem(swing);
        }
    }

    @Override
    protected void swingItem(Entity entity) {
        EnumHandSide mainSide = getMainHand(entity);

        if (canCast() && getArmPoseForSide(mainSide) != ArmPose.EMPTY) {
            if (swingProgress > -9990.0F && !isSleeping()) {
                swingArm(getUnicornArmForSide(mainSide));
            }
        } else {
            super.swingItem(entity);
        }
    }

    @Override
    protected void swingArms(float ticks) {
        if (isSleeping()) return;

        if (canCast()) {
            float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
            float sin = MathHelper.sin(ticks * 0.067F) * 0.05F;

            if (rightArmPose != ArmPose.EMPTY) {
                unicornArmRight.rotateAngleZ += cos;
                unicornArmRight.rotateAngleX += sin;
            }

            if (leftArmPose != ArmPose.EMPTY) {
                unicornArmLeft.rotateAngleZ += cos;
                unicornArmLeft.rotateAngleX += sin;
            }
        } else {
            super.swingArms(ticks);
        }
    }

    @Override
    public PonyRenderer getUnicornArmForSide(EnumHandSide side) {
        return side == EnumHandSide.LEFT ? unicornArmLeft : unicornArmRight;
    }

    @Override
    public boolean canCast() {
        return metadata.hasMagic();
    }

    @Override
    public boolean isCasting() {
        return rightArmPose != ArmPose.EMPTY || leftArmPose != ArmPose.EMPTY;
    }

    @Override
    public int getMagicColor() {
        return metadata.getGlowColor();
    }

    @Override
    protected void ponyCrouch() {
        super.ponyCrouch();
        unicornArmRight.rotateAngleX -= LEG_ROT_X_SNEAK_ADJ;
        unicornArmLeft.rotateAngleX -= LEG_ROT_X_SNEAK_ADJ;
    }

    @Override
    protected void renderHead(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderHead(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (canCast()) {
            horn.renderPart(scale);
            if (isCasting()) {
                horn.renderMagic(getMagicColor(), scale);
            }
        }
    }

    @Override
    protected void initLegs(float yOffset, float stretch) {
        super.initLegs(yOffset, stretch);
        unicornArmLeft = new PonyRenderer(this, 40, 32).size(64, 64);
        unicornArmRight = new PonyRenderer(this, 40, 32).size(64, 64);

        int armLength = getArmLength();
        int armWidth = getArmWidth();
        int armDepth = getArmDepth();

        float rarmX = getLegRotationX();
        float rarmY = getArmRotationY();

        float armX = THIRDP_ARM_CENTRE_X;
        float armY = THIRDP_ARM_CENTRE_Y;
        float armZ = BODY_CENTRE_Z / 2 - 1 - armDepth;

        unicornArmLeft .box(armX, armY, armZ, armWidth, armLength, armDepth, stretch + .25F)
                        .around(rarmX, yOffset + rarmY, 0);
        unicornArmRight.box(armX - armWidth, armY, armZ, armWidth, armLength, armDepth, stretch + .25F)
                        .around(-rarmX, yOffset + rarmY, 0);
    }
}
