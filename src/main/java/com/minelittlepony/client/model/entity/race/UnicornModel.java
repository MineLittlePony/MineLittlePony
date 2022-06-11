package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IUnicorn;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.mson.api.ModelContext;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

/**
 * Used for both unicorns and alicorns since there's no logical way to keep them distinct and not duplicate stuff.
 */
public class UnicornModel<T extends LivingEntity> extends EarthPonyModel<T> implements IUnicorn {

    protected final ModelPart unicornArmRight;
    protected final ModelPart unicornArmLeft;

    protected UnicornHorn horn;

    public UnicornModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        unicornArmRight = tree.getChild("right_cast");
        unicornArmLeft = tree.getChild("left_cast");
    }

    @Override
    public void init(ModelContext context) {
        super.init(context);
        horn = context.findByName("horn");
    }

    @Override
    public float getWobbleAmount() {
        return isCasting() ? 0 : super.getWobbleAmount();
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, T entity) {
        super.rotateLegs(move, swing, ticks, entity);

        unicornArmRight.setAngles(0, 0, 0);
        unicornArmRight.setPivot(-7, 12, -2);

        unicornArmLeft.setAngles(0, 0, 0);
        unicornArmLeft.setPivot(-7, 12, -2);
    }

    @Override
    public boolean isCasting() {
        return MineLittlePony.getInstance().getConfig().tpsmagic.get()
                && (rightArmPose != ArmPose.EMPTY || leftArmPose != ArmPose.EMPTY);
    }

    @Override
    protected void ponyCrouch() {
        super.ponyCrouch();
        unicornArmRight.pitch -= LEG_ROT_X_SNEAK_ADJ;
        unicornArmLeft.pitch -= LEG_ROT_X_SNEAK_ADJ;
    }

    @Override
    public void renderHead(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        super.renderHead(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);

        if (hasHorn()) {
            head.rotate(stack);
            horn.renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, attributes.interpolatorId);
        }
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        super.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);

        if (hasHorn() && canCast() && isCasting()) {
            stack.push();
            transform(BodyPart.HEAD, stack);
            head.rotate(stack);
            horn.renderMagic(stack, vertices, getMagicColor());
            stack.pop();
        }
    }

    @Override
    public ModelPart getArm(Arm side) {
        if (canCast() && getArmPoseForSide(side) != ArmPose.EMPTY && MineLittlePony.getInstance().getConfig().tpsmagic.get()) {
            return side == Arm.LEFT ? unicornArmLeft : unicornArmRight;
        }
        return super.getArm(side);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        horn.setVisible(visible);
    }
}
