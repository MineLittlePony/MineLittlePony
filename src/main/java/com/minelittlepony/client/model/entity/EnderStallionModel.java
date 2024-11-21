package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.client.render.entity.EnderStallionRenderer;

public class EnderStallionModel extends SkeleponyModel<EnderStallionRenderer.State> {

    private final ModelPart leftHorn;
    private final ModelPart rightHorn;

    public EnderStallionModel(ModelPart tree) {
        super(tree);
        leftHorn = tree.getChild("left_horn");
        rightHorn = tree.getChild("right_horn");
    }

    @Override
    protected void setModelVisibilities(EnderStallionRenderer.State state) {
        super.setModelVisibilities(state);
        tail.setVisible(false, state);
        snout.setVisible(false, state);
        horn.setVisible(!state.isBoss, state);
        leftHorn.visible = rightHorn.visible = state.isBoss;
    }

    @Override
    public void setModelAngles(EnderStallionRenderer.State state) {
        super.setModelAngles(state);

        if (state.isAttacking) {
            head.pivotY -= 5;
        }
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        stack.push();
        stack.translate(0, -1.15F, 0);
        super.render(stack, vertices, overlay, light, color);
        stack.pop();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        leftSleeve.visible = false;
        rightSleeve.visible = false;

        leftPants.visible = false;
        rightPants.visible = false;
    }
}
