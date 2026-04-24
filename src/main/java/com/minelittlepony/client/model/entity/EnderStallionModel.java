package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.EnderStallionRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

public class EnderStallionModel extends AlicornModel<EnderStallionRenderer.State> {
    private final ModelPart leftHorn;
    private final ModelPart rightHorn;

    public EnderStallionModel(ModelPart tree) {
        super(tree, false);
        leftHorn = tree.getChild("left_horn");
        rightHorn = tree.getChild("right_horn");
    }

    @Override
    public void setModelAngles(EnderStallionRenderer.State state) {
        super.setModelAngles(state);
        leftSleeve.visible = false;
        rightSleeve.visible = false;

        leftPants.visible = false;
        rightPants.visible = false;
        tail.setHidden();
        snout.setHidden();
        if (state.isBoss) {
            horn.setHidden();
        }
        leftHorn.visible = rightHorn.visible = state.isBoss;
        hat.visible = state.isAttacking;

        if (state.isAttacking) {
            head.y -= 5;
        }
    }

    @Override
    public void transform(EnderStallionRenderer.State state, BodyPart part, PoseStack stack) {
        if (part != BodyPart.WINGS) {
            stack.translate(0, -1.15F, 0);
        }
        super.transform(state, part, stack);
    }
}
