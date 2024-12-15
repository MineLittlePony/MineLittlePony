package com.minelittlepony.client.model.entity;

import com.minelittlepony.api.model.MobPosingHelper;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Arm;

public class ZomponyModel<T extends PonyRenderState> extends AlicornModel<T> {
    public ZomponyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    protected void rotateLegs(T state) {
        super.rotateLegs(state);
        if (shouldLiftBothArms(state)) {
            MobPosingHelper.rotateUndeadArms(state, this, state.limbFrequency, state.age);
        }
    }

    protected boolean shouldLiftBothArms(T state) {
        return (state.mainArm == Arm.LEFT ? state.leftArmPose : state.rightArmPose) == ArmPose.EMPTY;
    }
}
