package com.minelittlepony.client.transform;

import net.minecraft.util.math.Vec3d;

import com.google.common.collect.Maps;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IModel;
import com.minelittlepony.pony.meta.Size;
import com.mojang.blaze3d.platform.GlStateManager;

import java.util.Map;

public enum PonyTransformation {

    NORMAL(Size.NORMAL, 0, 3F, 0.75F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.getAttributes().isSwimming) translate(0, -0.3F, 0);
            if (model.getAttributes().isCrouching) translate(0, -0.2F, 0);
            if (model.getAttributes().isSleeping) translate(0, -0.61F, 0.1F);
            if (model.isRiding()) translate(0, -0.2F, -0.2F);

            switch (part) {
                case NECK:
                    if (model.getAttributes().isCrouching) translate(-0.03F, 0.03F, 0.1F);
                    break;
                case HEAD:
                    if (model.getAttributes().isCrouching) translate(0, 0.1F, 0);
                    if (model.getAttributes().isSwimmingRotated) translate(0, 0.37F, 0.45F);
                    break;
                case BACK:
                    translateVec(riderOffset);
                    break;
                default:
            }
        }
    },
    LANKY(Size.LANKY, 0, 2.6F, 0.75F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.getAttributes().isSwimming) translate(0, -0.2F, 0);
            if (model.getAttributes().isCrouching) translate(0, -0.15F, 0);
            if (model.getAttributes().isSleeping) translate(0, -0.6F, 0.15F);
            if (model.isRiding()) translate(0, 0, -0.2F);

            switch (part) {
                case NECK:
                    translate(0, -0.15F, -0.07F);
                    if (model.getAttributes().isCrouching) translate(-0.03F, 0.16F, 0.07F);
                    if (model.getAttributes().isSwimmingRotated) translate(0, 0.5F, 0.25F);
                    break;
                case HEAD:
                    translate(0, -0.17F, -0.04F);
                    if (model.getAttributes().isSleeping) translate(0, 0, -0.1F);
                    if (model.getAttributes().isCrouching) translate(0, 0.15F, 0);
                    if (model.getAttributes().isSwimmingRotated) translate(0, 0.45F, 0.45F);
                    break;
                case BODY:
                    translate(0, -0.2F, -0.04F);
                    scale(0.9F, 1.2F, 0.9F);
                    break;
                case TAIL:
                    translate(0, -0.2F, 0.08F);
                    break;
                case LEGS:
                    translate(0, -0.18F, 0);
                    scale(0.9F, 1.12F, 0.9F);
                    break;
                case BACK:
                    translateVec(riderOffset);
                    break;
            }
        }
    },
    BULKY(Size.BULKY, 0, 2.3F, 0.75F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.getAttributes().isCrouching) translate(0, -0.15F, 0);
            if (model.getAttributes().isSleeping) translate(0, -0.6F, 0.25F);
            if (model.isRiding()) translate(0, 0, -0.2F);

            switch (part) {
                case NECK:
                    translate(0, -0.15F, -0.07F);
                    if (model.getAttributes().isCrouching) translate(-0.03F, 0.16F, 0.07F);
                    if (model.getAttributes().isSwimmingRotated) translate(0, 0.5F, 0.25F);
                    break;
                case HEAD:
                    translate(0, -0.17F, -0.04F);
                    if (model.getAttributes().isSleeping) translate(0, 0, -0.1F);
                    if (model.getAttributes().isCrouching) translate(0, 0.15F, 0);
                    if (model.getAttributes().isSwimmingRotated) translate(0, 0.15F, 0.25F);
                    break;
                case BODY:
                    translate(0, -0.2F, -0.04F);
                    scale(1.15F, 1.2F, 1.2F);
                    break;
                case TAIL:
                    translate(0, -0.2F, 0.08F);
                    break;
                case LEGS:
                    translate(0, -0.18F, 0);
                    scale(1.15F, 1.12F, 1.15F);
                    break;
                case BACK:
                    translateVec(riderOffset);
                    break;
            }
        }
    },
    FOAL(Size.FOAL, 0, 3.8F, 0.75F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.getAttributes().isSwimming) translate(0, -0.9F, 0);
            if (model.getAttributes().isCrouching) translate(0, -0.3F, 0);
            if (model.getAttributes().isSleeping) translate(0, -0.65F, -0.3F);
            if (model.isRiding()) translate(0, -0.6F, -0.2F);

            translate(0, 0.2F, 0);

            switch (part) {
                case NECK:
                    translate(0, 0, 0.04F);
                    scale(1.3F, 1.3F, 1.3F);
                    if (model.getAttributes().isCrouching) translate(0, -0.01F, 0.15F);
                    break;
                case HEAD:
                    if (model.getAttributes().isSwimmingRotated) translate(0, 0.9F, 0.9F);
                    scale(1.3F, 1.3F, 1.3F);
                    break;
                case LEGS:
                    translate(0, 0.09F, 0);
                    scale(1, 0.81F, 1);
                    break;
                case BACK:
                    translateVec(riderOffset);
                    break;
                default:
            }
        }
    },
    TALL(Size.TALL, 0, 2.2F, 0.75F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.getAttributes().isCrouching) translate(0, -0.15F, 0);
            if (model.getAttributes().isSleeping) translate(0, -0.5F, 0.35F);
            if (model.isRiding()) translate(0, 0.1F, -0.2F);

            switch (part) {
                case NECK:
                    translate(0, -0.09F, 0);
                    scale(1, 1.1F, 1);
                    if (model.getAttributes().isCrouching) translate(-0.02F, -0.02F, 0.1F);
                    break;
                case HEAD:
                    translate(0.01F, -0.15F, 0);
                    if (model.getAttributes().isCrouching) translate(0, 0.04F, 0);
                    if (model.getAttributes().isSwimmingRotated) translate(0, 0.17F, 0.25F);
                    break;
                case BODY:
                case TAIL:
                    translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    translate(0, -0.27F, 0.03F);
                    scale(1, 1.18F, 1);
                    if (model.getAttributes().isGoingFast) translate(0, 0.05F, 0);
                    break;
                case BACK:
                    riderOffset = new Vec3d(0, 2.2F, 0.75F);
                    translateVec(riderOffset);
                    break;
            }
        }
    },
    YEARLING(Size.YEARLING, 0, 3.8F, 0.75F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.getAttributes().isSwimming) translate(0, -0.6F, 0);
            if (model.getAttributes().isCrouching) translate(0, -0.15F, 0);
            if (model.getAttributes().isSleeping) translate(0, -0.4F, -0.3F);
            if (model.isRiding()) translate(0, -0.4F, -0.2F);

            switch (part) {
                case NECK:
                    translate(0, -0.09F, -0.01F);
                    scale(1, 1.1F, 1);
                    if (model.getAttributes().isCrouching) translate(-0.02F, -0.02F, 0.1F);
                    break;
                case HEAD:
                    translate(0, -0.15F, 0.01F);
                    if (model.getAttributes().isCrouching) translate(0, 0.04F, 0);
                    if (model.getAttributes().isSwimmingRotated) translate(0, 0.8F, 0.85F);
                    scale(1.15F, 1.15F, 1.15F);
                    break;
                case BODY:
                case TAIL:
                    translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    translate(0, -0.265F, 0.03F);
                    scale(1, 1.18F, 1);
                    if (model.getAttributes().isGoingFast) translate(0, 0.05F, 0);
                    break;
                case BACK:
                    translateVec(riderOffset);
                    break;
            }
        }
    };

    private static final Map<Size, PonyTransformation> sizeToTransform = Maps.newEnumMap(Size.class);

    static {
        for (PonyTransformation i : values()) {
            sizeToTransform.put(i.size, i);
        }
    }

    protected Vec3d riderOffset;

    private final Size size;

    PonyTransformation(Size size, float rX, float rY, float rZ) {
        this.size = size;
        riderOffset = new Vec3d(rX, rY, rZ);
    }

    public static void translateVec(Vec3d offset) {
        GlStateManager.translated(offset.x, offset.y, offset.z);
    }

    protected void scale(float x, float y, float z) {
        GlStateManager.scalef(x, y, z);
    }

    protected void translate(float x, float y, float z) {
        GlStateManager.translatef(x, y, z);
    }

    public Vec3d getRiderOffset() {
        return riderOffset;
    }

    public abstract void transform(IModel model, BodyPart part);




    public static PonyTransformation forSize(Size size) {
        return sizeToTransform.getOrDefault(size, NORMAL);
    }
}
