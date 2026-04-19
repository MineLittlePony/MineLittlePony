package com.minelittlepony.client.model.entity.race;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemUseAnimation;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Used for both unicorns and alicorns since there's no logical way to keep them distinct and not duplicate stuff.
 */
public class UnicornModel<T extends PonyRenderState> extends EarthPonyModel<T> implements ModelWithHorn<T> {

    protected final ModelPart unicornArmRight;
    protected final ModelPart unicornArmLeft;

    private boolean usingUnicornArmLeft;
    private boolean usingUnicornArmRight;

    protected UnicornHorn<T> horn;

    public UnicornModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        unicornArmRight = tree.getChild("right_cast");
        unicornArmLeft = tree.getChild("left_cast");
    }

    @Override
    public SubModel<T> getHorn() {
        return horn;
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        horn = addPart(context.findByName("horn"));
        headRenderList.add(head::translateAndRotate).add(horn);
    }

    @Override
    protected void rotateLegs(T state) {
        super.rotateLegs(state);

        unicornArmRight.setRotation(0, 0, 0);
        unicornArmRight.setPos(-7, 12, -2);

        unicornArmLeft.setRotation(0, 0, 0);
        unicornArmLeft.setPos(-7, 12, -2);
    }

    @Override
    protected void ponyCrouch(T state) {
        super.ponyCrouch(state);
        unicornArmRight.xRot -= LEG_SNEAKING_PITCH_ADJUSTMENT;
        unicornArmLeft.xRot -= LEG_SNEAKING_PITCH_ADJUSTMENT;
    }

    @Override
    protected void setModelAngles(T state) {
        usingUnicornArmLeft = PonyConfig.getInstance().tpsmagic.get() && state.hasMagicGlow() && state.leftArmPose != ArmPose.EMPTY;
        usingUnicornArmRight = PonyConfig.getInstance().tpsmagic.get() && state.hasMagicGlow() && state.rightArmPose != ArmPose.EMPTY;
        super.setModelAngles(state);
    }

    @Override
    public ModelPart getArm(HumanoidArm side) {
        if ((side == HumanoidArm.LEFT ? usingUnicornArmLeft : usingUnicornArmRight)) {
            return side == HumanoidArm.LEFT ? unicornArmLeft : unicornArmRight;
        }
        return super.getArm(side);
    }

    @Override
    protected void positionheldItem(T state, HumanoidArm arm, PoseStack matrices) {
        super.positionheldItem(state, arm, matrices);

        if (!PonyConfig.getInstance().tpsmagic.get() || !state.hasMagicGlow()) {
            return;
        }

        float left = arm == HumanoidArm.LEFT ? -1 : 1;

        ItemUseAnimation action = state.getHeldItem(arm).action;
        if (action == ItemUseAnimation.SPYGLASS && state.attributes.itemUseTime > 0) {
            return;
        }

        matrices.translate(0.4F - (0.3F * left), -0.675F, -0.3F);

        boolean shouldAimItem = action == ItemUseAnimation.BOW && state.attributes.itemUseTime > 0 || state.getHeldItem(arm).forwardFacing;

        if (action == ItemUseAnimation.TRIDENT && state.getArmPoseForArm(arm) == ArmPose.THROW_TRIDENT) {
            matrices.translate(0, 2, 0);
        }

        if (shouldAimItem) {
            HumanoidArm main = state.attributes.mainArm;
            if (state.attributes.activeHand == InteractionHand.OFF_HAND) {
                main = main.getOpposite();
            }
            if (main == arm) {
                if (action == ItemUseAnimation.SPYGLASS) {
                    float x = 0.3F;
                    float z = -0.4F;

                    if (state.attributes.size == SizePreset.TALL || state.attributes.size == SizePreset.YEARLING) {
                        z += 0.05F;
                    } else if (state.attributes.size == SizePreset.FOAL) {
                        x -= 0.1F;
                        z -= 0.1F;
                    }

                    matrices.translate(x * left, 1, -z);
                } else {
                    matrices.translate(-0.6, -0.2, 0);
                }
            }
        }
    }
}
