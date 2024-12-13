package com.minelittlepony.client.transform;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.SizePreset;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PonyTransformation {

    NORMAL(SizePreset.NORMAL, 0, 3F, 0.75F) {
        @Override
        public void transform(PonyModel<?> model, BodyPart part, MatrixStack stack) {
            if (model.getAttributes().isSwimming) stack.translate(0, -0.3F, 0);
            if (model.getAttributes().isCrouching) stack.translate(0, -0.2F, 0);
            if (model.getAttributes().isLyingDown) stack.translate(0, -0.77F, 0.1F);
            if (model.getAttributes().isSleeping) stack.translate(0, 0.16F, 0);
            if (model.getAttributes().isSitting) stack.translate(0, -0.2F, -0.2F);

            switch (part) {
                case NECK:
                    if (model.getAttributes().isCrouching) stack.translate(-0.03F, 0.03F, 0.13F);
                    break;
                case HEAD:
                    if (model.getAttributes().isLyingDown) stack.translate(0, -0.05F, 0);
                    if (model.getAttributes().isCrouching) stack.translate(0, 0.1F, 0);
                    break;
                case BACK:
                    translateForRider(stack);
                    break;
                default:
            }
        }
    },
    STOCKY(SizePreset.STOCKY, 0, 3F, 0.75F) {
        @Override
        public void transform(PonyModel<?> model, BodyPart part, MatrixStack stack) {
            if (model.getAttributes().isSwimming) stack.translate(0, -0.3F, 0);
            if (model.getAttributes().isCrouching) stack.translate(0, -0.2F, 0);
            if (model.getAttributes().isLyingDown) stack.translate(0, -0.77F, 0.1F);
            if (model.getAttributes().isSleeping) stack.translate(0, 0.16F, 0);
            if (model.getAttributes().isSitting) stack.translate(0, -0.25F, -0.2F);

            switch (part) {
                case NECK:
                    stack.translate(-0.015F, -0.05F, -0.04F);
                    stack.scale(1.4F, 1, 1.2F);
                    if (model.getAttributes().isCrouching) stack.translate(-0.025F, 0.03F, 0.15F);
                    if (model.getAttributes().isSitting) stack.translate(0, 0, 0.03F);
                    break;
                case HEAD:
                    if (model.getAttributes().isLyingDown) stack.translate(0, -0.05F, 0);
                    if (model.getAttributes().isCrouching) stack.translate(0, 0.1F, 0);
                    break;
                case BODY:
                    stack.translate(0, -0.15F, -0.05F);
                    if (model.getAttributes().isCrouching) stack.translate(0, -0.07F, 0.04F);
                    if (model.getAttributes().isLyingDown) stack.translate(0, -0.1F, 0);
                    stack.scale(1.4F, 1.3F, 1.1F);
                    break;
                case LEGS:
                    stack.translate(0, 0, -0.1F);
                    if (model.getAttributes().isCrouching) stack.translate(0, 0, 0.1F);
                    stack.scale(1.1F, 1, 1.1F);
                    break;
                case BACK:
                    translateForRider(stack);
                    break;
            }
        }
    },
    LANKY(SizePreset.LANKY, 0, 2.6F, 0.75F) {
        @Override
        public void transform(PonyModel<?> model, BodyPart part, MatrixStack stack) {
            if (model.getAttributes().isSwimming) stack.translate(0, -0.2F, 0);
            if (model.getAttributes().isCrouching) stack.translate(0, -0.15F, 0);
            if (model.getAttributes().isLyingDown) stack.translate(0, -0.68F, 0.15F);
            if (model.getAttributes().isSleeping) stack.translate(0, 0.08F, 0);
            if (model.getAttributes().isSitting) stack.translate(0, 0, -0.2F);

            switch (part) {
                case NECK:
                    stack.translate(0, -0.2F, -0.05F);
                    stack.scale(1, 1.3F, 1);
                    if (model.getAttributes().isCrouching) stack.translate(-0.03F, 0.01F, 0.2F);
                    break;
                case HEAD:
                    stack.translate(0, -0.14F, -0.04F);
                    if (model.getAttributes().isLyingDown) stack.translate(0, 0, -0.1F);
                    if (model.getAttributes().isCrouching) stack.translate(0, 0.15F, 0);
                    break;
                case BODY:
                    stack.translate(0, -0.2F, -0.04F);
                    stack.scale(0.9F, 1.2F, 0.9F);
                    break;
                case TAIL:
                    stack.translate(0, -0.2F, 0.08F);
                    break;
                case LEGS:
                    stack.translate(0, -0.18F, 0);
                    stack.scale(0.9F, 1.12F, 0.9F);
                    break;
                case BACK:
                    translateForRider(stack);
                    break;
            }
        }
    },
    BULKY(SizePreset.BULKY, 0, 2.3F, 0.75F) {
        @Override
        public void transform(PonyModel<?> model, BodyPart part, MatrixStack stack) {
            if (model.getAttributes().isCrouching) stack.translate(0, -0.15F, 0);
            if (model.getAttributes().isLyingDown) stack.translate(0, -0.66F, 0.25F);
            if (model.getAttributes().isSleeping) stack.translate(0, 0.06F, 0);
            if (model.getAttributes().isSitting) stack.translate(0, 0, -0.2F);

            switch (part) {
                case NECK:
                    stack.translate(0, -0.2F, -0.07F);
                    stack.scale(1, 1.3F, 1);
                    if (model.getAttributes().isCrouching) stack.translate(-0.03F, -0.07F, 0.09F);
                    break;
                case HEAD:
                    stack.translate(0, -0.14F, -0.06F);
                    if (model.getAttributes().isLyingDown) stack.translate(-0.05F, 0, -0.1F);
                    if (model.getAttributes().isCrouching) stack.translate(0, 0.15F, 0);
                    break;
                case BODY:
                    stack.translate(0, -0.2F, -0.04F);
                    stack.scale(1.15F, 1.2F, 1.2F);
                    break;
                case TAIL:
                    stack.translate(0, -0.2F, 0.08F);
                    break;
                case LEGS:
                    stack.translate(0, -0.18F, 0);
                    stack.scale(1.15F, 1.12F, 1.15F);
                    break;
                case BACK:
                    translateForRider(stack);
                    break;
            }
        }
    },
    FOAL(SizePreset.FOAL, 0, 3.8F, 0.75F) {
        @Override
        public void transform(PonyModel<?> model, BodyPart part, MatrixStack stack) {
            if (model.getAttributes().isSwimming) stack.translate(0, -0.9F, 0);
            if (model.getAttributes().isCrouching) stack.translate(0, -0.2F, 0);
            if (model.getAttributes().isLyingDown) stack.translate(0, -0.98F, -0.3F);
            if (model.getAttributes().isSleeping) stack.translate(0, 0.18F, 0);
            if (model.getAttributes().isSitting) stack.translate(0, -0.6F, -0.2F);

            stack.translate(0, 0.2F, 0);

            switch (part) {
                case NECK:
                    stack.translate(0, 0, 0.04F);
                    stack.scale(1.3F, 1.3F, 1.3F);
                    if (model.getAttributes().isCrouching) stack.translate(-0.03F, -0.16F, 0.15F);
                    break;
                case HEAD:
                    stack.scale(1.3F, 1.3F, 1.3F);
                    break;
                case LEGS:
                    stack.translate(0, 0.09F, 0);
                    stack.scale(1, 0.81F, 1);
                    break;
                case BACK:
                    translateForRider(stack);
                    break;
                default:
            }
        }
    },
    TALL(SizePreset.TALL, 0, 2.2F, 0.75F) {
        @Override
        public void transform(PonyModel<?> model, BodyPart part, MatrixStack stack) {
            if (model.getAttributes().isCrouching) stack.translate(0, -0.15F, 0);
            if (model.getAttributes().isLyingDown) stack.translate(0, -0.6F, 0.35F);
            if (model.getAttributes().isSleeping) stack.translate(0, 0.1F, 0);
            if (model.getAttributes().isSitting) stack.translate(0, 0.1F, -0.2F);

            switch (part) {
                case NECK:
                    stack.translate(0, -0.21F, -0.01F);
                    stack.scale(1, 1.28F, 1);
                    if (model.getAttributes().isCrouching) stack.translate(-0.04F, -0.1F, 0.15F);
                    break;
                case HEAD:
                    stack.translate(0, -0.11F, 0);
                    if (model.getAttributes().isCrouching) stack.translate(0, 0.04F, 0);
                    break;
                case BODY:
                case TAIL:
                    stack.translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    stack.translate(0, -0.27F, 0.03F);
                    stack.scale(1, 1.18F, 1);
                    if (model.getAttributes().isGoingFast) stack.translate(0, 0.05F, 0);
                    break;
                case BACK:
                    translateForRider(stack);
                    break;
            }
        }
    },
    YEARLING(SizePreset.YEARLING, 0, 3.8F, 0.75F) {
        @Override
        public void transform(PonyModel<?> model, BodyPart part, MatrixStack stack) {
            if (model.getAttributes().isSwimming) stack.translate(0, -0.6F, 0);
            if (model.getAttributes().isCrouching) stack.translate(0, -0.15F, 0);
            if (model.getAttributes().isLyingDown) stack.translate(0, -0.71F, -0.3F);
            if (model.getAttributes().isSleeping) stack.translate(0, 0.26F, 0);
            if (model.getAttributes().isSitting) stack.translate(0, -0.4F, -0.2F);

            switch (part) {
                case NECK:
                    stack.translate(0, -0.2F, 0);
                    stack.scale(1, 1.3F, 1);
                    if (model.getAttributes().isCrouching) stack.translate(-0.04F, -0.05F, 0.15F);
                    break;
                case HEAD:
                    stack.translate(0, -0.15F, 0);
                    if (model.getAttributes().isCrouching) stack.translate(0, 0.04F, 0);
                    stack.scale(1.15F, 1.15F, 1.15F);
                    break;
                case BODY:
                case TAIL:
                    stack.translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    stack.translate(0, -0.265F, 0.03F);
                    stack.scale(1, 1.18F, 1);
                    if (model.getAttributes().isGoingFast) stack.translate(0, 0.05F, 0);
                    break;
                case BACK:
                    translateForRider(stack);
                    break;
            }
        }
    },
    SQUAT(SizePreset.SQUAT, 0, 3.4F, 0.75F) {
        @Override
        public void transform(PonyModel<?> model, BodyPart part, MatrixStack stack) {
            if (model.getAttributes().isSwimming) stack.translate(0, -0.6F, 0);
            if (model.getAttributes().isCrouching) stack.translate(0, -0.2F, 0);
            if (model.getAttributes().isLyingDown) stack.translate(0, -1, 0);
            if (model.getAttributes().isSleeping) stack.translate(0, 0.2F, 0);
            if (model.getAttributes().isSitting) stack.translate(0, -0.5F, -0.2F);

            stack.translate(0, -0.05F, 0);

            switch (part) {
                case NECK:
                    stack.translate(-0.01F, 0.1F, 0.03F);
                    stack.scale(1.4F, 1, 1.1F);
                    if (model.getAttributes().isCrouching) stack.translate(-0.03F, 0.04F, 0.1F);
                    break;
                case HEAD:
                    stack.translate(0, 0.07F, 0);
                    stack.scale(1.15F, 1.15F, 1.15F);
                    if (model.getAttributes().isCrouching) stack.translate(0, 0.07F, 0);
                    break;
                case BODY:
                    stack.scale(1.4F, 1.3F, 1);
                    if (model.getAttributes().isCrouching) stack.translate(0, -0.05F, 0);
                    break;
                case TAIL:
                    stack.translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    stack.translate(0, 0.35F, 0.02F);
                    stack.scale(1.1F, 0.8F, 1.1F);
                    if (model.getAttributes().isLyingDown) stack.translate(0, -0.1F, 0);
                    break;
                case BACK:
                    translateForRider(stack);
                    break;
            }
        }
    };

    private static final Map<Size, PonyTransformation> REGISTRY = Arrays.stream(values()).collect(Collectors.toMap(i -> i.size, Function.identity()));

    private final Size size;
    private final Vec3d riderOffset;

    PonyTransformation(Size size, float rX, float rY, float rZ) {
        this.size = size;
        riderOffset = new Vec3d(rX, rY, rZ);
    }

    public Vec3d getRiderOffset() {
        return riderOffset;
    }

    public void translateForRider(MatrixStack stack) {
        stack.translate(riderOffset.x, riderOffset.y, riderOffset.z);
    }

    public abstract void transform(PonyModel<?> model, BodyPart part, MatrixStack stack);

    public static PonyTransformation forSize(Size size) {
        return REGISTRY.getOrDefault(size, NORMAL);
    }
}
