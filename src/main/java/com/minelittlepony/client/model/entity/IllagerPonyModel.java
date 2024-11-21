package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.npc.IllagerPonyRenderer;

public class IllagerPonyModel<S extends IllagerPonyRenderer.State> extends AlicornModel<S> {

    public IllagerPonyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    public void setModelAngles(S state) {
        super.setModelAngles(state);
        IllagerEntity.State pose = state.state;

        boolean rightHanded = state.mainArm == Arm.RIGHT;
        float mult = rightHanded ? 1 : -1;
        ModelPart arm = getArm(state.mainArm);

        if (pose == IllagerEntity.State.ATTACKING) {
            // vindicator attacking
            float f = MathHelper.sin(state.getSwingAmount() * (float) Math.PI);
            float f1 = MathHelper.sin((1 - (1 - state.getSwingAmount()) * (1 - state.getSwingAmount())) * (float) Math.PI);

            float cos = MathHelper.cos(state.age * 0.09F) * 0.05F + 0.05F;
            float sin = MathHelper.sin(state.age * 0.067F) * 0.05F;

            rightArm.roll = cos;
            leftArm.roll  = cos;

            rightArm.yaw = 0.15707964F;
            leftArm.yaw = -0.15707964F;

            arm.pitch = -1.8849558F + MathHelper.cos(state.age * 0.09F) * 0.15F;
            arm.pitch += f * 2.2F - f1 * 0.4F;

            rightArm.pitch += sin;
            leftArm.pitch  -= sin;
        } else if (pose == IllagerEntity.State.SPELLCASTING) {
            // waving arms!
            // rightArm.rotationPointZ = 0;
            arm.pitch = (float) (-.75F * Math.PI);
            arm.roll = mult * MathHelper.cos(state.age * 0.6662F) / 4;
            arm.yaw = mult * 1.1F;
        } else if (pose == IllagerEntity.State.BOW_AND_ARROW) {
            aimBow(state, arm, state.age);
        }
    }
}
