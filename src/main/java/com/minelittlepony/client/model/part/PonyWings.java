package com.minelittlepony.client.model.part;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.mson.api.ModelView;
import com.minelittlepony.mson.api.MsonModel;

public class PonyWings<T extends Model & IPegasus> implements IPart, MsonModel {

    protected T pegasus;

    protected Wing leftWing;
    protected Wing rightWing;

    protected Wing legacyWing;

    private float wingScale;
    private float walkingRotationSpeed;

    public PonyWings(ModelPart tree) {

    }

    @Override
    public void init(ModelView context) {
        pegasus = context.getModel();
        leftWing = context.findByName("left_wing", Wing::new);
        rightWing = context.findByName("right_wing", Wing::new);
        legacyWing = context.findByName("legacy_right_wing", Wing::new);
        wingScale = context.getLocalValue("wing_scale", 1); // pegasi 1 / bats 1.3F
        walkingRotationSpeed = context.getLocalValue("walking_rotation_speed", 0.15F); // pegasi 0.15 / bats 0.05F
    }

    public Wing getLeft() {
        return leftWing;
    }

    public Wing getRight() {
        return (pegasus.isEmbedded(Wearable.SADDLE_BAGS_BOTH) || pegasus.isEmbedded(Wearable.SADDLE_BAGS_LEFT) || pegasus.isEmbedded(Wearable.SADDLE_BAGS_RIGHT)) ? legacyWing : rightWing;
    }

    @Override
    public void setRotationAndAngles(ModelAttributes attributes, float move, float swing, float bodySwing, float ticks) {
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
            if (!attributes.isCrouching && pegasus.isBurdened()) {
                flapAngle -= 1F;
            }
        } else {
            flapAngle = ROTATE_270 - 0.9F + (float)Math.sin(ticks / 10) / 15F;
        }

        if (!pegasus.isFlying()) {
            flapAngle = pegasus.getMetadata().getInterpolator(attributes.interpolatorId).interpolate("wingFlap", flapAngle, 10);
        }

        getLeft().rotateFlying(flapAngle);
        getRight().rotateFlying(-flapAngle);

    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
        getLeft().render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        getRight().render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    public class Wing implements MsonModel {

        protected IPegasus pegasus;

        protected final ModelPart extended;
        protected final ModelPart folded;

        public Wing(ModelPart tree) {
            extended = tree.getChild("extended");
            folded = tree.getChild("folded");
        }

        @Override
        public void init(ModelView context) {
            pegasus = context.getModel();
        }

        public void rotateWalking(float swing) {
            folded.yaw = swing * walkingRotationSpeed;
        }

        public void rotateFlying(float angle) {
            extended.roll = angle;
        }

        public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
            stack.push();
            stack.scale(wingScale, wingScale, wingScale);

            if (pegasus.wingsAreOpen()) {
                extended.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
            } else {
                boolean bags = pegasus.isWearing(Wearable.SADDLE_BAGS_BOTH);
                if (bags) {
                    stack.push();
                    stack.translate(0, 0, 0.198F);
                }
                folded.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
                if (bags) {
                    stack.pop();
                }
            }

            stack.pop();
        }
    }
}
