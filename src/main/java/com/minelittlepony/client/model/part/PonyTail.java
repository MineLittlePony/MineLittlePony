package com.minelittlepony.client.model.part;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.util.render.Part;
import com.minelittlepony.model.IPart;

import java.util.UUID;

public class PonyTail extends Part implements IPart {

    private static final int SEGMENTS = 4;

    private final AbstractPonyModel<?> theModel;

    private int tailStop = 0;

    public PonyTail(AbstractPonyModel<?> model) {
        super(model);
        theModel = model;

        for (int i = 0; i < SEGMENTS; i++) {
            addChild(new TailSegment(theModel, i));
        }
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

    private class TailSegment extends ModelPart {

        private final int index;

        public TailSegment(Model model, int index) {
            super(model);
            this.index = index;

            pivotY = ((float)index)/4 + 0.063f;

            int texX = (index % 2) * 4;

            around(TAIL_RP_X, TAIL_RP_Y, 0);

            if (index == 0) {
                tex(32, 0).top(-2, 0, 2, 4, 4, 0);
            }

            tex(36, texX) .east( 2, 0, 2, 4, 4, 0)
                          .west(-2, 0, 2, 4, 4, 0);
            tex(32, texX).south(-2, 0, 2, 4, 4, 0)
                         .north(-2, 0, 6, 4, 4, 0);
            tex(32, 0)  .bottom(-2, 4, 2, 4, 4, 0);
        }

        @Override
        public void render(MatrixStack stack, VertexConsumer renderContext, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
            if (index < tailStop) {
                super.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
            }
        }
    }
}
