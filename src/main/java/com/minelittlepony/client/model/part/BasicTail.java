package com.minelittlepony.client.model.part;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class BasicTail implements SubModel<PonyRenderState> {
    private ModelPart tail;

    public BasicTail(ModelPart tree) {
        tail = tree.getChild("tail");
    }

    @Override
    public void setAngles(PonyModel<PonyRenderState> model, PonyRenderState state) {
        tail.resetPose();
        tail.visible = !state.isSpectator;

        tail.yRot = state.wobbleAmount * 5;

        tail.zRot = Mth.cos(state.walkAnimationPos * 0.8F) * 0.2f * state.walkAnimationSpeed;

        if (state.attributes.isCrouching) {
            tail.xRot = model.getBodyPart(BodyPart.BODY).xRot - 0.1F;
            tail.y += 0.4F;
            tail.z -= 4.8F;
        } else if (state.attributes.isSitting) {
            tail.xRot = Mth.PI / 5;
            tail.y += 10.7F;
            tail.z -= 0.4F;
        } else {
            tail.xRot = state.walkAnimationSpeed / 2;
            swingX(state.ageInTicks);
        }
    }

    private void swingX(float ticks) {
        float sinTickFactor = Mth.sin(ticks * 0.067f) * 0.05f;
        tail.xRot += sinTickFactor;
        tail.yRot += sinTickFactor;
    }

    public void setHidden() {
        tail.visible = false;
    }

    @Override
    public void accept(PoseStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        tail.render(stack, vertices, overlay, light, color);
    }
}
