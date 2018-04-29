package com.minelittlepony.model;

import com.minelittlepony.model.armour.ModelPonyArmor;
import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.model.components.PonySnout;
import com.minelittlepony.model.components.PonyTail;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyData;
import com.minelittlepony.pony.data.PonySize;
import com.minelittlepony.render.AbstractPonyRenderer;
import com.minelittlepony.render.PonyRenderer;
import com.minelittlepony.render.plane.PlaneRenderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

import static net.minecraft.client.renderer.GlStateManager.*;
import static com.minelittlepony.model.PonyModelConstants.*;

/**
 * Foundation class for all types of ponies.
 */
public abstract class AbstractPonyModel extends ModelPlayer implements IModel {

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

    public PlaneRenderer upperTorso;
    public PlaneRenderer neck;

    public PonyTail tail;
    public PonySnout snout;

    public AbstractPonyModel(boolean arms) {
        super(0, arms);
    }

    @Override
    public PonyArmor createArmour() {
        return new PonyArmor(new ModelPonyArmor(), new ModelPonyArmor());
    }

    @Override
    public void setRotationAngles(float move, float swing, float age, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, age, headYaw, headPitch, scale, entity);

        float headRotateAngleY = isSleeping ? 1.4f : headYaw / 57.29578F;
        float headRotateAngleX = isSleeping ? 0.1f : headPitch / 57.29578F;

        headRotateAngleX = Math.min(headRotateAngleX, (float) (0.5f - Math.toRadians(motionPitch)));
        headRotateAngleX = Math.max(headRotateAngleX, (float) (-1.25f - Math.toRadians(motionPitch)));

        updateHeadRotation(headRotateAngleX, headRotateAngleY);

        float bodySwingRotation = 0;
        if (swingProgress > -9990.0F && !metadata.hasMagic()) {
            bodySwingRotation = MathHelper.sin(MathHelper.sqrt(swingProgress) * PI * 2) * 0.2F;
        }

        rotateLook(move, swing, bodySwingRotation, age);

        setLegs(move, swing, age, entity);
        holdItem(swing);
        swingItem(entity, swingProgress);

        if (isCrouching()) {
            adjustBody(BODY_ROTATE_ANGLE_X_SNEAK, BODY_RP_Y_SNEAK, BODY_RP_Z_SNEAK);
            sneakLegs();
            setHead(0, 6, -2);
        } else if (isRiding) {
            adjustBodyRiding();
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
            swingArms(age);
            setHead(0, 0, 0);
        }

        if (isSleeping) ponySleep();

        aimBow(leftArmPose, rightArmPose, age);
        fixSpecialRotationPoints(move);

        animateWears();

