package com.minelittlepony.model.player;

import com.minelittlepony.model.components.UnicornHorn;
import com.minelittlepony.render.PonyRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.capabilities.IModelUnicorn;

/**
 * Used for both unicorns and alicorns since there's no logical way to keep them distinct and not duplicate stuff.
 */
public class ModelAlicorn extends ModelPegasus implements IModelUnicorn {

    public PonyRenderer unicornArmRight;
    public PonyRenderer unicornArmLeft;

    public UnicornHorn horn;

    public ModelAlicorn(boolean smallArms) {
        super(smallArms);
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        horn = new UnicornHorn(this, yOffset, stretch);
    }

    @Override
    protected void rotateLegsOnGround(float move, float swing, float tick, Entity entity) {
        super.rotateLegsOnGround(move, swing, tick, entity);

        unicornArmRight.rotateAngleY = 0;
        unicornArmLeft.rotateAngleY = 0;
    }

    @Override
    protected void adjustLegs(float move, float swing, float tick) {
        super.adjustLegs(move, swing, tick);

        unicornArmLeft.rotateAngleZ = 0;
        unicornArmRight.rotateAngleZ = 0;

        unicornArmLeft.rotateAngleX = 0;
        unicornArmRight.rotateAngleX = 0;
    }

    @Override
    protected void holdItem(float swing) {
        if (canCast()) {
            boolean both = leftArmPose == ArmPose.ITEM && rightArmPose == ArmPose.ITEM;

            alignArmForAction(unicornArmLeft, leftArmPose, both, swing);
            alignArmForAction(unicornArmRight, rightArmPose, both, swing);
        } else {
            super.holdItem(swing);
        }
    }

    @Override
    protected void swingItem(Entity entity, float swingProgress) {
        if (canCast()) {
            if (swingProgress > -9990.0F && !isSleeping) {
                EnumHandSide mainSide = getMainHand(entity);

                if (getArmPoseForSide(mainSide) == ArmPose.EMPTY) return;
                swingArm(getUnicornArmForSide(mainSide));
            }
        } else {
            super.swingItem(entity, swingProgress);
        }
    }

    @Override
    protected void swingArms(float tick) {
        if (isSleeping) return;

        if (canCast()) {
            float cos = MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
            float sin = MathHelper.sin(tick * 0.067F) * 0.05F;

            if (rightArmPose != ArmPose.EMPTY) {
                unicornArmRight.rotateAngleZ += cos;
                unicornArmRight.rotateAngleX += sin;
            }

            if (leftArmPose != ArmPose.EMPTY) {
                unicornArmLeft.rotateAngleZ += cos;
                unicornArmLeft.rotateAngleX += sin;
            }
        } else {
            super.swingArms(tick);
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
    protected void sneakLegs() {
        super.sneakLegs();
        unicornArmRight.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        unicornArmLeft.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;
    }

    @Override
    protected void aimBow(ArmPose leftArm, ArmPose rightArm, float tick) {
        if (canCast()) {
            if (rightArm == ArmPose.BOW_AND_ARROW) aimBowPony(unicornArmRight, tick, true);
            if (leftArm == ArmPose.BOW_AND_ARROW) aimBowPony(unicornArmLeft, tick, false);
        } else {
            super.aimBow(leftArm, rightArm, tick);
        }
    }

    @Override
    protected void renderHead(Entity entity, float move, float swing, float age, float headYaw, float headPitch, float scale) {
        super.renderHead(entity, move, swing, age, headYaw, headPitch, scale);

        if (canCast()) {
            horn.render(scale);
            if (isCasting()) {
                horn.renderMagic(metadata.getGlowColor(), scale);
            }
        }
    }

    @Override
    protected void initLegTextures() {
        super.initLegTextures();
        unicornArmLeft = new PonyRenderer(this, 40, 32).size(64, 64);
        unicornArmRight = new PonyRenderer(this, 40, 32).size(64, 64);
        boxList.remove(unicornArmRight);
    }

    @Override
    protected void initLegPositions(float yOffset, float stretch) {
        super.initLegPositions(yOffset, stretch);
        float armY = THIRDP_ARM_CENTRE_Y - 6;
        float armZ = THIRDP_ARM_CENTRE_Z - 2;

        unicornArmLeft .box(FIRSTP_ARM_CENTRE_X - 2, armY, armZ, 4, 12, 4, stretch + .25f).around(5, yOffset + 2, 0);
        unicornArmRight.box(FIRSTP_ARM_CENTRE_X - 2, armY, armZ, 4, 12, 4, stretch + .25f).around(-5, yOffset + 2, 0);
    }
}
