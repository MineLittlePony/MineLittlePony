package com.minelittlepony.transform;

import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import net.minecraft.util.math.Vec3d;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.capabilities.IModel;

public enum PonyTransformation {

    NORMAL(0, 3, 0.5F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.isCrouching()) translate(0, -0.2F, 0);
            if (model.isSleeping()) translate(0, -0.61F, 0);
            if (model.isRiding()) translate(0, -0.2F, -0.2F);

            switch (part) {
                case NECK:
                    if (model.isCrouching()) translate(-0.03F, 0.03F, 0.1F);
                    break;
                case HEAD:
                    if (model.isCrouching()) translate(0, 0.1F, 0);
                    break;
                case BACK:
                    translateVec(riderOffset);
                    break;
                default:
            }
        }
    },
    LANKY(0, 2.3F, 0.3F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.isCrouching()) translate(0, -0.15F, 0);
            if (model.isSleeping()) translate(0, -0.98F, 0.2F);
            if (model.isRiding()) translate(0, 0, -0.2F);

            switch (part) {
                case NECK:
                    translate(0, -0.15F, -0.07F);
                    if (model.isCrouching()) translate(-0.03F, 0.16F, 0.07F);
                    break;
                case HEAD:
                    translate(0, -0.17F, -0.04F);
                    if (model.isSleeping()) translate(0, 0, -0.1F);
                    if (model.isCrouching()) translate(0, 0.15F, 0);
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
    BULKY(0, 2.3F, 0.3F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.isCrouching()) translate(0, -0.15F, 0);
            if (model.isSleeping()) translate(0, -0.98F, 0.2F);
            if (model.isRiding()) translate(0, 0, -0.2F);

            switch (part) {
                case NECK:
                    translate(0, -0.15F, -0.07F);
                    if (model.isCrouching()) translate(-0.03F, 0.16F, 0.07F);
                    break;
                case HEAD:
                    translate(0, -0.17F, -0.04F);
                    if (model.isSleeping()) translate(0, 0, -0.1F);
                    if (model.isCrouching()) translate(0, 0.15F, 0);
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
    FOAL(0, 4.5F, 0.6F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.isCrouching()) translate(0, -0.3F, 0);
            if (model.isSleeping()) translate(0, -0.6F, -0.5F);
            if (model.isRiding()) translate(0, -0.6F, -0.2F);

            translate(0, 0.2F, 0);

            switch (part) {
                case NECK:
                    translate(0, 0, 0.04F);
                    scale(1.3F, 1.3F, 1.3F);
                    if (model.isCrouching()) translate(0, -0.01F, 0.15F);
                    break;
                case HEAD:
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
    TALL(0, 2F, 0.6F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.isCrouching()) translate(0, -0.15F, 0);
            if (model.isSleeping()) translate(0, -0.5F, 0.25F);
            if (model.isRiding()) translate(0, 0.1F, -0.2F);

            switch (part) {
                case NECK:
                    translate(0, -0.09F, 0);
                    scale(1, 1.1F, 1);
                    if (model.isCrouching()) translate(-0.02F, -0.02F, 0.1F);
                    break;
                case HEAD:
                    translate(0.01F, -0.15F, 0);
                    if (model.isCrouching()) translate(0, 0.04F, 0);
                    break;
                case BODY:
                case TAIL:
                    translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    translate(0, -0.27F, 0.03F);
                    scale(1, 1.18F, 1);
                    if (model.isGoingFast()) translate(0, 0.05F, 0);
                    break;
                case BACK:
                    translateVec(riderOffset);
                    break;
            }
        }
    },
    YEARLING(0, 4.3F, 0.6F) {
        @Override
        public void transform(IModel model, BodyPart part) {
            if (model.isCrouching()) translate(0, -0.15F, 0);
            if (model.isSleeping()) translate(0, -0.5F, 0.25F);
            if (model.isRiding()) translate(0, -0.4F, -0.2F);

            switch (part) {
                case NECK:
                    translate(0, -0.09F, -0.01F);
                    scale(1, 1.1F, 1);
                    if (model.isCrouching()) translate(-0.02F, -0.02F, 0.1F);
                    break;
                case HEAD:
                    translate(0, -0.15F, 0.01F);
                    if (model.isCrouching()) translate(0, 0.04F, 0);
                    scale(1.15F, 1.15F, 1.15F);
                    break;
                case BODY:
                case TAIL:
                    translate(0, -0.1F, 0);
                    break;
                case LEGS:
                    translate(0, -0.265F, 0.03F);
                    scale(1, 1.18F, 1);
                    if (model.isGoingFast()) translate(0, 0.05F, 0);
                    break;
                case BACK:
                    translateVec(riderOffset);
                    break;
            }
        }
    };

    protected final Vec3d riderOffset;

    PonyTransformation(float rX, float rY, float rZ) {
        riderOffset = new Vec3d(rX, rY, rZ);
    }

    public static void translateVec(Vec3d offset) {
        translate(offset.x, offset.y, offset.z);
    }

    public Vec3d getRiderOffset() {
        return riderOffset;
    }

    public abstract void transform(IModel model, BodyPart part);
}
