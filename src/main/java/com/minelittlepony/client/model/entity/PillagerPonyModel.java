package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;

import com.minelittlepony.client.model.entity.race.ChangelingModel;
import com.minelittlepony.client.render.entity.npc.PillagerRenderer;

public class PillagerPonyModel extends ChangelingModel<PillagerRenderer.State> {
    public PillagerPonyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    protected BipedEntityModel.ArmPose getArmPose(PlayerEntityRenderState state, Arm arm) {
        ArmPose holdingPose = getHoldingPose(((PillagerRenderer.State)state).state);

        if (holdingPose != ArmPose.EMPTY) {
            boolean isMain = state.mainArm == Arm.RIGHT;

            return isMain ? holdingPose : ArmPose.EMPTY;
        }

        return super.getArmPose(state, arm);
    }

    static ArmPose getHoldingPose(IllagerEntity.State state) {
        switch (state) {
            case BOW_AND_ARROW: return ArmPose.BOW_AND_ARROW;
            case CROSSBOW_CHARGE: return ArmPose.CROSSBOW_CHARGE;
            case CROSSBOW_HOLD: return ArmPose.CROSSBOW_HOLD;
            default: return ArmPose.EMPTY;
        }
    }
}
