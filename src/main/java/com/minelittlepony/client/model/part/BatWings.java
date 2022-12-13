package com.minelittlepony.client.model.part;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.model.IPegasus;
import com.minelittlepony.api.model.ModelAttributes;

public class BatWings<T extends Model & IPegasus> extends PegasusWings<T> {

    public BatWings(ModelPart tree) {
        super(tree);
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
        stack.push();
        stack.scale(1.3F, 1.3F, 1.3F);

        super.renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, attributes);

        stack.pop();
    }

    public static class Wing extends PegasusWings.Wing {

        public Wing(ModelPart tree) {
            super(tree);
        }

        @Override
        public void rotateWalking(float swing) {
            folded.yaw = swing * 0.05F;
        }
    }
}
