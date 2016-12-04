package com.minelittlepony.model;

import com.minelittlepony.PonyData;
import com.minelittlepony.PonySize;
import com.minelittlepony.model.pony.ModelHumanPlayer;
import com.minelittlepony.model.pony.ModelPlayerPony;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

import static net.minecraft.client.renderer.GlStateManager.rotate;
import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

/**
 * TODO move this into constructor and make separate classes for the races.
 */
public abstract class AbstractPonyModel extends ModelPlayer {

    protected float scale = 0.0625F;

    public ModelRenderer steveLeftArm;
    public ModelRenderer steveRightArm;
    public ModelRenderer steveLeftArmwear;
    public ModelRenderer steveRightArmwear;

    public boolean isFlying;
    public boolean isSleeping;

    public PonyData metadata = new PonyData();
    public float motionPitch;


    public AbstractPonyModel(boolean arms) {
        super(0, arms);
        this.steveLeftArm = this.bipedLeftArm;
        this.steveRightArm = this.bipedRightArm;
        this.steveLeftArmwear = this.bipedLeftArmwear;
        this.steveRightArmwear = this.bipedLeftArmwear;
    }

    public void init(float yOffset, float stretch) {
        this.initTextures();
        this.initPositions(yOffset, stretch);
    }

    protected void initTextures() {
    }

    protected void initPositions(float yOffset, float stretch) {
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        if (doCancelRender()) {
            super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
            return;
        }
        this.steveRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.steveRightArm.rotateAngleY = 0;
        this.steveRightArm.rotateAngleZ = 0;
        this.steveLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.steveLeftArm.rotateAngleY = 0;
        this.steveLeftArm.rotateAngleZ = 0;

        copyModelAngles(steveRightArm, steveRightArmwear);
        copyModelAngles(steveLeftArm, steveLeftArmwear);
    }

    protected boolean doCancelRender() {
        return false;
    }

    public static void setRotationPoint(ModelRenderer aRenderer, float setX, float setY, float setZ) {
        aRenderer.rotationPointX = setX;
        aRenderer.rotationPointY = setY;
        aRenderer.rotationPointZ = setZ;
    }

    public static void shiftRotationPoint(ModelRenderer aRenderer, float shiftX, float shiftY, float shiftZ) {
        aRenderer.rotationPointX += shiftX;
        aRenderer.rotationPointY += shiftY;
        aRenderer.rotationPointZ += shiftZ;
    }

    public void transform(BodyPart part) {
        if (this instanceof ModelHumanPlayer)
            return;
        if (this.isRiding) {
            translate(0.0F, -0.6F, -0.2F);
        }

        if (this.isSleeping) {
            rotate(90.0F, 0.0F, 1.0F, 0.0F);
            rotate(270.0F, 0.0F, 0.0F, 1.0F);
            rotate(90.0F, 0.0F, 1.0F, 0.0F);
            rotate(180.0F, 0.0F, 0.0F, 1.0F);
            rotate(180.0F, 0.0F, 1.0F, 0.0F);
        }

        if (this.metadata.getSize() == PonySize.FOAL || isChild) {
            if (this.isSneak && !this.isFlying) {
                translate(0.0F, -0.12F, 0.0F);
            }

            if (this.isSleeping) {
                translate(0.0F, -1.2F, 0.25F);
            }
            if (this.isRiding) {
                translate(0, -.1, 0);
            }
            switch (part) {
                case NECK:
                case HEAD:
                    translate(0.0F, 0.76F, 0.0F);
                    scale(0.9F, 0.9F, 0.9F);
                    if (part == BodyPart.HEAD)
                        break;
                    if (this.isSneak && !this.isFlying) {
                        translate(0.0F, -0.01F, 0.15F);
                    }
                    break;
                case BODY:
                case TAIL:
                    translate(0.0F, 0.76F, -0.04F);
                    scale(0.6F, 0.6F, 0.6F);
                    break;
                case LEGS:
                    translate(0.0F, 0.89F, 0.0F);
                    scale(0.6F, 0.41F, 0.6F);
                    if (this.isSneak && !this.isFlying) {
                        translate(0.0F, 0.12F, 0.0F);
                    }

                    if (this instanceof ModelPlayerPony && ((ModelPlayerPony) this).rainboom) {
                        translate(0.0F, -0.08F, 0.0F);
                    }

                    break;
            }

        } else if (this.metadata.getSize() == PonySize.LARGE) {
            if (this.isSleeping) {
                translate(0.0F, -0.7F, 0.2F);
            }

            switch (part) {
                case HEAD:

                    translate(0.0F, -0.17F, -0.04F);
                    if (this.isSleeping) {
                        translate(0.0F, 0.0F, -0.1F);
                    }

                    if (this.isSneak && !this.isFlying) {
                        translate(0.0F, 0.15F, 0.0F);
                    }

                    break;
                case NECK:
                    translate(0.0F, -0.15F, -0.07F);
                    if (this.isSneak && !this.isFlying) {
                        translate(0.0F, 0.0F, -0.05F);
                    }

                    break;
                case BODY:
                    translate(0.0F, -0.2F, -0.04F);
                    scale(1.15F, 1.2F, 1.2F);
                    break;
                case TAIL:
                    translate(0.0F, -0.2F, 0.08F);
                    break;
                case LEGS:
                    translate(0.0F, -0.14F, 0.0F);
                    scale(1.15F, 1.12F, 1.15F);
                    break;
            }
        } else if (this.metadata.getSize() == PonySize.TALL) {
            if (this.isSleeping) {
                translate(0.0F, -0.65F, 0.25F);
            }

            switch (part) {
                case HEAD:
                    translate(0.0F, -0.15F, 0.01F);
                    if (this.isSneak && !this.isFlying) {
                        translate(0.0F, 0.05F, 0.0F);
                    }
                    break;
                case NECK:
                    translate(0.0F, -0.19F, -0.01F);
                    scale(1.0F, 1.1F, 1.0F);
                    if (this.isSneak && !this.isFlying) {
                        translate(0.0F, -0.06F, -0.04F);
                    }
                    break;
                case BODY:
                case TAIL:
                    translate(0.0F, -0.1F, 0.0F);
                    scale(1.0F, 1.0F, 1.0F);
                    break;
                case LEGS:
                    translate(0.0F, -0.25F, 0.03F);
                    scale(1.0F, 1.18F, 1.0F);
                    if (this instanceof ModelPlayerPony && ((ModelPlayerPony) this).rainboom) {
                        translate(0.0F, 0.05F, 0.0F);
                    }
                    break;
            }
        } else {
            if (this.isSleeping) {
                translate(0.0F, -0.75F, 0.25F);
            }
        }
        if (part == BodyPart.HEAD) {
            rotate(motionPitch, 1F, 0F, 0F);
        }
    }

    @Override
    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);
        if (model instanceof AbstractPonyModel) {
            AbstractPonyModel pony = (AbstractPonyModel) model;
            this.isFlying = pony.isFlying;
            this.isSleeping = pony.isSleeping;
            this.metadata = pony.metadata;
            this.motionPitch = pony.motionPitch;
        }
    }

    @Override
    public ModelRenderer getRandomModelBox(Random rand) {
        // empty lists cause problems
        ModelRenderer mr;
        do {
            // try until it's not
            mr = super.getRandomModelBox(rand);
        } while (mr.cubeList.isEmpty());
        return mr;
    }
}
