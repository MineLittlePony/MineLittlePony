package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.model.IUnicorn;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.model.MsonPart;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

/**
 * Used for both unicorns and alicorns since there's no logical way to keep them distinct and not duplicate stuff.
 */
public class ModelUnicorn<T extends LivingEntity> extends ModelEarthPony<T> implements IUnicorn<ModelPart> {

    protected ModelPart unicornArmRight;
    protected ModelPart unicornArmLeft;

    protected UnicornHorn horn;

    public ModelUnicorn(boolean smallArms) {
        super(smallArms);
    }

    @Override
    public void init(ModelContext context) {
        super.init(context);
        horn = context.findByName("horn");
        unicornArmRight = context.findByName("right_cast");
        unicornArmLeft = context.findByName("left_cast");
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

        ((MsonPart)unicornArmRight).rotate(0, 0, 0).around(-7, 12, -2);
        ((MsonPart)unicornArmLeft).rotate(0, 0, 0).around(-7, 12, -2);
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
    public ModelPart getUnicornArmForSide(Arm side) {
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
    protected void renderHead(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        super.renderHead(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);

        if (hasHorn()) {
            head.rotate(stack);
            horn.renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, attributes.interpolatorId);
            if (canCast() && isCasting()) {
                horn.renderMagic(stack, getMagicColor());
            }
        }
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
