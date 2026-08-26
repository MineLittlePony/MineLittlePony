package com.minelittlepony.client.transform;

import net.minecraft.client.model.geom.ModelPart;

import org.joml.Vector3f;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PonyTransformation implements TransformedModel.BodyType {
    NORMAL(SizePreset.NORMAL) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, PoseStack stack) {
            switch (part) {
                case NECK:
                    if (attributes.isCrouching) stack.translate(0, 0.03F, 0.13F);
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
    STOCKY(SizePreset.STOCKY) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, PoseStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(0, -0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, -0.095F, 0.13F);
                case HEAD:
                    if (attributes.isLyingDown) stack.translate(0, -0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.1F, 0);
                    break;
                case BODY:
                    stack.translate(0, -0.1F, -0.05F);
                    if (attributes.isCrouching) stack.translate(0, -0.05F, 0);
                    if (attributes.isLyingDown) stack.translate(0, -0.03F, 0);
                    break;
                case TAIL:
                    stack.translate(0, -0.1F, 0);
                    if (attributes.isCrouching) stack.translate(0, -0.05F, 0);
                    if (attributes.isLyingDown) stack.translate(0, -0.15F, 0);
                    break;
                case BACK:
                    if (attributes.isCrouching) stack.translate(0, -0.16F, 0.04F);
                    if (attributes.isSitting) stack.translate(0, -0.15F, -0.1F);
                    break;
                case LEGS:
                    if (!attributes.isLyingDown) stack.translate(0, 0.01F, 0);
                    break;
                default:
            }
        }

        @Override
        public void transform(ModelAttributes attributes, BodyPart bodyPart, ModelPart part) {
            part.offsetScale(switch (bodyPart) {
                case NECK -> new Vector3f(0.3F, 0, 0.15F);
                case BODY -> new Vector3f(0.4F, 0.3F, 0.1F);
                case LEGS -> new Vector3f(0.1F, 0, 0.1F);
                default -> ZERO;
            });
        }
    },
    LANKY(SizePreset.LANKY) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, PoseStack stack) {
            switch (part) {
                case NECK:
                    if (attributes.isCrouching) stack.translate(0, -0.1F, 0.15F);
                case HEAD:
                    stack.translate(0, -0.14F, 0);
                    if (attributes.isSleeping) stack.translate(0, 0.2F, 0);
                    break;
                case BACK:
                    if (attributes.isSleeping) stack.translate(0, 0.05F, 0);
                case TAIL:
                case BODY:
                    stack.translate(0, -0.15F, 0);
                    break;
                case LEGS:
                    if (!attributes.isLyingDown) stack.translate(0, -0.12F, -0.05F);
                    break;
                default:
            }
        }

        @Override
        public void transform(ModelAttributes attributes, BodyPart bodyPart, ModelPart part) {
            part.offsetScale(switch (bodyPart) {
                case HEAD -> new Vector3f(0.05F);
                case NECK -> new Vector3f(0, 0.3F, 0);
                case BODY -> new Vector3f(-0.1F, 0.2F, -0.1F);
                case LEGS -> new Vector3f(-0.1F, 0.12F, -0.1F);
                default -> ZERO;
            });
        }
    },
    BULKY(SizePreset.BULKY) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, PoseStack stack) {
            switch (part) {
                case NECK:
                    if (attributes.isCrouching) stack.translate(0, -0.091F, 0.09F);
                case HEAD:
                    stack.translate(0, 0, -0.06F);
                    if (attributes.isSleeping) stack.translate(0, 0.2F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.05F, 0);
                    break;
                case BACK:
                    stack.translate(0, -0.05F, 0);
                case BODY:
                case TAIL:
                    stack.translate(0, -0.1F, -0.04F);
                    break;
                case LEGS:
                    if (!attributes.isLyingDown) stack.translate(0, -0.12F, 0);
                    if (attributes.isGliding) stack.translate(0, 0.15F, 0);
                    break;
                default:
            }
        }

        @Override
        public void transform(ModelAttributes attributes, BodyPart bodyPart, ModelPart part) {
            part.offsetScale(switch (bodyPart) {
                case NECK -> new Vector3f(0, 0.3F, 0);
                case BODY -> new Vector3f(0.15F, 0.2F, 0.2F);
                case LEGS -> new Vector3f(0.15F, 0.12F, 0.15F);
                default -> ZERO;
            });
        }
    },
    FOAL(SizePreset.FOAL) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, PoseStack stack) {
            switch (part) {
                case NECK:
                    if (attributes.isCrouching) stack.translate(0F, 0, 0.1F);
                case HEAD:
                    if (attributes.isLyingDown) stack.translate(0, -0.25F, 0);
                    stack.translate(0, 0.2F, 0);
                    break;
                case LEGS:
                    if (!attributes.isLyingDown) stack.translate(0, 0.19F, 0);
                    if (attributes.isGliding) stack.translate(0, 0.1F, 0);
                    if (attributes.isGoingFast) stack.translate(0, 0.05F, 0);
                    break;
                case BACK:
                    if (attributes.isCrouching) stack.translate(0, -0.1F, 0);
                case BODY:
                case TAIL:
                    if (attributes.isLyingDown) stack.translate(0, -0.3F, 0);
                    stack.translate(0, 0.25F, 0);
                    break;
                case WINGS:
                    stack.translate(0.02F, 0.1F, 0);
                    break;
                default:
            }
        }

        @Override
        public void transform(ModelAttributes attributes, BodyPart bodyPart, ModelPart part) {
            part.offsetScale(switch (bodyPart) {
                case HEAD -> new Vector3f(0.3F);
                case HORN -> new Vector3f(-0.1F, -0.1F, -0.1F);
                case LEGS -> new Vector3f(0, -0.19F, 0);
                case WINGS -> new Vector3f(-0.2F);
                default -> ZERO;
            });
        }
    },
    TALL(SizePreset.TALL) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, PoseStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(0, -0.21F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0, 0.1F);
                    if (attributes.isCrouching || attributes.isSwimming) stack.translate(0, -0.028F, 0);
                    break;
                case HEAD:
                    stack.translate(0, -0.12F, 0);
                    if (attributes.isCrouching || attributes.isSwimming) stack.translate(0, 0.04F, 0);
                    break;
                case HORN:
                    stack.translate(0, 0.05F, 0);
                    break;
                case BACK:
                    stack.translate(0, -0.05F, 0);
                case BODY:
                case TAIL:
                    stack.translate(0, -0.1F, 0);
                    break;
                case WINGS:
                    stack.translate(-0.05F, 0, 0);
                    break;
                case LEGS:
                    if (!attributes.isLyingDown) stack.translate(0, -0.2F, 0);
                    if (attributes.isGoingFast) stack.translate(0, 0.049F, 0);
                    break;
                default:
            }
        }

        @Override
        public void transform(ModelAttributes attributes, BodyPart bodyPart, ModelPart part) {
            if (bodyPart == BodyPart.HORN) {
                part.y += 4;
                part.z += 3;
            }
            part.offsetScale(switch (bodyPart) {
                case NECK -> new Vector3f(0, 0.28F, 0);
                case HORN -> new Vector3f(0, 0.7F, 0);
                case LEGS -> new Vector3f(0, 0.18F, 0);
                case WINGS -> new Vector3f(0.1F);
                default -> ZERO;
            });
        }
    },
    YEARLING(SizePreset.YEARLING) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, PoseStack stack) {
            switch (part) {
                case NECK:
                    stack.translate(0, -0.1F, 0);
                    if (attributes.isCrouching) stack.translate(0, 1.3F * -0.05F, 0.15F);
                    if (attributes.isSwimming) stack.translate(0, 1.3F * -0.1F, 0);
                    break;
                case HEAD:
                    stack.translate(0, -0.1F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.04F, 0);
                    if (attributes.isSwimming) stack.translate(0, -0.05F, 0);
                    break;
                case BACK:
                    stack.translate(0, -0.05F, 0);
                    if (attributes.isCrouching) stack.translate(0, -0.05F, 0);
                case BODY:
                case TAIL:
                    stack.translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    if (!attributes.isLyingDown) stack.translate(0, -0.2F, 0);
                    if (attributes.isGoingFast) stack.translate(0, 0.1F, 0);
                    break;
                default:
            }
        }

        @Override
        public void transform(ModelAttributes attributes, BodyPart bodyPart, ModelPart part) {
            part.offsetScale(switch (bodyPart) {
                case NECK -> new Vector3f(0, 0.3F, 0);
                case HEAD -> new Vector3f(0.15F);
                case LEGS -> new Vector3f(0, 0.18F, 0);
                default -> ZERO;
            });
        }
    },
    SQUAT(SizePreset.SQUAT) {
        @Override
        public void transform(ModelAttributes attributes, BodyPart part, PoseStack stack) {
            switch (part) {
                case NECK:
                    if (!attributes.isLyingDown || attributes.isSwimming) stack.translate(0, 0.1F, 0);
                    if (attributes.isLyingDown) stack.translate(0, -0.1F, 0);
                    if (attributes.isCrouching) stack.translate(-0.042F, 0.04F, 0.15F);
                    break;
                case HEAD:
                    if (!attributes.isLyingDown) stack.translate(0, 0.1F, 0);
                    if (attributes.isLyingDown) stack.translate(0, -0.1F, 0);
                    if (attributes.isSwimming) stack.translate(0, 0.0575F, 0);
                    if (attributes.isCrouching) stack.translate(0, 0.0805F, 0);
                    break;
                case BACK:
                    stack.translate(0, 0.06F, 0);
                    if (attributes.isCrouching) stack.translate(0, -0.08F, 0);
                case BODY:
                case TAIL:
                    stack.translate(0, -0.4F, -0.025F);
                    if (!attributes.isLyingDown) stack.translate(0, 0.5F, 0);
                    if (attributes.isLyingDown) stack.translate(0, 0.2F, 0);
                    if (attributes.isCrouching) stack.translate(0, -0.065F, 0);
                    break;
                case LEGS:
                    if (!attributes.isLyingDown) stack.translate(0, 0.2F, -0.1F);
                    break;
                default:
            }
        }

        @Override
        public void transform(ModelAttributes attributes, BodyPart bodyPart, ModelPart part) {
            part.offsetScale(switch (bodyPart) {
                case NECK -> new Vector3f(0.4F, 0, 0.1F);
                case HEAD -> new Vector3f(0.15F);
                case BACK, BODY -> new Vector3f(0.4F, 0.3F, 0);
                case LEGS -> new Vector3f(0.1F, -0.2F, 0.1F);
                default -> ZERO;
            });
        }
    };

    static final Vector3f ZERO = new Vector3f();
    private static final Map<Size, PonyTransformation> REGISTRY = Arrays.stream(values()).collect(Collectors.toMap(i -> i.size, Function.identity()));

    private final Size size;

    PonyTransformation(Size size) {
        this.size = size;
    }

    @Override
    public abstract void transform(ModelAttributes attributes, BodyPart part, PoseStack stack);

    @Override
    public void transform(ModelAttributes attributes, BodyPart bodyPart, ModelPart part) {

    }

    public static PonyTransformation forSize(Size size) {
        return REGISTRY.getOrDefault(size, NORMAL);
    }
}
