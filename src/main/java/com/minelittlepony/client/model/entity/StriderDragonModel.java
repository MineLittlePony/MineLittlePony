package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.client.render.entity.StriderRenderer;

public class StriderDragonModel extends DragonModel<StriderRenderer.State> {

    public StriderDragonModel(ModelPart tree) {
        super(tree);
    }

    @Override
    public void setupAnim(StriderRenderer.State state) {
        super.setupAnim(state);

        body.xRot += 0.15F;

        if (!state.saddleStack.isEmpty()) {
            leftArm.xRot = 3.15F;
            leftArm.yRot = 1;
            rightArm.xRot = 3.15F;
            rightArm.yRot = -1;

            head.y += 4;
            head.z = -3;
            hat.y += 4;
            hat.z = -3;

            leftLeg.xRot += 0.4F;
            rightLeg.xRot += 0.4F;
        } else {
            leftArm.yRot -= 0.2F * state.flailAmount;
            rightArm.yRot += 0.2F * state.flailAmount;

            leftArm.z += 2;
            leftArm.xRot -= 0.3F;

            rightArm.z += 2;
            rightArm.xRot -= 0.3F;

            if (state.cold) {
                float armMotion = (float)Math.sin(state.ageInTicks * 0.1F) * 0.1F;

                leftArm.xRot = -1 - armMotion;
                rightArm.xRot = -1 + armMotion;

                leftArm.yRot = 0.8F;
                rightArm.yRot = -0.8F;

                leftArm.z -= 3;
                rightArm.z -= 3;
            }
        }

        tail.xRot = (float)Math.sin(state.walkAnimationSpeed) / 3F - 0.5F;
        tail2.xRot = -tail.xRot * 0.5F;
        tail3.xRot = tail2.xRot * 0.5F;

        tail.yRot = (float)Math.sin(state.ageInTicks / 20F) / 40 + (float)Math.sin(state.walkAnimationSpeed / 20F) * 0.25F;
        tail2.yRot = tail.yRot * 0.5F;
        tail3.yRot = tail2.yRot * 0.5F;
    }
}
