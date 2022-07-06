package com.minelittlepony.client.model.entity;

import com.minelittlepony.client.model.IMobModel;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.HostileEntity;

public class ZomponyModel<Zombie extends HostileEntity> extends AlicornModel<Zombie> implements IMobModel {

    private boolean isPegasus;

    public ZomponyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    public void animateModel(Zombie entity, float move, float swing, float ticks) {
        super.animateModel(entity, move, swing, ticks);
        isPegasus = entity.getUuid().getLeastSignificantBits() % 30 == 0;
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, Zombie entity) {
        super.rotateLegs(move, swing, ticks, entity);
        if (isZombified(entity)) {
            IMobModel.rotateUndeadArms(this, move, ticks);
        }
    }

    @Override
    public boolean canFly() {
        return isPegasus;
    }

    protected boolean isZombified(Zombie entity) {
        return rightArmPose == ArmPose.EMPTY;
    }
}
