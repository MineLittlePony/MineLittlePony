package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;
import com.minelittlepony.mson.api.MsonModel;
import com.minelittlepony.util.MathUtil;

public class PonyWings<S extends PonyRenderState> implements SubModel<S>, MsonModel {

    private WingedPonyModel<S> pegasus;

    protected Wing<S> leftWing;
    protected Wing<S> rightWing;

    protected Wing<S> legacyWing;

    private boolean visible;

    public PonyWings(ModelPart tree) {

    }

    @Override
    public void init(ModelView context) {
        pegasus = context.getModel();

        float wingScale = context.getLocalValue("wing_scale", 1); // pegasi 1 / bats 1.3F
        float walkingRotationSpeed = context.getLocalValue("walking_rotation_speed", 0.15F); // pegasi 0.15 / bats 0.05F

        leftWing = context.findByName("left_wing");
        rightWing = context.findByName("right_wing");
        legacyWing = context.findByName("legacy_right_wing");

        leftWing.wingScale = wingScale;
        leftWing.walkingRotationSpeed = walkingRotationSpeed;
        rightWing.wingScale = wingScale;
        rightWing.walkingRotationSpeed = walkingRotationSpeed;
        legacyWing.wingScale = wingScale;
        legacyWing.walkingRotationSpeed = walkingRotationSpeed;
    }

    public Wing<S> getLeft(S state) {
        return leftWing;
    }

    public Wing<S> getRight(S state) {
        return (
                state.getAttributes().isEmbedded(Wearable.SADDLE_BAGS_BOTH)
            || state.getAttributes().isEmbedded(Wearable.SADDLE_BAGS_LEFT)
            || state.getAttributes().isEmbedded(Wearable.SADDLE_BAGS_RIGHT)
        ) ? legacyWing : rightWing;
    }

    @Override
    public void setPartAngles(S state, float bodySwing) {
        float flap = 0;

        if (state.handSwingProgress > 0) {
            flap = MathHelper.sin(MathHelper.sqrt(state.handSwingProgress) * MathHelper.TAU);
        } else {
            float pi = MathHelper.PI * (float) Math.pow(state.limbAmplitudeMultiplier, 16);

            float mve = state.limbAmplitudeInverse * 0.6662f; // magic number ahoy (actually 2/3)
            float srt = state.limbAmplitudeMultiplier * 0.25F;

            flap = MathHelper.cos(mve + pi) * srt;
        }

        float flapAngle = MathUtil.Angles._270_DEG;

        if (pegasus.wingsAreOpen(state)) {
            flapAngle = pegasus.getWingRotationFactor(state);
            if (!state.attributes.isCrouching && isBurdened(state)) {
                flapAngle -= 1F;
            }
        } else {
            flapAngle = MathUtil.Angles._270_DEG - 0.9F + (float)Math.sin(state.age * 0.1F) / 15F;
        }

        if (!state.attributes.isFlying) {
            flapAngle = state.attributes.getMainInterpolator().interpolate("wingFlap", flapAngle, 10);
        }

        boolean extended = pegasus.wingsAreOpen(state);

        boolean bags = !extended && state.isWearing(Wearable.SADDLE_BAGS_BOTH);

        boolean useLegacyWing = (
                state.getAttributes().isEmbedded(Wearable.SADDLE_BAGS_BOTH)
            || state.getAttributes().isEmbedded(Wearable.SADDLE_BAGS_LEFT)
            || state.getAttributes().isEmbedded(Wearable.SADDLE_BAGS_RIGHT)
        );

        leftWing.open = extended;
        leftWing.bags = bags;
        leftWing.setAngles(state, flap, flapAngle);

        rightWing.open = extended;
        rightWing.bags = bags;
        rightWing.setAngles(state, -flap, -flapAngle);

        if (legacyWing != rightWing) {
            rightWing.root.hidden = useLegacyWing;
            legacyWing.root.hidden = !useLegacyWing;
            legacyWing.open = extended;
            legacyWing.bags = bags;
            legacyWing.setAngles(state, -flap, -flapAngle);
        }
    }

    @Override
    public void setVisible(boolean visible, S state) {
        visible = state.race.hasWings();
    }

    private boolean isBurdened(S state) {
        return state.getAttributes().isWearing(Wearable.SADDLE_BAGS_BOTH)
                || state.getAttributes().isWearing(Wearable.SADDLE_BAGS_LEFT)
                || state.getAttributes().isWearing(Wearable.SADDLE_BAGS_RIGHT);
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        if (visible) {
            leftWing.render(stack, vertices, overlay, light, color);
            rightWing.render(stack, vertices, overlay, light, color);
            legacyWing.render(stack, vertices, overlay, light, color);
        }
    }

    public static class Wing<S extends PonyRenderState> implements MsonModel {

        private final ModelPart root;

        protected final ModelPart extended;
        protected final ModelPart folded;

        private float wingScale = 1;
        private float walkingRotationSpeed = 0.15F;

        public boolean hidden;
        public boolean open;
        public boolean bags;

        public Wing(ModelPart tree) {
            root = tree;
            extended = tree.getChild("extended");
            folded = tree.getChild("folded");
        }

        public void setAngles(S state, float swing, float roll) {
            root.pivotY = root.getDefaultTransform().pivotY() + (bags ? 0.198F / wingScale : 0);
            root.xScale = wingScale;
            root.yScale = wingScale;
            root.zScale = wingScale;
            extended.visible = open;
            folded.visible = !open;
            folded.yaw = swing * walkingRotationSpeed;
            if (state.race.hasBugWings()) {
                extended.yaw = folded.yaw;
            }

            extended.roll = roll;
            if (state.race.hasBugWings()) {
                folded.roll = roll;
            }
        }

        public void render(MatrixStack matrices, VertexConsumer vertices, int overlay, int light, int color) {
            root.render(matrices, vertices, overlay, light, color);
        }
    }
}
