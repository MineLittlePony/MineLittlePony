package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.model.IPart;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.MsonModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PonyTail implements IPart, MsonModel {

    private ModelPart tail;
    private AbstractPonyModel<?> theModel;

    private int tailStop = 0;

    private final List<Segment> segments = new ArrayList<>();

    public PonyTail(ModelPart tree) {

    }

    @Override
    public void init(ModelContext context) {
        theModel = context.getModel();

        try {
            int segments = context.getLocals().getValue("segments").get().intValue();

            ModelContext subContext = context.resolve(this);

            for (int i = 0; i < segments; i++) {
                Segment segment = subContext.findByName("segment_" + i);
                segment.tail = this;
                segment.index = i;
                this.segments.add(segment);
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        tail = new ModelPart(new ArrayList<>(), new HashMap<>());
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
        stack.push();
        tail.rotate(stack);

        segments.forEach(segment -> segment.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha));

        stack.pop();
    }

    public static class Segment implements MsonModel {
        public PonyTail tail;

        public int index;

        private final ModelPart tree;

        public Segment(ModelPart tree) {
            this.tree = tree;
        }

        public void render(MatrixStack stack, VertexConsumer renderContext, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
            if (index < tail.tailStop) {
                tree.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
            }
        }
    }
}
