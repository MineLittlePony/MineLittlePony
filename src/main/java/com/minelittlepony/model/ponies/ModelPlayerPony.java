package com.minelittlepony.model.ponies;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.ModelPonyArmor;
import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.model.components.PegasusWings;
import com.minelittlepony.model.components.PonySnout;
import com.minelittlepony.model.components.PonyTail;
import com.minelittlepony.model.components.UnicornHorn;
import com.minelittlepony.render.plane.PlaneRenderer;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static com.minelittlepony.model.PonyModelConstants.*;

public class ModelPlayerPony extends AbstractPonyModel {

    private final boolean smallArms;
    public boolean rainboom;

    public ModelRenderer bipedCape;
    
    public PlaneRenderer[] Bodypiece;
    public PlaneRenderer BodypieceNeck;
    
    public ModelRenderer unicornArmRight, unicornArmLeft;
    
    public PonyTail Tail;
    public PonySnout snout;
    public UnicornHorn horn;
    public PegasusWings wings;

    public ModelPlayerPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;
    }
    
    @Override
    public PonyArmor createArmour() {
        return new PonyArmor(new ModelPonyArmor(), new ModelPonyArmor());
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        snout = new PonySnout(this, yOffset, stretch);
        horn = new UnicornHorn(this, yOffset, stretch);
        wings = new PegasusWings(this, yOffset, stretch);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        this.checkRainboom(entityIn, limbSwingAmount);
        this.rotateHead(netHeadYaw, headPitch);

        float bodySwingRotation = 0.0F;
        if (this.swingProgress > -9990.0F && !this.metadata.hasMagic()) {
            bodySwingRotation = MathHelper.sin(MathHelper.sqrt(this.swingProgress) * 3.1415927F * 2.0F) * 0.2F;
        }

        rotateLook(limbSwing, limbSwingAmount, bodySwingRotation, ageInTicks);

        this.setLegs(limbSwing, limbSwingAmount, ageInTicks, entityIn);
        this.holdItem(limbSwingAmount);
        this.swingItem(entityIn, this.swingProgress);
        if (this.isSneak && !this.isFlying && !this.rainboom) {
            this.adjustBody(BODY_ROTATE_ANGLE_X_SNEAK, BODY_RP_Y_SNEAK, BODY_RP_Z_SNEAK);
            this.sneakLegs();
            this.setHead(0.0F, 6.0F, -2.0F);
        } else if (this.isRiding) {
            this.adjustBodyRiding();
            this.bipedLeftLeg.rotationPointZ = 15;
            this.bipedLeftLeg.rotationPointY = 10;
            this.bipedLeftLeg.rotateAngleX = (float) (Math.PI * -0.25);
            this.bipedLeftLeg.rotateAngleY = (float) (Math.PI * -0.2);

            this.bipedRightLeg.rotationPointZ = 15;
            this.bipedRightLeg.rotationPointY = 10;
            this.bipedRightLeg.rotateAngleX = (float) (Math.PI * -0.25);
            this.bipedRightLeg.rotateAngleY = (float) (Math.PI * 0.2);

            this.bipedLeftArm.rotateAngleZ = (float) (Math.PI * -0.06);
            this.bipedRightArm.rotateAngleZ = (float) (Math.PI * 0.06);
        } else {

            this.adjustBody(BODY_ROTATE_ANGLE_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);

            this.bipedRightLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.bipedLeftLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.swingArms(ageInTicks);
            this.setHead(0.0F, 0.0F, 0.0F);
        }
        
        if (this.isSleeping) {
            this.ponySleep();
        }

        this.aimBow(leftArmPose, rightArmPose, ageInTicks);

        this.fixSpecialRotations();
        this.fixSpecialRotationPoints(limbSwing);

        animateWears();

        this.bipedCape.rotationPointY = isSneak ? 2 : isRiding ? -4 : 0;

        this.snout.setGender(this.metadata.getGender());
        this.wings.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    }
    
    protected void adjustBodyRiding() {
        this.adjustBodyComponents(BODY_ROTATE_ANGLE_X_RIDING, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
        this.adjustNeck(BODY_ROTATE_ANGLE_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);
        this.setHead(0.0F, 0.0F, 0.0F);
    }
    
    protected void rotateLook(float limbSwing, float limbSwingAmount, float bodySwing, float ticks) {
        this.Tail.setRotationAndAngles(rainboom, limbSwing, limbSwingAmount, bodySwing, ticks);
        
        for (PlaneRenderer i : this.Bodypiece) {
            i.rotateAngleY = bodySwing * 0.2F;
        }
        
        this.bipedBody.rotateAngleY = bodySwing * 0.2F;
        this.BodypieceNeck.rotateAngleY = bodySwing * 0.2F;
        this.bipedHead.offsetY = 0f;
        this.bipedHead.offsetZ = 0f;
        this.bipedHeadwear.offsetY = 0f;
        this.bipedHeadwear.offsetZ = 0f;
    }

    private void animateWears() {
        copyModelAngles(bipedLeftArm, bipedLeftArmwear);
        copyModelAngles(bipedRightArm, bipedRightArmwear);
        copyModelAngles(bipedLeftLeg, bipedLeftLegwear);
        copyModelAngles(bipedRightLeg, bipedRightLegwear);
        copyModelAngles(bipedBody, bipedBodyWear);
    }

    protected void checkRainboom(Entity entity, float swing) {
        boolean flying = this.metadata.getRace().hasWings() && this.isFlying
                || entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isElytraFlying();

        this.rainboom = flying && swing >= 0.9999F;
    }

    protected void setHead(float posX, float posY, float posZ) {
        this.bipedHead.setRotationPoint(posX, posY, posZ);
        this.bipedHeadwear.setRotationPoint(posX, posY, posZ);
    }

    protected void rotateHead(float horz, float vert) {
        float headRotateAngleY;
        float headRotateAngleX;
        if (this.isSleeping) {
            headRotateAngleY = 1.4F;
            headRotateAngleX = 0.1F;
        } else {
            headRotateAngleY = horz / 57.29578F;
            headRotateAngleX = vert / 57.29578F;
        }

        final float max = (float) (0.5f - Math.toRadians(this.motionPitch));
        final float min = (float) (-1.25f - Math.toRadians(this.motionPitch));
        headRotateAngleX = Math.min(headRotateAngleX, max);
        headRotateAngleX = Math.max(headRotateAngleX, min);
        this.bipedHead.rotateAngleY = headRotateAngleY;
        this.bipedHead.rotateAngleX = headRotateAngleX;
        this.bipedHeadwear.rotateAngleY = headRotateAngleY;
        this.bipedHeadwear.rotateAngleX = headRotateAngleX;
    }

    protected void setLegs(float move, float swing, float tick, Entity entity) {
        this.rotateLegs(move, swing, tick, entity);
        this.adjustLegs();
    }
    
    public boolean isFlying(Entity entity) {
        return (isFlying && metadata.getRace().hasWings()) ||
                (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isElytraFlying());
    }

    protected void rotateLegs(float move, float swing, float tick, Entity entity) {
        float leftArm, rightArm, 
            leftLeg, rightLeg;
        
        
        if (isFlying(entity)) {
            if (this.rainboom) {
                rightArm = leftArm = ROTATE_270;
                rightLeg = leftLeg = ROTATE_90;
            } else {
                rightArm = leftArm = MathHelper.sin(-swing / 2);
                rightLeg = leftLeg = MathHelper.sin(swing / 2);
            }

            steveRightArm.rotateAngleY = bipedRightArm.rotateAngleY = 0.2F;
            bipedLeftArm.rotateAngleY = bipedRightLeg.rotateAngleY = -0.2F;
            
            this.bipedLeftLeg.rotateAngleY = 0.2F;

        } else {
            float PI = (float)Math.PI;
            float pi = PI * (float) Math.pow(swing, 16);
            
            float mve = move * 0.6662F; // magic number ahoy
            float srt = swing / 4;
            
            leftArm = MathHelper.cos(mve + pi) * srt;
            rightArm = MathHelper.cos(mve + PI + pi / 2) * srt;
            
            leftLeg = MathHelper.cos(mve + PI - (pi * 0.4f)) * srt;
            rightLeg = MathHelper.cos(mve + pi * 0.2f) * srt;
            
            this.steveRightArm.rotateAngleY = 0;
            this.unicornArmRight.rotateAngleY = 0;
            this.unicornArmLeft.rotateAngleY = 0;

            this.bipedRightArm.rotateAngleY = 0;
            this.bipedLeftArm.rotateAngleY = 0;
            this.bipedRightLeg.rotateAngleY = 0;
            this.bipedLeftLeg.rotateAngleY = 0;
        }

        this.bipedRightArm.rotateAngleX = rightArm;
        this.steveRightArm.rotateAngleX = rightArm;
        this.unicornArmRight.rotateAngleX = 0;
        this.unicornArmLeft.rotateAngleX = 0;

        this.bipedLeftArm.rotateAngleX = leftArm;
        this.bipedRightLeg.rotateAngleX = rightLeg;
        this.bipedLeftLeg.rotateAngleX = leftLeg;
        
        this.bipedRightArm.rotateAngleZ = 0;

        this.steveRightArm.rotateAngleZ = 0;
        this.unicornArmRight.rotateAngleZ = 0;
        this.unicornArmLeft.rotateAngleZ = 0;
        this.bipedLeftArm.rotateAngleZ = 0;
    }
    
    private float getLegOutset() {
        if (isSleeping) return 2.6f;
        if (isSneak && !isFlying) return smallArms ? 1 : 0;
        return 4;
    }

    protected void adjustLegs() {
        float sinBodyRotateAngleYFactor = MathHelper.sin(this.bipedBody.rotateAngleY) * 5;
        float cosBodyRotateAngleYFactor = MathHelper.cos(this.bipedBody.rotateAngleY) * 5;
        
        
        float legOutset = getLegOutset();
        float spread = rainboom ? 2 : 1;

        this.bipedRightArm.rotationPointZ = spread + sinBodyRotateAngleYFactor;
        this.steveRightArm.rotationPointZ = spread + sinBodyRotateAngleYFactor;
        this.bipedLeftArm.rotationPointZ = spread - sinBodyRotateAngleYFactor;
        this.steveRightArm.rotationPointX = -cosBodyRotateAngleYFactor;

        float rpxl = legOutset - cosBodyRotateAngleYFactor - 1;
        float rpxr = cosBodyRotateAngleYFactor + 2 - legOutset;
        
        bipedRightArm.rotationPointX = bipedRightLeg.rotationPointX = rpxl;
        bipedLeftArm.rotationPointX = bipedLeftLeg.rotationPointX = rpxr;

        bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
        bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;

        //noinspection SuspiciousNameCombination
        this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;

        bipedRightArm.rotationPointY = bipedLeftArm.rotationPointY = 8;
        bipedRightLeg.rotationPointZ = bipedLeftLeg.rotationPointZ = 10;
    }

    
    
    protected void holdItem(float swing) {
        if (!this.rainboom && !this.metadata.hasMagic()) {
            boolean bothHoovesAreOccupied = this.leftArmPose == ArmPose.ITEM && this.rightArmPose == ArmPose.ITEM;
            alignArmForAction(bipedLeftArm, leftArmPose, bothHoovesAreOccupied, swing);
            alignArmForAction(bipedRightArm, rightArmPose, bothHoovesAreOccupied, swing);
        } else if (this.metadata.hasMagic()) {
            if (this.leftArmPose == ArmPose.BLOCK) blockArm(unicornArmLeft);
            if (this.rightArmPose == ArmPose.BLOCK) blockArm(unicornArmRight);
        }

        this.horn.setUsingMagic(this.leftArmPose != ArmPose.EMPTY || this.rightArmPose != ArmPose.EMPTY);
    }
    
    @SuppressWarnings("incomplete-switch")
    private void alignArmForAction(ModelRenderer arm, ArmPose pose, boolean bothHoovesAreOccupied, float swing) {
        switch (pose) {
            case ITEM:
                float swag = 1;
                if (!isFlying && bothHoovesAreOccupied) {
                    swag = (float) (1 - Math.pow(swing, 2));
                }
                float rotationMultiplier = 0.5f + (1 - swag)/2;
                arm.rotateAngleX = this.bipedLeftArm.rotateAngleX * rotationMultiplier - ((float) Math.PI / 10) * swag;
            case EMPTY:
                arm.rotateAngleY = 0;
                break;
            case BLOCK:
                arm.rotateAngleX = arm.rotateAngleX / 2 - 0.9424779F;
                arm.rotateAngleY = (float) (Math.PI / 6);
                break;
        }
    }
    
    private void blockArm(ModelRenderer arm) {
        arm.rotateAngleX = arm.rotateAngleX * 0.5F - 0.9424779F;
        arm.rotateAngleY = (float) (Math.PI / 6);
    }

    protected void swingItem(Entity entity, float swingProgress) {
        if (swingProgress > -9990.0F && !this.isSleeping) {
            float f16 = 1.0F - swingProgress;
            f16 *= f16 * f16;
            f16 = 1.0F - f16;
            float f22 = MathHelper.sin(f16 * 3.1415927F);
            float f28 = MathHelper.sin(swingProgress * 3.1415927F);
            float f33 = f28 * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            
            EnumHandSide mainSide = this.getMainHand(entity);
            
            boolean mainRight = mainSide == EnumHandSide.RIGHT;
            ArmPose mainPose = mainRight ? this.rightArmPose : this.leftArmPose;
            
            if (this.metadata.hasMagic() && mainPose != ArmPose.EMPTY) {
                swingArm(mainRight ? this.unicornArmRight : this.unicornArmLeft, f22, f33, f28);
            } else {
                swingArm(this.getArmForSide(mainSide), f22, f33, f28);
                swingArm(mainRight ? this.steveRightArm : this.steveLeftArm, f22, f33, f28);
            }
        }
    }
    
    private void swingArm(ModelRenderer arm, float f22, float f33, float f28) {
        arm.rotateAngleX = (float) (this.unicornArmRight.rotateAngleX - (f22 * 1.2D + f33));
        arm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
        arm.rotateAngleZ = f28 * -0.4F;
    }

    protected void swingArms(float tick) {
        float cosTickFactor = MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        float sinTickFactor = MathHelper.sin(tick * 0.067F) * 0.05F;

        if (this.rightArmPose != ArmPose.EMPTY && !this.isSleeping) {
            
            if (this.metadata.hasMagic()) {
                this.unicornArmRight.rotateAngleZ += cosTickFactor;
                this.unicornArmRight.rotateAngleX += sinTickFactor;
            } else {
                this.bipedRightArm.rotateAngleZ += cosTickFactor;
                this.bipedRightArm.rotateAngleX += sinTickFactor;
                
                this.steveRightArm.rotateAngleZ += cosTickFactor;
                this.steveRightArm.rotateAngleX += sinTickFactor;
            }
        }
        if (this.leftArmPose != ArmPose.EMPTY && !this.isSleeping) {
            if (this.metadata.hasMagic()) {
                this.unicornArmLeft.rotateAngleZ += cosTickFactor;
                this.unicornArmLeft.rotateAngleX += sinTickFactor;
            } else {
                this.bipedLeftArm.rotateAngleZ += cosTickFactor;
                this.bipedLeftArm.rotateAngleX += sinTickFactor;
                
                this.steveLeftArm.rotateAngleZ += cosTickFactor;
                this.steveLeftArm.rotateAngleX += sinTickFactor;
            }
        }
    }

    protected void adjustBody(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        this.adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);
        this.adjustNeck(rotateAngleX, rotationPointY, rotationPointZ);
    }

    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        this.bipedBody.rotateAngleX = rotateAngleX;
        this.bipedBody.rotationPointY = rotationPointY;
        this.bipedBody.rotationPointZ = rotationPointZ;
        
        for (PlaneRenderer i : Bodypiece) {
            i.rotateAngleX = rotateAngleX;
            i.rotationPointY = rotationPointY;
            i.rotationPointZ = rotationPointZ;
        }
    }

    protected void adjustNeck(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        BodypieceNeck.setRotationPoint(NECK_ROT_X + rotateAngleX, rotationPointY, rotationPointZ);
    }

    protected void sneakLegs() {
        this.steveRightArm.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        
        this.unicornArmRight.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        this.unicornArmLeft.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;

        this.bipedRightArm.rotateAngleX -= SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        this.bipedRightLeg.rotationPointY = FRONT_LEG_RP_Y_SNEAK;
        
        this.bipedLeftArm.rotateAngleX -= SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        this.bipedLeftLeg.rotationPointY = FRONT_LEG_RP_Y_SNEAK;
    }

    protected void ponySleep() {
        this.bipedRightArm.rotateAngleX = ROTATE_270;
        this.bipedLeftArm.rotateAngleX = ROTATE_270;
        this.bipedRightLeg.rotateAngleX = ROTATE_90;
        this.bipedLeftLeg.rotateAngleX = ROTATE_90;
        
        float headPosX, headPosY, headPosZ;
        
        if (this.isSneak) {
            headPosY = 2;
            headPosZ = -1;
            headPosX = 1;
        } else {
            headPosY = 2;
            headPosZ = 1;
            headPosX = 1;
        }

        this.setHead(headPosX, headPosY, headPosZ);
        shiftRotationPoint(this.bipedRightArm, 0.0F, 2.0F, 6.0F);
        shiftRotationPoint(this.bipedLeftArm, 0.0F, 2.0F, 6.0F);
        shiftRotationPoint(this.bipedRightLeg, 0.0F, 2.0F, -8.0F);
        shiftRotationPoint(this.bipedLeftLeg, 0.0F, 2.0F, -8.0F);
    }

    protected void aimBow(ArmPose leftArm, ArmPose rightArm, float tick) {
        if (leftArm == ArmPose.BOW_AND_ARROW || rightArm == ArmPose.BOW_AND_ARROW) {

            if (this.metadata.hasMagic()) {
                if (rightArm == ArmPose.BOW_AND_ARROW) aimBowPony(unicornArmRight, tick, true);
                if (leftArm == ArmPose.BOW_AND_ARROW) aimBowPony(unicornArmLeft, tick, false);
            } else {
                if (rightArm == ArmPose.BOW_AND_ARROW) aimBowPony(bipedRightArm, tick, false);
                if (leftArm == ArmPose.BOW_AND_ARROW) aimBowPony(bipedLeftArm, tick, false);
            }
        }
    }

    protected void aimBowPony(ModelRenderer arm, float tick, boolean shift) {
        arm.rotateAngleZ = 0.0F;
        arm.rotateAngleY = -0.06F + this.bipedHead.rotateAngleY;
        arm.rotateAngleX = ROTATE_270 + this.bipedHead.rotateAngleX;
        arm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        arm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.05F;
        if (shift) shiftRotationPoint(arm, 0.0F, 0.0F, 1.0F);
    }

    protected void fixSpecialRotations() {
        this.Bodypiece[9].rotateAngleX += 0.5F;
        this.Bodypiece[10].rotateAngleX += 0.5F;
        this.Bodypiece[11].rotateAngleX += 0.5F;
        this.Bodypiece[12].rotateAngleX += 0.5F;
        this.Bodypiece[13].rotateAngleX += 0.5F;
    }

    protected void fixSpecialRotationPoints(float move) {
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        pushMatrix();
        this.transform(BodyPart.HEAD);
        this.renderHead(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        popMatrix();

        pushMatrix();
        this.transform(BodyPart.NECK);
        this.renderNeck();
        popMatrix();

        pushMatrix();
        this.transform(BodyPart.BODY);
        this.renderBody(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.Tail.render(this.metadata.getTail(), this.scale);
        popMatrix();

        pushMatrix();
        this.transform(BodyPart.LEGS);
        this.renderLegs();
        popMatrix();
    }

    protected void renderHead(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.bipedHead.render(this.scale);
        this.bipedHeadwear.render(this.scale);
        this.bipedHead.postRender(scale);
        this.horn.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

    }

    protected void renderNeck() {
        GlStateManager.scale(0.9, 0.9, 0.9);
        this.BodypieceNeck.render(this.scale);
    }

    protected void renderBody(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.bipedBody.render(this.scale);
        if (this.textureHeight == 64) {
            this.bipedBodyWear.render(this.scale);
        }
        for (PlaneRenderer aBodypiece : this.Bodypiece) {
            aBodypiece.render(this.scale);
        }
        this.bipedBody.postRender(scale);
        this.wings.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, this.scale);
    }

    protected void renderLegs() {
        if (!this.isSneak) {
            this.bipedBody.postRender(this.scale);
        }

        this.bipedLeftArm.render(this.scale);
        this.bipedRightArm.render(this.scale);
        this.bipedLeftLeg.render(this.scale);
        this.bipedRightLeg.render(this.scale);
        
        if (this.textureHeight == 64) {
            this.bipedLeftArmwear.render(this.scale);
            this.bipedRightArmwear.render(this.scale);
            this.bipedLeftLegwear.render(this.scale);
            this.bipedRightLegwear.render(this.scale);
        }
    }

    @Override
    protected void initTextures() {
        this.boxList.clear();
        this.Bodypiece = new PlaneRenderer[14];
        this.initHeadTextures();
        this.initBodyTextures();
        this.initLegTextures();
        this.Tail = new PonyTail(this);
    }

    protected void initHeadTextures() {
        this.bipedCape = new ModelRenderer(this, 0, 0).setTextureSize(64, 32);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
    }

    protected void initBodyTextures() {
        this.bipedBody = new ModelRenderer(this, 16, 16);
        
        if (this.textureHeight == 64) {
            this.bipedBodyWear = new ModelRenderer(this, 16, 32);
        }
        
        this.Bodypiece[0] = new PlaneRenderer(this, 24, 0);
        this.Bodypiece[0].mirrorz = true;
        
        this.Bodypiece[1] = new PlaneRenderer(this, 24, 0);
        
        this.Bodypiece[2] = new PlaneRenderer(this, 32, 20);
        this.Bodypiece[2].mirrorz = true;
        
        this.Bodypiece[3] = new PlaneRenderer(this, 56, 0);
        
        this.Bodypiece[4] = new PlaneRenderer(this, 4, 0);
        this.Bodypiece[4].mirrorz = true;
        
        this.Bodypiece[5] = new PlaneRenderer(this, 4, 0);
        
        this.Bodypiece[6] = new PlaneRenderer(this, 36, 16);
        this.Bodypiece[7] = new PlaneRenderer(this, 36, 16);
        this.Bodypiece[8] = new PlaneRenderer(this, 36, 16);
        
        this.Bodypiece[11] = new PlaneRenderer(this, 32, 0);
        this.Bodypiece[11].mirror = true;
        
        this.Bodypiece[9] = new PlaneRenderer(this, 32, 0);
        this.Bodypiece[10] = new PlaneRenderer(this, 32, 0);
        this.Bodypiece[12] = new PlaneRenderer(this, 32, 0);
        this.Bodypiece[13] = new PlaneRenderer(this, 32, 0);
        
        this.BodypieceNeck = new PlaneRenderer(this, 0, 16);

    }

    protected void initLegTextures() {
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);

        this.bipedLeftArm = new ModelRenderer(this, 32, 48);
        this.bipedLeftLeg = new ModelRenderer(this, 16, 48);

        this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
        this.bipedRightLegwear = new ModelRenderer(this, 0, 32);

        this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
        this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);

        this.unicornArmRight = new ModelRenderer(this, 40, 32).setTextureSize(64, 64);
        this.unicornArmLeft = new ModelRenderer(this, 40, 32).setTextureSize(64, 64);

        this.boxList.remove(this.steveRightArm);
        this.boxList.remove(this.unicornArmRight);
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        this.initHeadPositions(yOffset, stretch);
        this.initBodyPositions(yOffset, stretch);
        this.initLegPositions(yOffset, stretch);
        this.Tail.init(yOffset, stretch);
    }

    protected void initHeadPositions(float yOffset, float stretch) {
        this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, stretch);
        this.bipedHead.addBox(-4.0F + HEAD_CENTRE_X, -4 + HEAD_CENTRE_Y, -4.0F + HEAD_CENTRE_Z, 8, 8, 8, stretch);
        this.bipedHead.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2);
        // set ears
        this.bipedHead.setTextureOffset(12, 16);
        this.bipedHead.addBox(-4.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);
        this.bipedHead.mirror = true;
        this.bipedHead.addBox(2.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);

        this.bipedHeadwear.addBox(-4.0F + HEAD_CENTRE_X, -4.0F + HEAD_CENTRE_Y, -4.0F + HEAD_CENTRE_Z, 8, 8, 8, stretch + 0.5F);
        this.bipedHeadwear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2);
    }

    protected void initBodyPositions(float yOffset, float stretch) {
        this.bipedBody.addBox(-4.0F, 4.0F, -2.0F, 8, 8, 4, stretch);
        this.bipedBody.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.bipedBodyWear.addBox(-4.0F, 4.0F, -2.0F, 8, 8, 4, stretch + 0.25F);
        this.bipedBodyWear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        this.Bodypiece[0].addWestPlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, -4.0F + BODY_CENTRE_Z, 8, 8, stretch);
        this.Bodypiece[1].addEastPlane(4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, -4.0F + BODY_CENTRE_Z, 8, 8, stretch);
        this.Bodypiece[2].addTopPlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, -4.0F + BODY_CENTRE_Z, 8, 12, stretch);
        this.Bodypiece[3].addBottomPlane(-4.0F + BODY_CENTRE_X, 4.0F + BODY_CENTRE_Y, -4.0F + BODY_CENTRE_Z, 8, 8, stretch);
        this.Bodypiece[4].addWestPlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, 4.0F + BODY_CENTRE_Z, 8, 4, stretch);
        this.Bodypiece[5].addEastPlane(4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, 4.0F + BODY_CENTRE_Z, 8, 4, stretch);
        this.Bodypiece[6].addBackPlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, 8.0F + BODY_CENTRE_Z, 8, 4, stretch);
        this.Bodypiece[7].addBackPlane(-4.0F + BODY_CENTRE_X, 0.0F + BODY_CENTRE_Y, 8.0F + BODY_CENTRE_Z, 8, 4, stretch);
        this.Bodypiece[8].addBottomPlane(-4.0F + BODY_CENTRE_X, 4.0F + BODY_CENTRE_Y, 4.0F + BODY_CENTRE_Z, 8, 4, stretch);
        this.Bodypiece[9].addTopPlane(-1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z, 2, 6, stretch);
        this.Bodypiece[10].addBottomPlane(-1.0F + BODY_CENTRE_X, 4.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z, 2, 6, stretch);
        this.Bodypiece[11].addWestPlane(-1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z, 2, 6, stretch);
        this.Bodypiece[12].addEastPlane(1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z, 2, 6, stretch);
        this.Bodypiece[13].addBackPlane(-1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 8.0F + BODY_CENTRE_Z, 2, 2, stretch);
        
        for (int i = 0; i < this.Bodypiece.length; i++) {
            this.Bodypiece[i].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        }

        this.BodypieceNeck.addBackPlane(-2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y, -8.8F + BODY_CENTRE_Z, 4, 4, stretch);
        this.BodypieceNeck.addBackPlane(-2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y, -4.8F + BODY_CENTRE_Z, 4, 4, stretch);
        this.BodypieceNeck.addEastPlane(2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y, -8.8F + BODY_CENTRE_Z, 4, 4, stretch);
        this.BodypieceNeck.addWestPlane(-2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y, -8.8F + BODY_CENTRE_Z, 4, 4, stretch);
        
        this.BodypieceNeck.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck.rotateAngleX = NECK_ROT_X;
    }

    protected void initLegPositions(float yOffset, float stretch) {
        int armWidth = this.smallArms ? 3 : 4;
        float armY = this.smallArms ? 8.5f : 8f;
        float armX = this.smallArms ? -2f : -3f;
        
        this.bipedRightArm.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, armWidth, 12, 4, stretch);
        this.bipedLeftArm .addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, armWidth, 12, 4, stretch);
        
        this.bipedRightArm.setRotationPoint(armX, yOffset + armY, 0.0F);
        this.bipedLeftArm .setRotationPoint(3.0F, yOffset + armY, 0.0F);
        
        this.bipedRightLeg.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch);
        this.bipedLeftLeg .addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch);
        
        this.bipedRightLeg.setRotationPoint(-3.0F, 0.0F + yOffset, 0.0F);

        this.unicornArmRight.addBox(-2.0F + FIRSTP_ARM_CENTRE_X, -6.0F + FIRSTP_ARM_CENTRE_Y, -2.0F + FIRSTP_ARM_CENTRE_Z, 4, 12, 4, stretch + .25f);
        this.unicornArmLeft .addBox(-2.0F + FIRSTP_ARM_CENTRE_X, -6.0F + FIRSTP_ARM_CENTRE_Y, -2.0F + FIRSTP_ARM_CENTRE_Z, 4, 12, 4, stretch + .25f);
        
        this.unicornArmRight.setRotationPoint(-5.0F, 2.0F + yOffset, 0.0F);
        this.unicornArmLeft .setRotationPoint(-5.0F, 2.0F + yOffset, 0.0F);
        
        if (bipedRightArmwear != null) {
            this.bipedRightArmwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, armWidth, 12, 4, stretch + 0.25f);
            this.bipedRightArmwear.setRotationPoint(-3.0F, yOffset + armY, 0.0F);
        }
        
        if (bipedLeftArmwear != null) {
            this.bipedLeftArmwear .addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 3, 12, 4, stretch + 0.25f);
            this.bipedLeftArmwear .setRotationPoint(3.0F, yOffset + armY, 0.0F);
        }
        
        if (bipedRightLegwear != null) {
            this.bipedRightLegwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch + 0.25f);
            this.bipedRightLegwear.setRotationPoint(-3.0F, 0.0F + yOffset, 0.0F);
        }

        if (this.bipedLeftLegwear != null) {
            this.bipedLeftLegwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch + 0.25f);
        }
    }

    @Override
    public void renderCape(float scale) {
        this.bipedCape.render(scale);
    }

}
