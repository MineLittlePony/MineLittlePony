package com.minelittlepony.model;

import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyData;
import com.minelittlepony.pony.data.PonySize;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * TODO: move this into constructor and make separate classes for the races.
 */
public abstract class AbstractPonyModel extends ModelPlayer {

    /**
     * The model's current scale.
     */
    protected float scale = 0.0625F;

    public boolean isFlying;
    public boolean isSleeping;

    /**
     * Associcated pony data.
     */
    public IPonyData metadata = new PonyData();
    
    /**
     * Vertical pitch whilst flying.
     */
    public float motionPitch;
    
    /**
     * Flag indicating that this model is performing a rainboom (flight).
     */
    public boolean rainboom;
    
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
        initPositions(yOffset, stretch);
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
        }
    }

    /**
     * Returns true if the default minecraft handling should be used.
     */
    protected boolean doCancelRender() {
        return false;
    }
    
    /**
     * Returns true if this model is on the ground and crouching.
     */
    public boolean isCrouching() {
        return isSneak && !isFlying;
    }

    /**
     * Returns true if the given entity can and is flying, or has an elytra.
     */
    public boolean isFlying(Entity entity) {
        return (isFlying && metadata.getRace().hasWings()) ||
                (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isElytraFlying());
    }
    
    /**
     * Returns true if the current model is a child or a child-like foal.
     */
    public boolean isChild() {
        return metadata.getSize() == PonySize.FOAL || isChild;
    }

    /**
     * Adjusts the rotation center of the given renderer by the given amounts in each direction.
     */
    public void shiftRotationPoint(ModelRenderer renderer, float x, float y, float z) {
        renderer.rotationPointX += x;
        renderer.rotationPointY += y;
        renderer.rotationPointZ += z;
    }
    
    /**
     * Rotates the provided arm to the correct orientation for holding an item.
     * 
     * @param arm           The arm to rotate
     * @param direction     Direction multiplier. 1 for right, -1 for left.
     * @param swingProgress How far we are through the current swing
     * @param tick          Render partial ticks
     */
    protected void rotateArmHolding(ModelRenderer arm, float direction, float swingProgress, float tick) {
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
        if (isRiding) translate(0, -0.6F, -0.2F);

        if (isSleeping) {
            rotate(90, 0, 1, 0);
            rotate(270, 0, 0, 1);
            rotate(90, 0, 1, 0);
            rotate(180, 0, 0, 1);
            rotate(180, 0, 1, 0);
        }

        if (isChild()) {
            transformFoal(part);
        } else if (metadata.getSize() == PonySize.LARGE) {
            transformLarge(part);
        } else if (metadata.getSize() == PonySize.TALL) {
            transformTall(part);
        } else {
            if (isSleeping) translate(0, -0.75F, 0.25F);
        }
        if (part == BodyPart.HEAD) {
            rotate(motionPitch, 1, 0, 0);
        }
    }
    
    private void transformTall(BodyPart part) {
        if (isSleeping) translate(0, -0.65F, 0.25F);

        switch (part) {
            case HEAD:
                translate(0, -0.15F, 0.01F);
                if (isCrouching()) translate(0, 0.05F, 0);
                break;
            case NECK:
                translate(0, -0.19F, -0.01F);
                scale(1, 1.1F, 1);
                if (isCrouching()) translate(0, -0.06F, -0.04F);
                break;
            case BODY:
            case TAIL:
                translate(0, -0.1F, 0);
                scale(1, 1, 1);
                break;
            case LEGS:
                translate(0, -0.25F, 0.03F);
                scale(1, 1.18F, 1);
                if (rainboom) translate(0, 0.05F, 0);
                break;
        }
    }
    
    private void transformLarge(BodyPart part) {
        if (this.isSleeping) translate(0, -0.7F, 0.2F);

        switch (part) {
            case HEAD:
                translate(0, -0.17F, -0.04F);
                if (isSleeping) translate(0, 0, -0.1F);
                if (isCrouching()) translate(0, 0.15F, 0);

                break;
            case NECK:
                translate(0, -0.15F, -0.07F);
                if (isCrouching()) translate(0, 0, -0.05F);

                break;
            case BODY:
                translate(0, -0.2F, -0.04F);
                scale(1.15F, 1.2F, 1.2F);
                break;
            case TAIL:
                translate(0, -0.2F, 0.08F);
                break;
            case LEGS:
                translate(0, -0.14F, 0);
                scale(1.15F, 1.12F, 1.15F);
                break;
        }
    }
    
    private void transformFoal(BodyPart part) {
        if (isCrouching()) translate(0, -0.12F, 0.0F);
        if (isSleeping) translate(0, -1.2F, 0.25F);
        if (isRiding) translate(0, -.1, 0);
        
        switch (part) {
            case NECK:
            case HEAD:
                translate(0, 0.76F, 0);
                scale(0.9F, 0.9F, 0.9F);
                if (part == BodyPart.HEAD)
                    break;
                if (isCrouching()) translate(0, -0.01F, 0.15F);
                break;
            case BODY:
            case TAIL:
                translate(0, 0.76F, -0.04F);
                scale(0.6F, 0.6F, 0.6F);
                break;
            case LEGS:
                translate(0, 0.89F, 0);
                scale(0.6F, 0.41F, 0.6F);
                if (isCrouching()) translate(0, 0.12F, 0);
                if (rainboom) translate(0, -0.08F, 0);

                break;
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
            rainboom = pony.rainboom;
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
