package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.model.IPart;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.MsonModel;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PonyTail implements IPart, MsonModel {

    private ModelPart tail;
    private AbstractPonyModel<?> theModel;

    private int tailStop = 0;

    @Override
    public void init(ModelContext context) {
        theModel = (AbstractPonyModel<?>)context.getModel();

        tail = new ModelPart(theModel);

        try {
            int segments = context.getLocals().getValue("segments").get().intValue();

            for (int i = 0; i < segments; i++) {
                Segment segment = context.findByName("segment_" + i);
                segment.index = i;
                tail.addChild(segment);
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        tail.roll = rainboom ? 0 : MathHelper.cos(move * 0.8F) * 0.2f * swing;
        tail.yaw = bodySwing;

        if (theModel.getAttributes().isCrouching && !rainboom) {
            rotateSneak();
        } else if (theModel.isRiding()) {
            tail.pivotZ = TAIL_RP_Z_RIDING;
            tail.pivotY = TAIL_RP_Y_RIDING;
            tail.pitch = PI / 5;
        } else {
            tail.setPivot(TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_NOTSNEAK);
            if (rainboom) {
                tail.pitch = ROTATE_90 + MathHelper.sin(move) / 10;
            } else {
                tail.pitch = swing / 2;

                swingX(ticks);
            }
        }

        if (rainboom) {
            tail.pivotY += 6;
            tail.pivotZ++;
        }

        tailStop = theModel.getMetadata().getTail().ordinal();
    }

    private void swingX(float ticks) {
        float sinTickFactor = MathHelper.sin(ticks * 0.067f) * 0.05f;
        tail.pitch += sinTickFactor;
        tail.yaw += sinTickFactor;
    }

    private void rotateSneak() {
        tail.setPivot(TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_SNEAK);
        tail.pitch = -BODY_ROT_X_SNEAK + 0.1F;
    }

    @Override
    public void setVisible(boolean visible) {
        tail.visible = visible;
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
        tail.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    public static class Segment extends ModelPart implements MsonModel {

        public PonyTail tail;

        public int index;

        public Segment(ModelContext context) {
            super(context.getModel());
        }

        @Override
        public void init(ModelContext context) {
            tail = context.getContext();
            context.findByName("segment", this);
        }

        @Override
        public void render(MatrixStack stack, VertexConsumer renderContext, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
            if (index < tail.tailStop) {
                super.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
            }
        }
    }
}
