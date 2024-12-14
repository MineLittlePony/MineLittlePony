package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;
import com.minelittlepony.mson.util.RenderList;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.util.*;

/**
 * Used for both unicorns and alicorns since there's no logical way to keep them distinct and not duplicate stuff.
 */
public class UnicornModel<T extends PonyRenderState> extends EarthPonyModel<T> {

    protected final ModelPart unicornArmRight;
    protected final ModelPart unicornArmLeft;

    protected UnicornHorn<T> horn;

    @SuppressWarnings("deprecation")
    public UnicornModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        unicornArmRight = tree.getChild("right_cast");
        unicornArmLeft = tree.getChild("left_cast");
        headRenderList.add(RenderList.of().add(head::rotate).add(SubModel.toRenderList(() -> horn)));
        mainRenderList.add(withStage(BodyPart.HEAD, RenderList.of().add(head::rotate).add((stack, vertices, overlay, light, color) -> {
            if (isCasting(currentState)) {
                horn.renderMagic(stack, vertices, currentState == null ? 0 : currentState.attributes.metadata.glowColor());
            }
        })));
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        horn = addPart(context.findByName("horn"));
    }

    public boolean isCasting(T state) {
        return state instanceof PlayerEntityRenderState s
                && (getArmPose(s, Arm.LEFT) != ArmPose.EMPTY || getArmPose(s, Arm.RIGHT) != ArmPose.EMPTY);
    }

    @Override
    public float getWobbleAmplitude(T state) {
        return isCasting(state) ? 0 : 1;
    }

    @Override
    protected void rotateLegs(T state) {
        super.rotateLegs(state);

        unicornArmRight.setAngles(0, 0, 0);
        unicornArmRight.setPivot(-7, 12, -2);

        unicornArmLeft.setAngles(0, 0, 0);
        unicornArmLeft.setPivot(-7, 12, -2);
    }

    @Override
    protected void ponyCrouch(T state) {
        super.ponyCrouch(state);
        unicornArmRight.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
        unicornArmLeft.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ModelPart getArm(Arm side) {
        if (currentState != null && currentState.hasMagicGlow() && getArmPose(currentState, side) != ArmPose.EMPTY && PonyConfig.getInstance().tpsmagic.get()) {
            return side == Arm.LEFT ? unicornArmLeft : unicornArmRight;
        }
        return super.getArm(side);
    }

    @Override
    public void positionheldItem(T state, Arm arm, MatrixStack matrices) {
        super.positionheldItem(state, arm, matrices);

        if (!PonyConfig.getInstance().tpsmagic.get() || !state.hasMagicGlow()) {
            return;
        }

        float left = arm == Arm.LEFT ? -1 : 1;

        UseAction action = state.attributes.heldStack.getUseAction();
        if (action == UseAction.SPYGLASS && state.attributes.itemUseTime > 0) {
            return;
        }

        matrices.translate(0.4F - (0.3F * left), -0.675F, -0.3F);

        boolean shouldAimItem =
                (action == UseAction.BOW) && state.attributes.itemUseTime > 0
                || PonyConfig.getInstance().forwardHoldingItems.get().contains(Registries.ITEM.getId(state.attributes.heldStack.getItem()));

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
