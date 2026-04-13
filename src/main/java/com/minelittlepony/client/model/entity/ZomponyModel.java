package com.minelittlepony.client.model.entity;

import com.minelittlepony.api.model.MobPosingHelper;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.state.HostilePonyRenderState;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Arm;

public class ZomponyModel<T extends HostilePonyRenderState> extends AlicornModel<T> {
    public ZomponyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    protected void rotateLegs(T state) {
        super.rotateLegs(state);
        if (shouldAnimateAsZombie(state)) {
            MobPosingHelper.animateZombieArms(getArm(Arm.LEFT), getArm(Arm.RIGHT), state.aggressive, state);
        }
    }

    protected boolean shouldAnimateAsZombie(T state) {
        return true;
    }
}
