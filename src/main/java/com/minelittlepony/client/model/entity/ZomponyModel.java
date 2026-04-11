package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

import com.minelittlepony.api.model.MobPosingHelper;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class ZomponyModel<T extends PonyRenderState> extends AlicornModel<T> {
    public ZomponyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    protected void rotateLegs(T state) {
        super.rotateLegs(state);
        if (shouldLiftBothArms(state)) {
            MobPosingHelper.rotateUndeadArms(state, this, state.walkAnimationPos, state.ageInTicks);
        }
    }

    protected boolean shouldLiftBothArms(T state) {
        return (state.mainArm == HumanoidArm.LEFT ? state.leftArmPose : state.rightArmPose) == ArmPose.EMPTY;
    }
}
