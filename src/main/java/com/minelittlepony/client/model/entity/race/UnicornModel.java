package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;
import com.minelittlepony.mson.util.RenderList;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.util.*;

/**
 * Used for both unicorns and alicorns since there's no logical way to keep them distinct and not duplicate stuff.
 */
public class UnicornModel<T extends PonyRenderState> extends EarthPonyModel<T> implements HornedPonyModel<T> {

    protected final ModelPart unicornArmRight;
    protected final ModelPart unicornArmLeft;

    protected UnicornHorn<T> horn;

    public UnicornModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        unicornArmRight = tree.getChild("right_cast");
        unicornArmLeft = tree.getChild("left_cast");
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        horn = addPart(context.findByName("horn"));
        headRenderList.add(RenderList.of().add(head::rotate).add(forPart(horn)).checked(() -> currentState.getRace().hasHorn()));
        this.mainRenderList.add(withStage(BodyPart.HEAD, RenderList.of().add(head::rotate).add((stack, vertices, overlay, light, color) -> {
            horn.renderMagic(stack, vertices, currentState.attributes.metadata.glowColor());
        })).checked(() -> currentState.hasMagicGlow() && isCasting(currentState)));
    }

    @Override
    public float getWobbleAmount() {
        return isCasting(currentState) ? 0 : super.getWobbleAmount();
    }

    @Override
    protected void rotateLegs(T state, float move, float swing, float ticks) {
        super.rotateLegs(state, move, swing, ticks);

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

    @Override
    public ModelPart getArm(Arm side) {
        if (currentState.hasMagicGlow() && getArmPoseForSide(currentState, side) != ArmPose.EMPTY && PonyConfig.getInstance().tpsmagic.get()) {
            return side == Arm.LEFT ? unicornArmLeft : unicornArmRight;
        }
        return super.getArm(side);
    }

    @Override
    protected void positionheldItem(T state, Arm arm, MatrixStack matrices) {
        super.positionheldItem(state, arm, matrices);

        if (!PonyConfig.getInstance().tpsmagic.get() || !currentState.hasMagicGlow()) {
            return;
        }

        float left = arm == Arm.LEFT ? -1 : 1;

        matrices.translate(0.4F - (0.3F * left), -0.675F, -0.3F);

        UseAction action = state.attributes.heldStack.getUseAction();
        boolean shouldAimItem =
                (action == UseAction.SPYGLASS || action == UseAction.BOW) && state.attributes.itemUseTime > 0
                || PonyConfig.getInstance().forwardHoldingItems.get().contains(Registries.ITEM.getId(state.attributes.heldStack.getItem()));

        if (shouldAimItem) {
            Arm main = state.attributes.mainArm;
            if (state.attributes.activeHand == Hand.OFF_HAND) {
                main = main.getOpposite();
            }
            if (main == arm) {
                if (action == UseAction.SPYGLASS) {
                    Size size = state.getSize();
                    float x = 0.3F;
                    float z = -0.4F;

                    if (size == SizePreset.TALL || size == SizePreset.YEARLING) {
                        z += 0.05F;
                    } else if (size == SizePreset.FOAL) {
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