        if (snout != null) {
            snout.setGender(metadata.getGender());
        }
    }

    protected void adjustBodyRiding() {
        adjustBodyComponents(BODY_ROTATE_ANGLE_X_RIDING, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
        adjustNeck(BODY_ROTATE_ANGLE_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);
        setHead(0, 0, 0);
    }

    protected void rotateLook(float move, float swing, float bodySwing, float ticks) {
        tail.setRotationAndAngles(rainboom, move, swing, bodySwing, ticks);
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
     * Sets the head rotation angle.
     */
    protected void setHead(float posX, float posY, float posZ) {
        bipedHead.setRotationPoint(posX, posY, posZ);
        bipedHeadwear.setRotationPoint(posX, posY, posZ);
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
        if (isFlying(entity)) {
            rotateLegsInFlight(move, swing, tick, entity);
        } else {
            rotateLegsOnGround(move, swing, tick, entity);
        }

        bipedLeftArm.rotateAngleZ = 0;
        bipedRightArm.rotateAngleZ = 0;

        adjustLegs(move, swing, tick);
    }

    protected void rotateLegsInFlight(float move, float swing, float tick, Entity entity) {
        float armX = MathHelper.sin(-swing / 2);
        float legX = MathHelper.sin(swing / 2);

        bipedLeftArm.rotateAngleX = armX;
        bipedRightArm.rotateAngleX = armX;

        bipedLeftLeg.rotateAngleX = legX;
        bipedRightLeg.rotateAngleX = legX;

        bipedLeftArm.rotateAngleY = -0.2F;
        bipedLeftLeg.rotateAngleY = 0.2F;

        bipedRightArm.rotateAngleY = 0.2F;
        bipedRightLeg.rotateAngleY = -0.2F;
    }

    protected void rotateLegsOnGround(float move, float swing, float tick, Entity entity) {
        float pi = PI * (float) Math.pow(swing, 16);

        float mve = move * 0.6662F; // magic number ahoy
        float srt = swing / 4;

        float leftArm = MathHelper.cos(mve + pi) * srt;
        float rightArm = MathHelper.cos(mve + PI + pi / 2) * srt;

        float leftLeg = MathHelper.cos(mve + PI - (pi * 0.4f)) * srt;
        float rightLeg = MathHelper.cos(mve + pi * 0.2f) * srt;

        bipedLeftArm.rotateAngleX = leftArm;
        bipedRightArm.rotateAngleX = rightArm;

        bipedLeftLeg.rotateAngleX = leftLeg;
        bipedRightLeg.rotateAngleX = rightLeg;

        bipedLeftArm.rotateAngleY = 0;
        bipedRightArm.rotateAngleY = 0;

        bipedLeftLeg.rotateAngleY = 0;
        bipedRightLeg.rotateAngleY = 0;
    }

    protected float getLegOutset() {
        if (isSleeping) return 2.6f;
        if (isCrouching()) return 0;
        return 4;
    }

    protected float getLegSpread() {
        return rainboom ? 2 : 1;
    }

    protected void adjustLegs(float move, float swing, float tick) {
        float sin = MathHelper.sin(bipedBody.rotateAngleY) * 5;
        float cos = MathHelper.cos(bipedBody.rotateAngleY) * 5;

        float spread = getLegSpread();

        bipedRightArm.rotationPointZ = spread + sin;
        bipedLeftArm.rotationPointZ = spread - sin;

        float legRPX = cos - getLegOutset();

        bipedRightArm.rotationPointX = -legRPX;
        bipedRightLeg.rotationPointX = -legRPX;

        bipedLeftArm.rotationPointX = legRPX;
        bipedLeftLeg.rotationPointX = legRPX;

        bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
        bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;

        bipedRightArm.rotationPointY = bipedLeftArm.rotationPointY = 8;
        bipedRightLeg.rotationPointZ = bipedLeftLeg.rotationPointZ = 10;
    }

    protected void holdItem(float swing) {
        boolean both = leftArmPose == ArmPose.ITEM && rightArmPose == ArmPose.ITEM;

        alignArmForAction(bipedLeftArm, leftArmPose, both, swing);
        alignArmForAction(bipedRightArm, rightArmPose, both, swing);
    }

    protected void alignArmForAction(ModelRenderer arm, ArmPose pose, boolean both, float swing) {
        switch (pose) {
            case ITEM:
                float swag = 1;
                if (!isFlying && both) {
                    swag -= (float)Math.pow(swing, 2);
                }
                float mult = 1 - swag/2;
                arm.rotateAngleX = bipedLeftArm.rotateAngleX * mult - (PI / 10) * swag;
            case EMPTY:
                arm.rotateAngleY = 0;
                break;
            case BLOCK:
                arm.rotateAngleX = arm.rotateAngleX / 2 - 0.9424779F;
                arm.rotateAngleY = PI / 6;
                break;
            default:
        }
    }

    protected void swingItem(Entity entity, float swingProgress) {
        if (swingProgress > -9990.0F && !isSleeping) {
            EnumHandSide mainSide = getMainHand(entity);

            if (getArmPoseForSide(mainSide) == ArmPose.EMPTY) return;

            swingArm(getArmForSide(mainSide));
        }
    }

    protected void swingArm(ModelRenderer arm) {
        float swing = 1 - (float)Math.pow(1 - swingProgress, 3);

        float deltaX = MathHelper.sin(swing * PI);
        float deltaZ = MathHelper.sin(swingProgress * PI);

        float deltaAim = deltaZ * (0.7F - bipedHead.rotateAngleX) * 0.75F;

        arm.rotateAngleX -= deltaAim + deltaX * 1.2F;
        arm.rotateAngleY += bipedBody.rotateAngleY * 2;
        arm.rotateAngleZ = -deltaZ * 0.4F;
    }

    protected void swingArms(float tick) {
        if (isSleeping) return;

        float cos = MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(tick * 0.067F) * 0.05F;

        if (rightArmPose != ArmPose.EMPTY) {
            bipedRightArm.rotateAngleZ += cos;
            bipedRightArm.rotateAngleX += sin;
        }

        if (leftArmPose != ArmPose.EMPTY) {
            bipedLeftArm.rotateAngleZ += cos;
            bipedLeftArm.rotateAngleX += sin;
        }
    }

    protected void adjustBody(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);
        adjustNeck(rotateAngleX, rotationPointY, rotationPointZ);
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

        AbstractPonyRenderer.shiftRotationPoint(bipedRightArm, 0, 2, 6);
        AbstractPonyRenderer.shiftRotationPoint(bipedLeftArm, 0, 2, 6);
        AbstractPonyRenderer.shiftRotationPoint(bipedRightLeg, 0, 2, -8);
        AbstractPonyRenderer.shiftRotationPoint(bipedLeftLeg, 0, 2, -8);
    }

    protected void aimBow(ArmPose leftArm, ArmPose rightArm, float tick) {
        if (rightArm == ArmPose.BOW_AND_ARROW) aimBowPony(bipedRightArm, tick, false);
        if (leftArm == ArmPose.BOW_AND_ARROW) aimBowPony(bipedLeftArm, tick, false);
    }

    protected void aimBowPony(ModelRenderer arm, float tick, boolean shift) {
        arm.rotateAngleZ = 0;
        arm.rotateAngleY = bipedHead.rotateAngleY - 0.06F;
        arm.rotateAngleX = ROTATE_270 + bipedHead.rotateAngleX;
        arm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        arm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.05F;
        if (shift) AbstractPonyRenderer.shiftRotationPoint(arm, 0, 0, 1);
    }

    protected void fixSpecialRotationPoints(float move) {
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
     * Loads texture values.
     */
    protected void initTextures() {
        boxList.clear();
        initHeadTextures();
        initBodyTextures();
        initLegTextures();
        initTailTextures();
    }

    /**
     * Loads texture positions and boxes. Pretty much just finishes the job of initTextures.
     */
    protected void initPositions(float yOffset, float stretch) {
        initHeadPositions(yOffset, stretch);
        initBodyPositions(yOffset, stretch);
        initLegPositions(yOffset, stretch);
        initTailPositions(yOffset, stretch);
    }

    protected void initTailTextures() {
        tail = new PonyTail(this);
    }

    protected void initHeadTextures() {
        bipedHead = new PonyRenderer(this, 0, 0);
        bipedHeadwear = new PonyRenderer(this, 32, 0);
        snout = new PonySnout(this);
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
        bipedLeftArm = new ModelRenderer(this, 32, 48);
        bipedRightArm = new ModelRenderer(this, 40, 16);

        bipedLeftArmwear = new ModelRenderer(this, 48, 48);
        bipedRightArmwear = new ModelRenderer(this, 40, 32);

        bipedLeftLeg = new ModelRenderer(this, 16, 48);
        bipedRightLeg = new ModelRenderer(this, 0, 16);

        bipedLeftLegwear = new ModelRenderer(this, 0, 48);
        bipedRightLegwear = new ModelRenderer(this, 0, 32);
    }

    protected void initTailPositions(float yOffset, float stretch) {
        tail.init(yOffset, stretch);
    }

    protected void initHeadPositions(float yOffset, float stretch) {
        snout.init(yOffset, stretch);
        ((PonyRenderer)bipedHead).offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                 .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                 .box(-4, -4, -4, 8, 8, 8, stretch)
                                 .tex(12, 16)
                                 .box(-4, -6, 1, 2, 2, 2, stretch)
                                 .flipX()
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

    protected int getArmWidth() {
        return 4;
    }

    protected int getArmDepth() {
        return 4;
    }

    protected float getLegRotationX() {
        return 3;
    }

    protected float getArmRotationY() {
        return 8;
    }

    protected void initLegPositions(float yOffset, float stretch) {
        int armWidth = getArmWidth();
        int armDepth = getArmDepth();

        float rarmX = getLegRotationX();
        float rarmY = getArmRotationY();

        float armX = THIRDP_ARM_CENTRE_X;
        float armY = THIRDP_ARM_CENTRE_Y - 6;
        float armZ = BODY_CENTRE_Z / 2 - 1 - armDepth;

        bipedLeftArm .addBox(armX, armY, armZ, armWidth, 12, armDepth, stretch);
        bipedRightArm.addBox(armX - armWidth, armY, armZ, armWidth, 12, armDepth, stretch);

        bipedLeftLeg .addBox(armX, armY, armZ, armWidth, 12, armDepth, stretch);
        bipedRightLeg.addBox(armX - armWidth, armY, armZ, armWidth, 12, armDepth, stretch);

        bipedLeftArm .setRotationPoint( rarmX, yOffset + rarmY, 0);
        bipedRightArm.setRotationPoint(-rarmX, yOffset + rarmY, 0);

        bipedLeftLeg .setRotationPoint( rarmX, yOffset, 0);
        bipedRightLeg.setRotationPoint(-rarmX, yOffset, 0);

        if (bipedLeftArmwear != null) {
            bipedLeftArmwear.addBox(armX, armY, armZ, armWidth, 12, armDepth, stretch + 0.25f);
            bipedLeftArmwear.setRotationPoint(rarmX, yOffset + rarmY, 0);
        }

        if (bipedRightArmwear != null) {
            bipedRightArmwear.addBox(armX - armWidth, armY, armZ, armWidth, 12, armDepth, stretch + 0.25f);
            bipedRightArmwear.setRotationPoint(-rarmX, yOffset + rarmY, 0);
        }

        if (bipedLeftLegwear != null) {
            bipedLeftLegwear.addBox(armX, armY, armZ, armWidth, 12, armDepth, stretch + 0.25f);
            bipedRightLegwear.setRotationPoint(rarmX, yOffset, 0);
        }

        if (bipedRightLegwear != null) {
            bipedRightLegwear.addBox(armX - armWidth, armY, armZ, armWidth, 12, armDepth, stretch + 0.25f);
            bipedRightLegwear.setRotationPoint(-rarmX, yOffset, 0);
        }
    }

    public ArmPose getArmPoseForSide(EnumHandSide side) {
        return side == EnumHandSide.RIGHT ? rightArmPose : leftArmPose;
    }

    @Override
    public boolean isCrouching() {
        return isSneak && !isFlying;
    }

    /**
     * Checks flying and speed conditions and sets rainboom to true if we're a species with wings and is going faaast.
     */
    protected void checkRainboom(Entity entity, float swing) {
        rainboom = isFlying(entity) && swing >= 0.9999F;
    }

    @Override
    public boolean isFlying(Entity entity) {
        return (isFlying && metadata.getRace().hasWings()) ||
                (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isElytraFlying());
    }

    @Override
    public boolean isFlying() {
        return isFlying;
    }

    @Override
    public boolean isChild() {
        return metadata.getSize() == PonySize.FOAL || isChild;
    }

    @Override
    public float getSwingAmount() {
        return swingProgress;
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
        float swing = MathHelper.sin(swingProgress * PI);
        float roll = MathHelper.sin((1 - (1 - swingProgress) * (1 - swingProgress)) * PI);

        float cos = MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(tick * 0.067F) / 10;

        arm.rotateAngleX = -1.5707964F;
        arm.rotateAngleX -= swing * 1.2F - roll * 0.4F;
        arm.rotateAngleX += sin;

        arm.rotateAngleY = direction * (0.1F - swing * 0.6F);
        arm.rotateAngleZ = cos;

    }

    @Override
    public void render(Entity entityIn, float move, float swing, float age, float headYaw, float headPitch, float scale) {

        pushMatrix();
        transform(BodyPart.HEAD);
        renderHead(entityIn, move, swing, age, headYaw, headPitch, scale);
        popMatrix();

        pushMatrix();
        transform(BodyPart.NECK);
        renderNeck();
        popMatrix();

        pushMatrix();
        transform(BodyPart.BODY);
        renderBody(entityIn, move, swing, age, headYaw, headPitch, scale);
        popMatrix();

        pushMatrix();
        transform(BodyPart.LEGS);
        renderLegs();
        popMatrix();
    }

    protected void renderHead(Entity entity, float move, float swing, float age, float headYaw, float headPitch, float scale) {
        bipedHead.render(scale);
        bipedHeadwear.render(scale);
        bipedHead.postRender(scale);
    }

    protected void renderNeck() {
        GlStateManager.scale(0.9, 0.9, 0.9);
        neck.render(scale);
    }

    protected void renderBody(Entity entity, float move, float swing, float age, float headYaw, float headPitch, float scale) {
        bipedBody.render(scale);
        if (textureHeight == 64) {
            bipedBodyWear.render(scale);
        }
        upperTorso.render(scale);
        bipedBody.postRender(scale);
        tail.render(scale);
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
        if (isSleeping) translate(0, -0.7F, 0.2F);

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
        if (isCrouching()) translate(0, -0.12F, 0);
        if (isSleeping) translate(0, -1.2F, 0.25F);
        if (isRiding) translate(0, -0.1F, 0);

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
        int randomI = rand.nextInt(boxList.size());
        int index = randomI;

        ModelRenderer result;
        do {
            result = boxList.get(randomI);
            if (!result.cubeList.isEmpty()) return result;

            index = (index + 1) % boxList.size();
        } while (index != randomI);

        return result;
    }
}
