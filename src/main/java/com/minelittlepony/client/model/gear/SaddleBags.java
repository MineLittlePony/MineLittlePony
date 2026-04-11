package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.ModelWithWings;
import com.minelittlepony.api.model.gear.WearableGear;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.util.MathUtil;

public class SaddleBags<T extends HumanoidRenderState & PonyModel.AttributedHolder> extends WearableGear<T> {
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
    public void setupAnim(GearRenderState<T> state) {
        super.setupAnim(state);

        boolean hangLow = state.model instanceof ModelWithWings pegasus && pegasus.wingsAreOpen(state.entityState);

        float pi = Mth.PI * (float) Math.pow(state.limbAngle, 16);

        float mve = state.limbDistance * 0.6662f;
        float srt = state.limbAngle * Mth.DEG_TO_RAD / 10;

        float bodySwing = Mth.cos(mve + pi) * srt;

        leftBag.xRot = bodySwing;
        rightBag.xRot = bodySwing;

        if (state.model instanceof ModelWithWings pegasus && state.entityState.getAttributes().isFlying) {
            bodySwing = pegasus.getWingRotationFactor(state.entityState) - MathUtil.Angles._270_DEG;
            bodySwing /= 10;
        }

        leftBag.zRot = bodySwing;
        rightBag.zRot = -bodySwing;

        leftBag.visible = wearable == Wearable.SADDLE_BAGS_BOTH || wearable == Wearable.SADDLE_BAGS_LEFT;
        rightBag.visible = wearable == Wearable.SADDLE_BAGS_BOTH || wearable == Wearable.SADDLE_BAGS_RIGHT;
        strap.visible = wearable == Wearable.SADDLE_BAGS_BOTH;

        float dropAmount = hangLow ? 0.15F : 0;
        dropAmount = state.entityState.getAttributes().getMainInterpolator().interpolate("dropAmount", dropAmount, 3);

        if (wearable == Wearable.SADDLE_BAGS_BOTH) {
            root().z -= 0.2F;
        }

        leftBag.y += dropAmount;
        rightBag.y += dropAmount;

        if (wearable != Wearable.SADDLE_BAGS_BOTH) {
            leftBag.y += 0.3F;
            rightBag.y += 0.3F;
            leftBag.z -= 0.3F;
            rightBag.z -= 0.3F;
        }
    }
}
