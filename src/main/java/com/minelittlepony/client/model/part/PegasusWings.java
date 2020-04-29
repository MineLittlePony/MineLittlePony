package com.minelittlepony.client.model.part;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.model.IPart;
import com.minelittlepony.model.IPegasus;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.MsonModel;

import java.util.UUID;

public class PegasusWings<T extends Model & IPegasus> implements IPart, MsonModel {

    protected T pegasus;

    protected Wing leftWing;
    protected Wing rightWing;

    protected Wing legacyWing;

    @Override
    public void init(ModelContext context) {
        pegasus = context.getModel();
        leftWing = context.findByName("left_wing");
        rightWing = context.findByName("right_wing");
        legacyWing = context.findByName("legacy_right_wing");
    }

    public Wing getLeft() {
        return leftWing;
    }

    public Wing getRight() {
        return pegasus.isWearing(Wearable.SADDLE_BAGS) ? legacyWing : rightWing;
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        float flap = 0;
        float progress = pegasus.getSwingAmount();

        if (progress > 0) {
            flap = MathHelper.sin(MathHelper.sqrt(progress) * PI * 2);
        } else {
            float pi = PI * (float) Math.pow(swing, 16);

            float mve = move * 0.6662f; // magic number ahoy (actually 2/3)
            float srt = swing / 4;

            flap = MathHelper.cos(mve + pi) * srt;
        }

        getLeft().rotateWalking(flap);
        getRight().rotateWalking(-flap);

        float flapAngle = ROTATE_270;

        if (pegasus.wingsAreOpen()) {
            flapAngle = pegasus.getWingRotationFactor(ticks);
            if (!pegasus.getAttributes().isCrouching && pegasus.isWearing(Wearable.SADDLE_BAGS)) {
                flapAngle -= 1F;
            }
        }

        if (!pegasus.isFlying()) {
            flapAngle = pegasus.getMetadata().getInterpolator(interpolatorId).interpolate("wingFlap", flapAngle, 10);
        }

        getLeft().rotateFlying(flapAngle);
        getRight().rotateFlying(-flapAngle);

    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
        getLeft().render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        getRight().render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    public static class Wing implements MsonModel {

        protected IPegasus pegasus;

        protected ModelPart extended;
        protected ModelPart folded;

        @Override
        public void init(ModelContext context) {
            pegasus = context.getModel();
            extended = context.findByName("extended");
            folded = context.findByName("folded");
        }

        public void rotateWalking(float swing) {
            folded.yaw = swing * 0.15F;
        }

        public void rotateFlying(float angle) {
            extended.roll = angle;
        }

        public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
            if (pegasus.wingsAreOpen()) {
                extended.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
            } else {
                boolean bags = pegasus.isWearing(Wearable.SADDLE_BAGS);
                if (bags) {
                    stack.push();
                    stack.translate(0, 0, 0.198F);
                }
                folded.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
                if (bags) {
                    stack.pop();
                }
            }
        }
    }
}
