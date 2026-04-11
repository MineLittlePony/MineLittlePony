package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.illager.AbstractIllager;

import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.npc.IllagerPonyRenderer;

public class IllagerPonyModel<S extends IllagerPonyRenderer.State> extends AlicornModel<S> {

    public IllagerPonyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    public void setModelAngles(S state) {
        super.setModelAngles(state);
        AbstractIllager.IllagerArmPose pose = state.state;

        boolean rightHanded = state.mainArm == HumanoidArm.RIGHT;
        float mult = rightHanded ? 1 : -1;
        ModelPart arm = getArm(state.mainArm);

        if (pose == AbstractIllager.IllagerArmPose.ATTACKING) {
            // vindicator attacking
            float f = Mth.sin(state.attackTime * (float) Math.PI);
            float f1 = Mth.sin((1 - (1 - state.attackTime) * (1 - state.attackTime)) * (float) Math.PI);

            float cos = Mth.cos(state.ageInTicks * 0.09F) * 0.05F + 0.05F;
            float sin = Mth.sin(state.ageInTicks * 0.067F) * 0.05F;

            rightArm.zRot = cos;
            leftArm.zRot  = cos;

            rightArm.yRot = 0.15707964F;
            leftArm.yRot = -0.15707964F;

            arm.xRot = -1.8849558F + Mth.cos(state.ageInTicks * 0.09F) * 0.15F;
            arm.xRot += f * 2.2F - f1 * 0.4F;

            rightArm.xRot += sin;
            leftArm.xRot  -= sin;
        } else if (pose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            // waving arms!
            // rightArm.rotationPointZ = 0;
            arm.setRotation(-0.75F * Mth.PI, mult * 1.1F, mult * Mth.cos(state.ageInTicks * 0.6662F) / 4);
        } else if (pose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
            aimBow(state, arm);
        }
    }
}
