package com.minelittlepony.model.ponies;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.ModelPonyArmor;
import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.model.components.PegasusWings;
import com.minelittlepony.model.components.PonySnout;
import com.minelittlepony.model.components.PonyTail;
import com.minelittlepony.model.components.UnicornHorn;
import com.minelittlepony.render.PonyRenderer;
import com.minelittlepony.render.plane.PlaneRenderer;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static com.minelittlepony.model.PonyModelConstants.*;

public class ModelPlayerPony extends AbstractPonyModel {

    private final boolean smallArms;
    
    public ModelRenderer bipedCape;
    
    public PlaneRenderer upperTorso;
    public PlaneRenderer neck;
    
    public PonyRenderer unicornArmRight, unicornArmLeft;
    
    public PonyTail tail;
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

        setLegs(limbSwing, limbSwingAmount, ageInTicks, entityIn);
        holdItem(limbSwingAmount);
        swingItem(entityIn, swingProgress);

        if (isCrouching() && !rainboom) {
            adjustBody(BODY_ROTATE_ANGLE_X_SNEAK, BODY_RP_Y_SNEAK, BODY_RP_Z_SNEAK);
            sneakLegs();
            setHead(0, 6, -2);
        } else if (isRiding) {
            this.adjustBodyRiding();
            bipedLeftLeg.rotationPointZ = 15;
            bipedLeftLeg.rotationPointY = 10;
            bipedLeftLeg.rotateAngleX = -PI / 4;
            bipedLeftLeg.rotateAngleY = -PI / 5;

            bipedRightLeg.rotationPointZ = 15;
            bipedRightLeg.rotationPointY = 10;
            bipedRightLeg.rotateAngleX = -PI / 4;
            bipedRightLeg.rotateAngleY =  PI / 5;

            bipedLeftArm.rotateAngleZ = -PI * 0.06f;
            bipedRightArm.rotateAngleZ = PI * 0.06f;
        } else {
            adjustBody(BODY_ROTATE_ANGLE_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);

            bipedRightLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            bipedLeftLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            swingArms(ageInTicks);
            setHead(0, 0, 0);
        }
        
        if (isSleeping) ponySleep();

        aimBow(leftArmPose, rightArmPose, ageInTicks);
        fixSpecialRotationPoints(limbSwing);

        animateWears();

