package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.Sizes;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.client.render.PonyRenderDispatcher;
import com.minelittlepony.client.util.render.RenderList;
import com.minelittlepony.mson.api.ModelView;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

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

    @Override
    protected void positionheldItem(Arm arm, MatrixStack matrices) {
        super.positionheldItem(arm, matrices);

        if (!MineLittlePony.getInstance().getConfig().tpsmagic.get() || !hasMagic()) {
            return;
        }

        float left = arm == Arm.LEFT ? -1 : 1;

        matrices.translate(-0.3F, -0.675F, -0.3F - (0.3F * left));

        UseAction action = getAttributes().heldStack.getUseAction();

        if ((action == UseAction.SPYGLASS || action == UseAction.BOW) && getAttributes().itemUseTime > 0) {
            Arm main = getAttributes().mainArm;
            if (getAttributes().activeHand == Hand.OFF_HAND) {
                main = main.getOpposite();
            }
            if (main == arm) {
                if (action == UseAction.SPYGLASS) {
                    Size size = getSize();
                    float x = 0.4F;
                    float z = -0.5F;

                    if (size == Sizes.TALL || size == Sizes.YEARLING) {
                        z += 0.05F;
                    } else if (size == Sizes.FOAL) {
                        x -= 0.1F;
                        z -= 0.1F;
                    }

                    //matrices.translate(x * left, -0.2, z);
                    matrices.translate(-z, 0.9, x * left);
                } else {
                    matrices.translate(-0.6, -0.2, 0);
                }
            }
        }
    }

    @Override
    public void postItemRender(IPony pony, T entity, ItemStack drop, ModelTransformationMode transform, Arm hand, MatrixStack stack, VertexConsumerProvider renderContext) {
        if (!MineLittlePony.getInstance().getConfig().tpsmagic.get() || !hasMagic()) {
            return;
        }

        PonyRenderDispatcher.getInstance().getMagicRenderer().renderItemGlow(pony, entity, drop, transform, hand, getMagicColor(), stack, renderContext);
    }
}
