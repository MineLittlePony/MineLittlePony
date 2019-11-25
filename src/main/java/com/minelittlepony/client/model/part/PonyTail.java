package com.minelittlepony.client.model.part;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.model.IPart;

import java.util.UUID;

public class PonyTail extends ModelPart implements IPart {

    private final AbstractPonyModel<?> theModel;

    private int tailStop = 0;

    public PonyTail(AbstractPonyModel<?> model) {
        super(model);
        theModel = model;
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        roll = rainboom ? 0 : MathHelper.cos(move * 0.8F) * 0.2f * swing;
        yaw = bodySwing;

        if (theModel.getAttributes().isCrouching && !rainboom) {
            rotateSneak();
        } else if (theModel.isRiding()) {
            pivotZ = TAIL_RP_Z_RIDING;
            pivotY = TAIL_RP_Y_RIDING;
            pitch = PI / 5;
        } else {
            setPivot(TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_NOTSNEAK);
            if (rainboom) {
                pitch = ROTATE_90 + MathHelper.sin(move) / 10;
            } else {
                pitch = swing / 2;

                swingX(ticks);
            }
        }

        if (rainboom) {
            pivotY += 6;
            pivotZ++;
        }

        tailStop = theModel.getMetadata().getTail().ordinal();
    }

    private void swingX(float ticks) {
        float sinTickFactor = MathHelper.sin(ticks * 0.067f) * 0.05f;
        pitch += sinTickFactor;
        yaw += sinTickFactor;
    }

    private void rotateSneak() {
        setPivot(TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_SNEAK);
        pitch = -BODY_ROT_X_SNEAK + 0.1F;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
        render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    private static class Segment extends ModelPart {

        public PonyTail tail;

        int index;

        public Segment(Model model) {
            super(model);
        }

        public void setOwner(int index, PonyTail tail) {
            this.index = index;
            this.tail = tail;
        }

        @Override
        public void render(MatrixStack stack, VertexConsumer renderContext, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
            if (index < tail.tailStop) {
                super.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
            }
        }
    }
}
