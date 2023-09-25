package com.minelittlepony.client.model.entity;

import com.minelittlepony.api.model.MobPosingHelper;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.HostileEntity;

public class ZomponyModel<Zombie extends HostileEntity> extends AlicornModel<Zombie> {

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
            MobPosingHelper.rotateUndeadArms(this, move, ticks);
        }
    }

    @Override
    public Race getRace() {
        return isPegasus ? (super.getRace().hasHorn() ? Race.ALICORN : Race.PEGASUS) : super.getRace();
    }

    protected boolean isZombified(Zombie entity) {
        return rightArmPose == ArmPose.EMPTY;
    }
}
