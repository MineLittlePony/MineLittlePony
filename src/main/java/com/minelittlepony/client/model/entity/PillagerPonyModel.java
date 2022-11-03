package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Arm;

import com.minelittlepony.client.model.entity.race.ChangelingModel;

public class PillagerPonyModel<T extends PillagerEntity> extends ChangelingModel<T> {

    public PillagerPonyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    public void animateModel(T entity, float move, float swing, float ticks) {
        ArmPose holdingPose = getHoldingPose(entity.getState());

        if (holdingPose != ArmPose.EMPTY) {
            boolean rightHanded = entity.getMainArm() == Arm.RIGHT;

            leftArmPose = rightHanded ? ArmPose.EMPTY : holdingPose;
            rightArmPose = rightHanded ? holdingPose : ArmPose.EMPTY;
        }
    }

    protected ArmPose getHoldingPose(IllagerEntity.State state) {
        switch (state) {
            case BOW_AND_ARROW: return ArmPose.BOW_AND_ARROW;
            case CROSSBOW_CHARGE: return ArmPose.CROSSBOW_CHARGE;
            case CROSSBOW_HOLD: return ArmPose.CROSSBOW_HOLD;
            default: return ArmPose.EMPTY;
        }
    }
}
