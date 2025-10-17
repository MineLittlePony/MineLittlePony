package com.minelittlepony.client.transform;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.SizePreset;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PonyTransformation {
    NORMAL(SizePreset.NORMAL, 0, 3F, 0.75F) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, MatrixStack stack) {
            switch (part) {
                case NECK:
                    if (attributes.isCrouching) stack.translate(-0.03F, 0.03F, 0.13F);
                    break;
                case HEAD:
                    if (attributes.isLyingDown) stack.translate(0, -0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.1F, 0);
                    break;
                case BACK:
                    stack.translate(0, -0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, -0.08F, 0);
                    break;
                default:
            }
        }
    },
    STOCKY(SizePreset.STOCKY, 0, 3F, 0.75F) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, MatrixStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(-0.015F, 0.05F, -0.04F);
                    if (attributes.isSwimming) stack.translate(0, 0.1F, 0);
                    stack.scale(1.4F, 1, 1.2F);
                    if (attributes.isCrouching) stack.translate(-0.025F, 0.03F, 0.15F);
                    if (attributes.isSitting) stack.translate(0, 0, 0.03F);
                    break;
                case HEAD:
                    if (attributes.isLyingDown) stack.translate(0, 0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.1F, 0);
                    if (attributes.isSwimming) stack.translate(0, 0.2F, 0);
                    break;
                case BODY:
                    stack.translate(0, 0.35F, -0.05F);
                    if (attributes.isCrouching) stack.translate(0, -0.07F, 0.04F);
                    if (attributes.isLyingDown) stack.translate(0, -0.1F, 0);
                    stack.scale(1.4F, 1.3F, 1.1F);
                    break;
                case BACK:
                    stack.translate(0, -0.08F, 0);
                    if (attributes.isCrouching) stack.translate(0, -0.07F, 0.04F);
                    if (attributes.isLyingDown) stack.translate(0, -0.1F, 0);
                    if (attributes.isSleeping) stack.translate(0, 0.25F, 0);
                    break;
                case LEGS:
                    if (attributes.isCrouching) stack.translate(0, 0, 0.1F);
                    stack.scale(1.1F, 1, 1.1F);
                    break;
                default:
            }
        }
    },
    LANKY(SizePreset.LANKY, 0, 2.6F, 0.75F) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, MatrixStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(0, 0.27F, 0);
                    stack.scale(1, 1.3F, 1);
                    if (attributes.isCrouching) stack.translate(-0.03F, -0.01F, 0.15F);
                    if (attributes.isSleeping) stack.translate(0, -0.1F, 0);
                    break;
                case HEAD:
                    stack.translate(0, -0.14F, -0.04F);
                    if (attributes.isSleeping) stack.translate(0, 0.2F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.15F, 0);
                    if (attributes.isSwimming) stack.translate(0, 0.2F, 0);
                    break;
                case BACK:
                    if (attributes.isSleeping) stack.translate(0, 0.05F, 0);
                case BODY:
                    stack.translate(0, 0.2F, 0);
                    stack.scale(0.9F, 1.2F, 0.9F);
                    break;
                case TAIL:
                    stack.translate(0, -0.2F, 0.08F);
                    break;
                case LEGS:
                    if (attributes.isLyingDown || attributes.isGoingFast) {
                        stack.scale(0.9F, 0.9F, 1.12F);
                    } else {
                        stack.scale(0.9F, 1.12F, 0.9F);
                    }
                    break;
            }
        }
    },
    BULKY(SizePreset.BULKY, 0, 2.3F, 0.75F) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, MatrixStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(0, 0.25F, -0.07F);
                    stack.scale(1, 1.3F, 1);
                    if (attributes.isCrouching) stack.translate(-0.03F, -0.07F, 0.09F);
                    break;
                case HEAD:
                    stack.translate(0, -0.14F, -0.06F);
                    if (attributes.isSleeping) stack.translate(0, 0.2F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.15F, 0);
                    if (attributes.isSwimming) stack.translate(0, 0.2F, 0);
                    break;
                case BODY:
                    stack.translate(0, 0.2F, -0.04F);
                    stack.scale(1.15F, 1.2F, 1.2F);
                    break;
                case TAIL:
                    stack.translate(0, -0.2F, 0.08F);
                    break;
                case LEGS:
                    if (attributes.isLyingDown || attributes.isGoingFast) {
                        stack.scale(1.15F, 1.15F, 1.12F);
                    } else {
                        stack.scale(1.15F, 1.12F, 1.15F);
                    }
                    break;
                case BACK:
                    stack.translate(0, -0.15F, -0.04F);
                    break;
            }
        }
    },
    FOAL(SizePreset.FOAL, 0, 3.8F, 0.75F) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, MatrixStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(0, -0.1F, 0.01F);
                    if (attributes.isCrouching) stack.translate(-0.03F, -0.19F, 0.18F);
                    if (attributes.isLyingDown) stack.translate(0, 0.1F, 0);
                case HEAD:
                    if (attributes.isLyingDown) stack.translate(0, attributes.isSleeping ? -0.5F : -0.35F, 0.1F);
                    if (attributes.isSwimming) stack.translate(0, -0.2F, 0);
                    stack.translate(0, 0.65F, 0);
                    stack.scale(1.3F, 1.3F, 1.3F);
                    break;
                case LEGS:
                    if (attributes.isLyingDown || attributes.isGoingFast) {
                        stack.scale(1, 1, 0.81F);
                    } else {
                        stack.scale(1, 0.81F, 1);
                    }

                    break;
                case BACK:
                    if (attributes.isCrouching) stack.translate(0, -0.1F, 0);
                case BODY:
                    if (attributes.isLyingDown) stack.translate(0, -0.3F, 0);
                    stack.translate(0, 0.25F, 0);
                    break;
                default:
            }
        }
    },
    TALL(SizePreset.TALL, 0, 2.2F, 0.75F) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, MatrixStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(0, 0.21F, -0.01F);
                    stack.scale(1, 1.28F, 1);
                    if (attributes.isCrouching) stack.translate(-0.04F, -0.1F, 0.15F);
                    if (attributes.isSwimming) stack.translate(0, -0.1F, 0);
                    break;
                case HEAD:
                    stack.translate(0, -0.11F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.04F, 0);
                    if (attributes.isSwimming) stack.translate(0, 0.05F, 0);
                    break;
                case BACK:
                    stack.translate(0, -0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, -0.1F, 0);
                case BODY:
                case TAIL:
                    stack.translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    if (attributes.isLyingDown || attributes.isGoingFast) {
                        stack.scale(1, 1, 1.18F);
                    } else {
                        stack.scale(1, 1.18F, 1);
                    }
                    if (attributes.isGoingFast) stack.translate(0, 0.05F, 0);
                    break;
            }
        }
    },
    YEARLING(SizePreset.YEARLING, 0, 3.8F, 0.75F) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, MatrixStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(0, 0.2F, 0);
                    stack.scale(1, 1.3F, 1);
                    if (attributes.isCrouching) stack.translate(-0.04F, -0.05F, 0.15F);
                    if (attributes.isSwimming) stack.translate(0, -0.1F, 0);
                    break;
                case HEAD:
                    stack.translate(0, 0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.04F, 0);
                    if (attributes.isSwimming) stack.translate(0, -0.05F, 0);
                    stack.scale(1.15F, 1.15F, 1.15F);
                    break;
                case BACK:
                    stack.translate(0, -0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, -0.05F, 0);
                case BODY:
                case TAIL:
                    stack.translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    if (attributes.isLyingDown || attributes.isGoingFast) {
                        stack.scale(1, 1, 1.18F);
                    } else {
                        stack.scale(1, 1.18F, 1);
                    }
                    if (attributes.isGoingFast) {
                        stack.translate(0, -0.1F, 0);
                    }
                    break;
            }
        }
    },
    SQUAT(SizePreset.SQUAT, 0, 3.4F, 0.75F) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, MatrixStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(-0.01F, 0.1F, 0.03F);
                    stack.scale(1.4F, 1, 1.1F);
                    if (attributes.isCrouching) stack.translate(-0.03F, 0.04F, 0.1F);
                    if (attributes.isSwimming) stack.translate(0, 0.2F, 0);
                    break;
                case HEAD:
                    stack.translate(0, 0.3F, 0);
                    stack.scale(1.15F, 1.15F, 1.15F);
                    if (attributes.isSwimming) stack.translate(0, 0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.07F, 0);
                    break;
                case BACK:
                    if (attributes.isLyingDown) stack.translate(0, 0.1F, 0);
                case BODY:
                    if (!attributes.isLyingDown) stack.translate(0, 0.5F, 0);
                    stack.scale(1.4F, 1.3F, 1);
                    if (attributes.isCrouching) stack.translate(0, -0.05F, 0);
                    break;
                case TAIL:
                    stack.translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    stack.translate(0, 0, -0.1F);
                    if (attributes.isGoingFast) stack.translate(0, 0.3F, 0);
                    if (attributes.isLyingDown || attributes.isGoingFast) {
                        stack.scale(1.1F, 1.1F, 0.8F);
                    } else {
                        stack.scale(1.1F, 0.8F, 1.1F);
                    }
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

    @Deprecated
    public Vec3d getRiderOffset() {
        return riderOffset;
    }

    public void translateForRider(MatrixStack stack) {
        stack.translate(riderOffset.x, riderOffset.y / 16F, riderOffset.z);
    }

    public abstract void transform(ModelAttributes attributes, BodyPart part, MatrixStack stack);

    public static PonyTransformation forSize(Size size) {
        return REGISTRY.getOrDefault(size, NORMAL);
    }
}