        if (bipedCape != null) {
            bipedCape.rotationPointY = isSneak ? 2 : isRiding ? -4 : 0;
    
            snout.setGender(metadata.getGender());
            wings.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        }
    }
    
    protected void adjustBodyRiding() {
        adjustBodyComponents(BODY_ROTATE_ANGLE_X_RIDING, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
        adjustNeck(BODY_ROTATE_ANGLE_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);
        setHead(0, 0, 0);
    }
    
    protected void rotateLook(float limbSwing, float limbSwingAmount, float bodySwing, float ticks) {
        tail.setRotationAndAngles(rainboom, limbSwing, limbSwingAmount, bodySwing, ticks);
        bodySwing /= 5;
        
        upperTorso.rotateAngleY = bodySwing;
        bipedBody.rotateAngleY = bodySwing;
        neck.rotateAngleY = bodySwing;
    }

    private void animateWears() {
        copyModelAngles(bipedLeftArm, bipedLeftArmwear);
        copyModelAngles(bipedRightArm, bipedRightArmwear);
        copyModelAngles(bipedLeftLeg, bipedLeftLegwear);
        copyModelAngles(bipedRightLeg, bipedRightLegwear);
        copyModelAngles(bipedBody, bipedBodyWear);
    }

    /**
     * Checks flying and speed conditions and sets rainboom to true if we're a species with wings and is going faaast.
     */
    protected void checkRainboom(Entity entity, float swing) {
        rainboom = isFlying(entity) && swing >= 0.9999F;
    }

    /**
     * Sets the head rotation angle.
     */
    protected void setHead(float posX, float posY, float posZ) {
        bipedHead.setRotationPoint(posX, posY, posZ);
        bipedHeadwear.setRotationPoint(posX, posY, posZ);
    }

    /**
     * Rotates the head within reason. X is clamped to around motionPitch.
     * Both arguments are also ignored when sleeping.
     */
    private void rotateHead(float horz, float vert) {
        float headRotateAngleY = isSleeping ? 1.4f : horz / 57.29578F;
        float headRotateAngleX = isSleeping ? 0.1f : vert / 57.29578F;
        
        headRotateAngleX = Math.min(headRotateAngleX, (float) (0.5f - Math.toRadians(motionPitch)));
        headRotateAngleX = Math.max(headRotateAngleX, (float) (-1.25f - Math.toRadians(motionPitch)));
        
        updateHeadRotation(headRotateAngleX, headRotateAngleY);
    }
    
    /**
     * Called to update the head rotation.
     * 
     * @param x New rotation X
     * @param y New rotation Y
     */
    protected void updateHeadRotation(float x, float y) {
        bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY = y;
        bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX = x;
    }

    protected void setLegs(float move, float swing, float tick, Entity entity) {
        rotateLegs(move, swing, tick, entity);
        adjustLegs();
    }
    
    protected void rotateLegs(float move, float swing, float tick, Entity entity) {
        float leftArm, rightArm, leftLeg, rightLeg;
        
        
        if (isFlying(entity)) {
            if (rainboom) {
                rightArm = leftArm = ROTATE_270;
                rightLeg = leftLeg = ROTATE_90;
            } else {
                rightArm = leftArm = MathHelper.sin(-swing / 2);
                rightLeg = leftLeg = MathHelper.sin(swing / 2);
            }

            bipedRightArm.rotateAngleY = 0.2F;
            bipedLeftArm.rotateAngleY = bipedRightLeg.rotateAngleY = -0.2F;
            
            bipedLeftLeg.rotateAngleY = 0.2F;

        } else {
            float pi = PI * (float) Math.pow(swing, 16);
            
            float mve = move * 0.6662F; // magic number ahoy
            float srt = swing / 4;
            
            leftArm = MathHelper.cos(mve + pi) * srt;
            rightArm = MathHelper.cos(mve + PI + pi / 2) * srt;
            
            leftLeg = MathHelper.cos(mve + PI - (pi * 0.4f)) * srt;
            rightLeg = MathHelper.cos(mve + pi * 0.2f) * srt;

            bipedLeftArm.rotateAngleY = 0;
            bipedRightArm.rotateAngleY = 0;

            bipedLeftLeg.rotateAngleY = 0;
            bipedRightLeg.rotateAngleY = 0;

            unicornArmRight.rotateAngleY = 0;
            unicornArmLeft.rotateAngleY = 0;
        }

        bipedLeftArm.rotateAngleX = leftArm;
        bipedRightArm.rotateAngleX = rightArm;

        bipedLeftLeg.rotateAngleX = leftLeg;
        bipedRightLeg.rotateAngleX = rightLeg;

        bipedLeftArm.rotateAngleZ = 0;
        bipedRightArm.rotateAngleZ = 0;

        unicornArmLeft.rotateAngleZ = 0;
        unicornArmRight.rotateAngleZ = 0;

        unicornArmLeft.rotateAngleX = 0;
        unicornArmRight.rotateAngleX = 0;
    }
    
    private float getLegOutset() {
        if (isSleeping) return 2.6f;
        if (isSneak && !isFlying) return smallArms ? 1 : 0;
        return 4;
    }

    protected void adjustLegs() {
        float sin = MathHelper.sin(bipedBody.rotateAngleY) * 5;
        float cos = MathHelper.cos(bipedBody.rotateAngleY) * 5;

        float spread = rainboom ? 2 : 1;

        bipedRightArm.rotationPointZ = spread + sin;

        bipedLeftArm.rotationPointZ = spread - sin;

        float legOutset = getLegOutset();
        float rpxl = cos + 1 - legOutset;
        float rpxr = legOutset - cos - 1;

        bipedRightArm.rotationPointX = rpxr;
        bipedRightLeg.rotationPointX = rpxr;
        bipedLeftArm.rotationPointX = rpxl;
        bipedLeftLeg.rotationPointX = rpxl;

        // Push the front legs back apart if we're a thin pony
        if (smallArms) {
            bipedLeftArm.rotationPointX--;
            bipedLeftArm.rotationPointX += 2;
        }

        bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
        bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;

        bipedRightArm.rotationPointY = bipedLeftArm.rotationPointY = 8;
        bipedRightLeg.rotationPointZ = bipedLeftLeg.rotationPointZ = 10;
    }

    
    
    protected void holdItem(float swing) {
        boolean both = leftArmPose == ArmPose.ITEM && rightArmPose == ArmPose.ITEM;

        if (!rainboom && !metadata.hasMagic()) {
            alignArmForAction(bipedLeftArm, leftArmPose, both, swing);
            alignArmForAction(bipedRightArm, rightArmPose, both, swing);
        } else if (metadata.hasMagic()) {
            alignArmForAction(unicornArmLeft, leftArmPose, both, swing);
            alignArmForAction(unicornArmRight, rightArmPose, both, swing);
        }

        horn.setUsingMagic(this.leftArmPose != ArmPose.EMPTY || this.rightArmPose != ArmPose.EMPTY);
    }

    private void alignArmForAction(ModelRenderer arm, ArmPose pose, boolean both, float swing) {
        switch (pose) {
            case ITEM:
                float swag = 1;
                if (!isFlying && both) {
                    swag -= (float)Math.pow(swing, 2);
                }
                float mult = 1 - swag/2f;
                arm.rotateAngleX = bipedLeftArm.rotateAngleX * mult - (PI / 10) * swag;
            case EMPTY:
                arm.rotateAngleY = 0;
                break;
            case BLOCK:
                blockArm(arm);
                break;
            default:
        }
    }
    
    private void blockArm(ModelRenderer arm) {
        arm.rotateAngleX = arm.rotateAngleX / 2 - 0.9424779F;
        arm.rotateAngleY = PI / 6;
    }

    protected void swingItem(Entity entity, float swingProgress) {
        if (swingProgress > -9990.0F && !this.isSleeping) {
            EnumHandSide mainSide = this.getMainHand(entity);
            boolean mainRight = mainSide == EnumHandSide.RIGHT;
            ArmPose mainPose = mainRight ? rightArmPose : leftArmPose;
            
            if (mainPose == ArmPose.EMPTY) return;
            
            float f16 = 1 - swingProgress;
            f16 *= f16 * f16;
            f16 = 1 - f16;
            float f22 = MathHelper.sin(f16 * PI);
            float f28 = MathHelper.sin(swingProgress * PI);
            float f33 = f28 * (0.7F - bipedHead.rotateAngleX) * 0.75F;
            
            if (metadata.hasMagic()) {
                swingArm(mainRight ? unicornArmRight : unicornArmLeft, f22, f33, f28);
            } else {
                swingArm(getArmForSide(mainSide), f22, f33, f28);
            }
        }
    }
    
    private void swingArm(ModelRenderer arm, float f22, float f33, float f28) {
        arm.rotateAngleX = (float) (arm.rotateAngleX - (f22 * 1.2D + f33));
        arm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
        arm.rotateAngleZ = f28 * -0.4F;
    }

    protected void swingArms(float tick) {
        float cos = MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(tick * 0.067F) * 0.05F;

        if (this.rightArmPose != ArmPose.EMPTY && !this.isSleeping) {
            
            if (this.metadata.hasMagic()) {
                this.unicornArmRight.rotateAngleZ += cos;
                this.unicornArmRight.rotateAngleX += sin;
            } else {
                this.bipedRightArm.rotateAngleZ += cos;
                this.bipedRightArm.rotateAngleX += sin;
            }
        }
        if (this.leftArmPose != ArmPose.EMPTY && !this.isSleeping) {
            if (this.metadata.hasMagic()) {
                this.unicornArmLeft.rotateAngleZ += cos;
                this.unicornArmLeft.rotateAngleX += sin;
            } else {
                this.bipedLeftArm.rotateAngleZ += cos;
                this.bipedLeftArm.rotateAngleX += sin;
            }
        }
    }

    protected void adjustBody(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        this.adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);
        this.adjustNeck(rotateAngleX, rotationPointY, rotationPointZ);
    }

    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        bipedBody.rotateAngleX = rotateAngleX;
        bipedBody.rotationPointY = rotationPointY;
        bipedBody.rotationPointZ = rotationPointZ;
        
        upperTorso.rotateAngleX = rotateAngleX;
        upperTorso.rotationPointY = rotationPointY;
        upperTorso.rotationPointZ = rotationPointZ;
    }

    protected void adjustNeck(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        neck.setRotationPoint(NECK_ROT_X + rotateAngleX, rotationPointY, rotationPointZ);
    }

    /**
     * Aligns legs to a sneaky position.
     */
    protected void sneakLegs() {
        unicornArmRight.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        unicornArmLeft.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;

        bipedRightArm.rotateAngleX -= SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        bipedLeftArm.rotateAngleX -= SNEAK_LEG_X_ROTATION_ADJUSTMENT;

        bipedLeftLeg.rotationPointY = bipedRightLeg.rotationPointY = FRONT_LEG_RP_Y_SNEAK;
    }

    protected void ponySleep() {
        bipedRightArm.rotateAngleX = ROTATE_270;
        bipedLeftArm.rotateAngleX = ROTATE_270;
        bipedRightLeg.rotateAngleX = ROTATE_90;
        bipedLeftLeg.rotateAngleX = ROTATE_90;
        
        setHead(1, 2, isSneak ? -1 : 1);
        
        shiftRotationPoint(bipedRightArm, 0, 2, 6);
        shiftRotationPoint(bipedLeftArm, 0, 2, 6);
        shiftRotationPoint(bipedRightLeg, 0, 2, -8);
        shiftRotationPoint(bipedLeftLeg, 0, 2, -8);
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
        arm.rotateAngleZ = 0;
        arm.rotateAngleY = bipedHead.rotateAngleY - 0.06F;
        arm.rotateAngleX = ROTATE_270 + bipedHead.rotateAngleX;
        arm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        arm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.05F;
        if (shift) shiftRotationPoint(arm, 0, 0, 1);
    }

    protected void fixSpecialRotationPoints(float move) {
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        
        pushMatrix();
        transform(BodyPart.HEAD);
        renderHead(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        popMatrix();

        pushMatrix();
        transform(BodyPart.NECK);
        renderNeck();
        popMatrix();

        pushMatrix();
        transform(BodyPart.BODY);
        renderBody(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        popMatrix();

        pushMatrix();
        transform(BodyPart.LEGS);
        renderLegs();
        popMatrix();
    }

    protected void renderHead(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        bipedHead.render(scale);
        bipedHeadwear.render(scale);
        bipedHead.postRender(scale);
        horn.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    protected void renderNeck() {
        GlStateManager.scale(0.9, 0.9, 0.9);
        neck.render(scale);
    }

    protected void renderBody(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        bipedBody.render(scale);
        if (textureHeight == 64) {
            bipedBodyWear.render(scale);
        }
        upperTorso.render(scale);
        bipedBody.postRender(scale);
        wings.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        tail.render(metadata.getTail(), scale);
    }

    protected void renderLegs() {
        if (!isSneak) bipedBody.postRender(scale);

        bipedLeftArm.render(scale);
        bipedRightArm.render(scale);
        bipedLeftLeg.render(scale);
        bipedRightLeg.render(scale);
        
        if (textureHeight == 64) {
            bipedLeftArmwear.render(scale);
            bipedRightArmwear.render(scale);
            bipedLeftLegwear.render(scale);
            bipedRightLegwear.render(scale);
        }
    }

    @Override
    protected void initTextures() {
        boxList.clear();
        initHeadTextures();
        initBodyTextures();
        initLegTextures();
        tail = new PonyTail(this);
    }

    protected void initHeadTextures() {
        bipedCape = new PonyRenderer(this, 0, 0).size(64, 32);
        bipedHead = new PonyRenderer(this, 0, 0);
        bipedHeadwear = new PonyRenderer(this, 32, 0);
    }

    protected void initBodyTextures() {
        bipedBody = new ModelRenderer(this, 16, 16);
        
        if (textureHeight == 64) {
            bipedBodyWear = new ModelRenderer(this, 16, 32);
        }
        
        upperTorso = new PlaneRenderer(this, 24, 0);
        neck = new PlaneRenderer(this, 0, 16);
    }

    protected void initLegTextures() {
        bipedRightArm = new ModelRenderer(this, 40, 16);
        bipedRightLeg = new ModelRenderer(this, 0, 16);

        bipedLeftArm = new ModelRenderer(this, 32, 48);
        bipedLeftLeg = new ModelRenderer(this, 16, 48);

        bipedRightArmwear = new ModelRenderer(this, 40, 32);
        bipedRightLegwear = new ModelRenderer(this, 0, 32);

        bipedLeftArmwear = new ModelRenderer(this, 48, 48);
        bipedLeftLegwear = new ModelRenderer(this, 0, 48);

        unicornArmRight = new PonyRenderer(this, 40, 32).size(64, 64);
        unicornArmLeft = new PonyRenderer(this, 40, 32).size(64, 64);

        boxList.remove(unicornArmRight);
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        initHeadPositions(yOffset, stretch);
        initBodyPositions(yOffset, stretch);
        initLegPositions(yOffset, stretch);
        initTailPositions(yOffset, stretch);
    }
    
    protected void initTailPositions(float yOffset, float stretch) {
        tail.init(yOffset, stretch);
    }

    protected void initHeadPositions(float yOffset, float stretch) {
        bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, stretch);
        
        ((PonyRenderer)bipedHead).offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                 .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                 .box(-4, -4, -4, 8, 8, 8, stretch)
                                 .tex(12, 16)
                                 .box(-4, -6, 1, 2, 2, 2, stretch)
                                 .mirror()
                                 .box(2, -6, 1, 2, 2, 2, stretch);
        
        ((PonyRenderer)bipedHeadwear).offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                     .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                     .box(-4, -4, -4, 8, 8, 8, stretch + 0.5F);
    }

    /**
     * Creates the main torso and neck.
     */
    protected void initBodyPositions(float yOffset, float stretch) {
        bipedBody.addBox(-4, 4, -2, 8, 8, 4, stretch);
        bipedBody.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        
        bipedBodyWear.addBox(-4, 4, -2, 8, 8, 4, stretch + 0.25F);
        bipedBodyWear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        
        upperTorso.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
                  .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                    .tex(24, 0)    .addEastPlane( 4, -4, -4, 8, 8, stretch)
                    .tex(56, 0)  .addBottomPlane(-4,  4, -4, 8, 8, stretch)
                    .tex(4,  0)    .addEastPlane( 4, -4,  4, 8, 4, stretch)
                    .tex(36, 16)   .addBackPlane(-4, -4,  8, 8, 4, stretch)
                                   .addBackPlane(-4,  0,  8, 8, 4, stretch)
                                 .addBottomPlane(-4,  4,  4, 8, 4, stretch)
                .flipZ().tex(24, 0).addWestPlane(-4, -4, -4, 8, 8, stretch)
                        .tex(32, 20).addTopPlane(-4, -4, -4, 8, 12, stretch)
                        .tex(4, 0) .addWestPlane(-4, -4,  4, 8, 4, stretch)
                // Tail stub
              .child(0)
                .tex(32, 0).addTopPlane(-1, 2, 2, 2, 6, stretch)
                        .addBottomPlane(-1, 4, 2, 2, 6, stretch)
                          .addEastPlane( 1, 2, 2, 2, 6, stretch)
                          .addBackPlane(-1, 2, 8, 2, 2, stretch)
                  .flipZ().addWestPlane(-1, 2, 2, 2, 6, stretch)
                  .rotateAngleX = 0.5F;
        
        neck.at(NECK_CENTRE_X, NECK_CENTRE_Y, NECK_CENTRE_Z)
            .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
            .addFrontPlane(0, 0, 0, 4, 4, stretch)
            .addBackPlane(0, 0, 4, 4, 4, stretch)
            .addEastPlane(4, 0, 0, 4, 4, stretch)
            .addWestPlane(0, 0, 0, 4, 4, stretch)
            .rotateAngleX = NECK_ROT_X;
    }

    protected void initLegPositions(float yOffset, float stretch) {
        int armWidth = smallArms ? 3 : 4;
        float rarmY = smallArms ? 8.5f : 8;
        float rarmX = smallArms ? 2 : 3;
        
        float armX = THIRDP_ARM_CENTRE_X - 2;
        float armY = THIRDP_ARM_CENTRE_Y - 6;
        float armZ = THIRDP_ARM_CENTRE_Z - 2;
        
        bipedLeftArm .addBox(armX, armY, armZ, armWidth, 12, 4, stretch);
        bipedRightArm.addBox(armX, armY, armZ, armWidth, 12, 4, stretch);
        
        bipedLeftLeg .addBox(armX, armY, armZ, 4, 12, 4, stretch);
        bipedRightLeg.addBox(armX, armY, armZ, 4, 12, 4, stretch);
        
        bipedLeftArm .setRotationPoint( rarmX, yOffset + rarmY, 0);
        bipedRightArm.setRotationPoint(-rarmX, yOffset + rarmY, 0);
        
        bipedLeftLeg .setRotationPoint( rarmX, yOffset, 0);
        bipedRightLeg.setRotationPoint(-rarmX, yOffset, 0);

        if (bipedLeftArmwear != null) {
            bipedLeftArmwear.addBox(armX, armY, armZ, 3, 12, 4, stretch + 0.25f);
            bipedLeftArmwear.setRotationPoint(3, yOffset + rarmY, 0);
        }
        
        if (bipedRightArmwear != null) {
            bipedRightArmwear.addBox(armX, armY, armZ, armWidth, 12, 4, stretch + 0.25f);
            bipedRightArmwear.setRotationPoint(-3, yOffset + rarmY, 0);
        }
        
        if (bipedLeftLegwear != null) {
            bipedLeftLegwear.addBox(armX, armY, armZ, 4, 12, 4, stretch + 0.25f);
            bipedRightLegwear.setRotationPoint(3, yOffset, 0);
        }
        
        if (bipedRightLegwear != null) {
            bipedRightLegwear.addBox(armX, armY, armZ, 4, 12, 4, stretch + 0.25f);
            bipedRightLegwear.setRotationPoint(-3, yOffset, 0);
        }

        unicornArmLeft .addBox(FIRSTP_ARM_CENTRE_X - 2, armY, armZ, 4, 12, 4, stretch + .25f);
        unicornArmRight.addBox(FIRSTP_ARM_CENTRE_X - 2, armY, armZ, 4, 12, 4, stretch + .25f);

        unicornArmLeft .setRotationPoint(5, yOffset + 2, 0);
        unicornArmRight.setRotationPoint(-5, yOffset + 2, 0);
    }

    @Override
    public void renderCape(float scale) {
        bipedCape.render(scale);
    }
}
