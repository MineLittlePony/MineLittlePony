package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IUnicorn;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.client.util.render.RenderList;
import com.minelittlepony.mson.api.ModelView;

import net.minecraft.client.model.ModelPart;
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
    public void init(ModelView context) {
        super.init(context);
        horn = addPart(context.findByName("horn"));
        headRenderList.add(RenderList.of().add(head::rotate).add(forPart(horn)).checked(this::hasHorn));
        this.mainRenderList.add(withStage(BodyPart.HEAD, RenderList.of().add(head::rotate).add((stack, vertices, overlayUv, lightUv, red, green, blue, alpha) -> {
            horn.renderMagic(stack, vertices, getMagicColor());
        })).checked(() -> hasHorn() && hasMagic() && isCasting()));
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
        unicornArmRight.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
        unicornArmLeft.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
    }

    @Override
    public ModelPart getArm(Arm side) {
        if (hasMagic() && getArmPoseForSide(side) != ArmPose.EMPTY && MineLittlePony.getInstance().getConfig().tpsmagic.get()) {
            return side == Arm.LEFT ? unicornArmLeft : unicornArmRight;
        }
        return super.getArm(side);
    }
}
