package com.minelittlepony.client.model.entity;

import com.minelittlepony.api.model.MobPosingHelper;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.ZomponyRenderer;

import net.minecraft.client.model.ModelPart;

public class ZomponyModel<T extends ZomponyRenderer.State> extends AlicornModel<T> {
    public ZomponyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    protected void rotateLegs(T state, float move, float swing, float ticks) {
        super.rotateLegs(state, move, swing, ticks);
        if (shouldLiftBothArms(state)) {
            MobPosingHelper.rotateUndeadArms(state, this, state.limbFrequency, state.age);
        }
    }

    protected boolean shouldLiftBothArms(T state) {
        return getArmPose(state, state.mainArm) == ArmPose.EMPTY;
    }
}
