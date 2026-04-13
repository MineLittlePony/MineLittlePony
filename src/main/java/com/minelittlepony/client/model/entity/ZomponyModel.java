package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

import com.minelittlepony.api.model.MobPosingHelper;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.state.HostilePonyRenderState;

public class ZomponyModel<T extends HostilePonyRenderState> extends AlicornModel<T> {
    public ZomponyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    protected void rotateLegs(T state) {
        super.rotateLegs(state);
        if (shouldAnimateAsZombie(state)) {
            MobPosingHelper.animateZombieArms(getArm(HumanoidArm.LEFT), getArm(HumanoidArm.RIGHT), state.aggressive, state);
        }
    }

    protected boolean shouldAnimateAsZombie(T state) {
        return true;
    }
}
