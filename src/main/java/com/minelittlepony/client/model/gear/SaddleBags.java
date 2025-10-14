package com.minelittlepony.client.model.gear;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.WingedPonyModel;
import com.minelittlepony.api.model.gear.WearableGear;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.util.MathUtil;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.math.MathHelper;

public class SaddleBags<T extends BipedEntityRenderState & PonyModel.AttributedHolder> extends WearableGear<T> {
    private final ModelPart leftBag;
    private final ModelPart rightBag;

    private final ModelPart strap;

    public SaddleBags(ModelPart tree, Wearable wearable) {
        super(tree, wearable, BodyPart.BODY, 0);
        strap = tree.getChild("strap");
        leftBag = tree.getChild("left_bag");
        rightBag = tree.getChild("right_bag");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setAngles(GearRenderState<T> state) {
        boolean hangLow = state.model instanceof WingedPonyModel pegasus && pegasus.wingsAreOpen(state.entityState);

        float pi = MathHelper.PI * (float) Math.pow(state.limbDistance, 16);

        float mve = state.limbDistance * 0.6662f;
        float srt = state.limbAngle / 10;

        float bodySwing = MathHelper.cos(mve + pi) * srt;

        leftBag.pitch = bodySwing;
        rightBag.pitch = bodySwing;

        if (state.model instanceof WingedPonyModel pegasus && state.entityState.getAttributes().isFlying) {
            bodySwing = pegasus.getWingRotationFactor(state.entityState) - MathUtil.Angles._270_DEG;
            bodySwing /= 10;
        }

        leftBag.roll = bodySwing;
        rightBag.roll = -bodySwing;

        leftBag.visible = wearable == Wearable.SADDLE_BAGS_BOTH || wearable == Wearable.SADDLE_BAGS_LEFT;
        rightBag.visible = wearable == Wearable.SADDLE_BAGS_BOTH || wearable == Wearable.SADDLE_BAGS_RIGHT;
        strap.visible = wearable == Wearable.SADDLE_BAGS_BOTH;

        float dropAmount = hangLow ? 0.15F : 0;
        dropAmount = state.entityState.getAttributes().getMainInterpolator().interpolate("dropAmount", dropAmount, 3);

        if (wearable == Wearable.SADDLE_BAGS_BOTH) {
            getRootPart().originZ -= 0.2F;
        }

        leftBag.originY += dropAmount;
        rightBag.originY += dropAmount;

        if (wearable != Wearable.SADDLE_BAGS_BOTH) {
            leftBag.originY += 0.3F;
            rightBag.originY += 0.3F;
            leftBag.originZ -= 0.3F;
            rightBag.originZ -= 0.3F;
        }
    }
}
