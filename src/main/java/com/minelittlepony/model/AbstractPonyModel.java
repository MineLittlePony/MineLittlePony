package com.minelittlepony.model;

import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.model.ponies.ModelPlayerPony;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyData;
import com.minelittlepony.pony.data.PonySize;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * TODO move this into constructor and make separate classes for the races.
 */
public abstract class AbstractPonyModel extends ModelPlayer {

    protected float scale = 0.0625F;

    public boolean isFlying;
    public boolean isSleeping;

    public IPonyData metadata = new PonyData();
    public float motionPitch;

    public AbstractPonyModel(boolean arms) {
        super(0, arms);
    }

    /**
     * Sets up this model's initial values, like a constructor...
     * @param yOffset YPosition for this model. Always 0.
     * @param stretch Scaling factor for this model. Ranges above or below 0 (no change).
     */
    public void init(float yOffset, float stretch) {
        initTextures();
        this.initPositions(yOffset, stretch);
    }
    
    /**
     * Returns a new pony armour to go with this model. Called on startup by a model wrapper.
     */
    public abstract PonyArmor createArmour();
    
    /**
     * Loads texture values.
     */
    protected abstract void initTextures();

    /**
     * Loads texture positions and boxes. Pretty much just finishes the job of initTextures.
     */
    protected abstract void initPositions(float yOffset, float stretch);

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        if (doCancelRender()) {
            super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
            return;
        }
    }

    /**
     * Returns true if the default minecraft handling should be used.
     */
    protected boolean doCancelRender() {
        return false;
    }

    public static void shiftRotationPoint(ModelRenderer aRenderer, float shiftX, float shiftY, float shiftZ) {
        aRenderer.rotationPointX += shiftX;
        aRenderer.rotationPointY += shiftY;
        aRenderer.rotationPointZ += shiftZ;
    }
    
    /**
     * Rotates the provided arm to the correct orientation for holding an item.
     * 
     * @param arm           The arm to rotate
     * @param direction     Direction multiplier. 1 for right, -1 for left.
     * @param swingProgress How far we are through the current swing
     * @param tick          Render partial ticks
     */
    protected static void rotateArmHolding(ModelRenderer arm, float direction, float swingProgress, float tick) {
        float swing = MathHelper.sin(swingProgress * (float)Math.PI);
        float roll = MathHelper.sin((1 - (1 - swingProgress) * (1 - swingProgress)) * (float)Math.PI);
        
        arm.rotateAngleZ = 0.0F;
        arm.rotateAngleY = direction * (0.1F - swing * 0.6F);
        arm.rotateAngleX = -1.5707964F;
        arm.rotateAngleX -= swing * 1.2F - roll * 0.4F;
        arm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        arm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.1F;
    }

    /**
     * Applies a transform particular to a certain body part.
     * 
     * FIXME: Too long! Is there a better way to do this?
     */
    public void transform(BodyPart part) {
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

    /**
     * Copies this model's attributes from some other.
     */
    @Override
    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);
        if (model instanceof AbstractPonyModel) {
            AbstractPonyModel pony = (AbstractPonyModel) model;
            isFlying = pony.isFlying;
            isSleeping = pony.isSleeping;
            metadata = pony.metadata;
            motionPitch = pony.motionPitch;
        }
    }

    @Override
    public ModelRenderer getRandomModelBox(Random rand) {
        // grab one at random, but cycle through the list until you find one that's filled.
        // Return if you find one, or if you get back to where you started in which case there isn't any.
        int index = rand.nextInt(boxList.size());
        int i = index;

        ModelRenderer mr;
        do {
            mr = boxList.get(index);
            if (!mr.cubeList.isEmpty()) return mr;

            i = (i + 1) % boxList.size();
        } while (i != index);

        return mr;
    }
}
