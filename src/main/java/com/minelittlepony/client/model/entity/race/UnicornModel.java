package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.*;

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
        headRenderList.add(head::applyTransform).add(horn);
    }

    @Override
    protected void rotateLegs(T state) {
        super.rotateLegs(state);

        unicornArmRight.setAngles(0, 0, 0);
        unicornArmRight.setOrigin(-7, 12, -2);

        unicornArmLeft.setAngles(0, 0, 0);
        unicornArmLeft.setOrigin(-7, 12, -2);
    }

    @Override
    protected void ponyCrouch(T state) {
        super.ponyCrouch(state);
        unicornArmRight.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
        unicornArmLeft.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
    }

    @Override
    protected void setModelAngles(T state) {
        usingUnicornArmLeft = PonyConfig.getInstance().tpsmagic.get() && state.hasMagicGlow() && state.leftArmPose != ArmPose.EMPTY;
        usingUnicornArmRight = PonyConfig.getInstance().tpsmagic.get() && state.hasMagicGlow() && state.rightArmPose != ArmPose.EMPTY;
        super.setModelAngles(state);
    }

    @Override
    public ModelPart getArm(Arm side) {
        if ((side == Arm.LEFT ? usingUnicornArmLeft : usingUnicornArmRight)) {
            return side == Arm.LEFT ? unicornArmLeft : unicornArmRight;
        }
        return super.getArm(side);
    }

    @Override
    protected void positionheldItem(T state, Arm arm, MatrixStack matrices) {
        super.positionheldItem(state, arm, matrices);

        if (!PonyConfig.getInstance().tpsmagic.get() || !state.hasMagicGlow()) {
            return;
        }

        float left = arm == Arm.LEFT ? -1 : 1;

        UseAction action = state.getHeldItem(arm).action;
        if (action == UseAction.SPYGLASS && state.attributes.itemUseTime > 0) {
            return;
        }

        matrices.translate(0.4F - (0.3F * left), -0.675F, -0.3F);

        boolean shouldAimItem = action == UseAction.BOW && state.attributes.itemUseTime > 0 || state.getHeldItem(arm).forwardFacing;

        if (shouldAimItem) {
            Arm main = state.attributes.mainArm;
            if (state.attributes.activeHand == Hand.OFF_HAND) {
                main = main.getOpposite();
            }
            if (main == arm) {
                if (action == UseAction.SPYGLASS) {
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
