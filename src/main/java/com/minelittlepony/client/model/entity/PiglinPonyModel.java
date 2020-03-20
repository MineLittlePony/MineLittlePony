package com.minelittlepony.client.model.entity;

import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinEntity;

import com.minelittlepony.client.model.entity.race.AlicornModel;

public class PiglinPonyModel extends AlicornModel<HostileEntity> {

    private boolean isPegasus;

    public PiglinPonyModel() {
        super(false);
    }

    @Override
    public void animateModel(HostileEntity entity, float move, float swing, float ticks) {
        isPegasus = entity.getUuid().getLeastSignificantBits() % 30 == 0;

        if (entity instanceof PiglinEntity) {
            PiglinEntity piglinEntity = (PiglinEntity)entity;
            PiglinEntity.Activity activity = piglinEntity.getActivity();

            leftArmPose = ArmPose.EMPTY;
            rightArmPose = ArmPose.EMPTY;

            if (activity == PiglinEntity.Activity.CROSSBOW_HOLD) {
                rightArmPose = ArmPose.CROSSBOW_HOLD;
            } else if (activity == PiglinEntity.Activity.CROSSBOW_CHARGE) {
                rightArmPose = ArmPose.BOW_AND_ARROW;
            } else if (activity == PiglinEntity.Activity.ADMIRING_ITEM) {
               leftArmPose = ArmPose.ITEM;
            }
         }
    }

    @Override
    public boolean canFly() {
        return isPegasus;
    }
}
