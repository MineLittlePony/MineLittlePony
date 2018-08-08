package com.minelittlepony.model;

import com.minelittlepony.model.armour.ModelPonyArmor;
import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.model.capabilities.IModelPart;
import com.minelittlepony.model.components.PonySnout;
import com.minelittlepony.model.components.PonyTail;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.Pony;
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

    private boolean isSleeping;
    private boolean isFlying;
    private boolean isElytraFlying;
    private boolean isSwimming;
    private boolean headGear;

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
    protected boolean rainboom;

    public PlaneRenderer upperTorso;
    public PlaneRenderer neck;

    public IModelPart tail;
    public PonySnout snout;

    public AbstractPonyModel(boolean arms) {
        super(0, arms);
    }

    @Override
    public PonyArmor createArmour() {
        return new PonyArmor(new ModelPonyArmor(), new ModelPonyArmor());
    }

    /**
     * Checks flying and speed conditions and sets rainboom to true if we're a species with wings and is going faaast.
     */
    protected void checkRainboom(Entity entity, float swing) {
        rainboom = canFly() || isElytraFlying();
        rainboom &= Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ) > 0.4F;
    }

    public void updateLivingState(EntityLivingBase entity, Pony pony) {
        isChild = entity.isChild();
        isSneak = entity.isSneaking();
        isSleeping = entity.isPlayerSleeping();
        isFlying = pony.isPegasusFlying(entity);
        isElytraFlying = entity.isElytraFlying();
        isSwimming = pony.isSwimming(entity);
        headGear = pony.isWearingHeadgear(entity);
    }

    /**
     * Sets the model's various rotation angles.
     *
     * @param move      Entity motion parameter - i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param swing     Degree to which each 'limb' swings.
     * @param ticks     Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     * @param headYaw   Horizontal head motion in radians.
     * @param headPitch Vertical head motion in radians.
     * @param scale     Scaling factor used to render this model. Determined by the return value of {@link RenderLivingBase.prepareScale}. Usually {@code 0.0625F}.
     * @param entity    The entity we're being called for.
     */
    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        checkRainboom(entity, swing);

        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        float headRotateAngleY = isSleeping ? 1.4f : headYaw / 57.29578F;
        float headRotateAngleX = isSleeping ? 0.1f : headPitch / 57.29578F;

        headRotateAngleX = Math.min(headRotateAngleX, (float) (0.5f - Math.toRadians(motionPitch)));
        headRotateAngleX = Math.max(headRotateAngleX, (float) (-1.25f - Math.toRadians(motionPitch)));

        updateHeadRotation(headRotateAngleX, headRotateAngleY);

        shakeBody(move, swing, getWobbleAmount(), ticks);
        rotateLegs(move, swing, ticks, entity);

        if (!isSwimming && !rainboom) {
            holdItem(swing);
        }
        swingItem(entity);

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
            swingArms(ticks);
            setHead(0, 0, 0);
        }

        if (isSleeping) ponySleep();

        animateWears();

        snout.setGender(metadata.getGender());
    }

    protected float getWobbleAmount() {

        if (swingProgress <= 0) {
            return 0;
        }

        return MathHelper.sin(MathHelper.sqrt(swingProgress) * PI * 2) * 0.04F;
    }

    protected void adjustBodyRiding() {
        adjustBodyComponents(BODY_ROTATE_ANGLE_X_RIDING, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
        adjustNeck(BODY_ROTATE_ANGLE_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);
        setHead(0, 0, 0);
    }

    /**
     * Sets the model's various rotation angles.
     *
     * @param move      Entity motion parameter - i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param swing     Degree to which each 'limb' swings.
     * @param bodySwing Horizontal (Y) body rotation.
     * @param ticks       Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     */
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        tail.setRotationAndAngles(isSwimming || rainboom, move, swing, bodySwing * 5, ticks);

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

    @Override
    public ModelRenderer getHead() {
        return bipedHead;
    }

    /**
     * Sets the head rotation point.
     */
    protected void setHead(float posX, float posY, float posZ) {
        bipedHead.setRotationPoint(posX, posY, posZ);
        bipedHeadwear.setRotationPoint(posX, posY, posZ);
    }

    /**
     * Called to update the head rotation.
     *
     * @param x     New rotation X
     * @param y     New rotation Y
     */
    protected void updateHeadRotation(float x, float y) {
        bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY = y;
        bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX = x;
    }

    /**
    *
    * Used to set the legs rotation based on walking/crouching animations.
    *
    * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
    *
    */
    protected void rotateLegs(float move, float swing, float ticks, Entity entity) {
        if (isSwimming()) {
            rotateLegsSwimming(move, swing, ticks, entity);
        } else if (isGoingFast()) {
            rotateLegsInFlight(move, swing, ticks, entity);
        } else {
            rotateLegsOnGround(move, swing, ticks, entity);
        }

        bipedRightArm.rotateAngleZ = 0;
        bipedLeftArm.rotateAngleZ = 0;

        float sin = MathHelper.sin(bipedBody.rotateAngleY) * 5;
        float cos = MathHelper.cos(bipedBody.rotateAngleY) * 5;

        float spread = getLegSpread();

        bipedRightArm.rotationPointZ = spread + sin;
        bipedLeftArm.rotationPointZ = spread - sin;

        float legRPX = cos - getLegOutset();

        legRPX = metadata.getInterpolator().interpolate("legOffset", legRPX, 3);

        bipedRightArm.rotationPointX = -legRPX;
        bipedRightLeg.rotationPointX = -legRPX;

        bipedLeftArm.rotationPointX = legRPX;
        bipedLeftLeg.rotationPointX = legRPX;

        bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
        bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;

        bipedRightArm.rotationPointY = bipedLeftArm.rotationPointY = 8;
        bipedRightLeg.rotationPointZ = bipedLeftLeg.rotationPointZ = 10;
    }

    /**
     * Rotates legs in quopy fashion whilst swimming.
     *
     * @param move      Entity motion parameter - i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param swing     Degree to which each 'limb' swings.
     * @param ticks     Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     * @param entity    The entity we're being called for.
     *
     */
    protected void rotateLegsSwimming(float move, float swing, float ticks, Entity entity) {

        float forward = ROTATE_270 - ROTATE_90/3;
        float down = ROTATE_90;

        float leftX = down + MathHelper.sin((move / 3) + 2*PI/3) / 2;
        float leftY = -forward - MathHelper.sin((move / 3) + 2*PI/3);

        float rightX = down + MathHelper.sin(move / 3) / 2;


        bipedLeftArm.rotateAngleX = leftX;
        bipedLeftArm.rotateAngleY = leftY;

        bipedRightArm.rotateAngleY = -leftY;
        bipedRightArm.rotateAngleX = leftX;

        bipedLeftLeg.rotateAngleX = leftX;
        bipedRightLeg.rotateAngleX = rightX;

        bipedLeftLeg.rotateAngleY = 0;
        bipedRightLeg.rotateAngleY = 0;
    }

    /**
     * Rotates legs in quopy fashion whilst flying.
     *
     * @param move      Entity motion parameter - i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param swing     Degree to which each 'limb' swings.
     * @param ticks     Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     * @param entity    The entity we're being called for.
     *
     */
    protected void rotateLegsInFlight(float move, float swing, float ticks, Entity entity) {
        float armX = rainboom ? ROTATE_270 : MathHelper.sin(-swing / 2);
        float legX = rainboom ? ROTATE_90 : MathHelper.sin(swing / 2);

        bipedLeftArm.rotateAngleX = armX;
        bipedRightArm.rotateAngleX = armX;

        bipedLeftLeg.rotateAngleX = legX;
        bipedRightLeg.rotateAngleX = legX;

        bipedLeftArm.rotateAngleY = -0.2F;
        bipedLeftLeg.rotateAngleY = 0.2F;

        bipedRightArm.rotateAngleY = 0.2F;
        bipedRightLeg.rotateAngleY = -0.2F;
    }

    /**
     * Rotates legs in quopy fashion for walking.
     *
     * @param move      Entity motion parameter - i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param swing     Degree to which each 'limb' swings.
     * @param ticks     Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     * @param entity    The entity we're being called for.
     *
     */
    protected void rotateLegsOnGround(float move, float swing, float ticks, Entity entity) {
        float angle = PI * (float) Math.pow(swing, 16);

        float baseRotation = move * 0.6662F; // magic number ahoy
        float scale = swing / 4;

        bipedLeftArm.rotateAngleX =  MathHelper.cos(baseRotation + angle) * scale;
        bipedRightArm.rotateAngleX = MathHelper.cos(baseRotation + PI + angle / 2) * scale;

        bipedLeftLeg.rotateAngleX =  MathHelper.cos(baseRotation + PI - (angle * 0.4f)) * scale;
        bipedRightLeg.rotateAngleX = MathHelper.cos(baseRotation + angle / 5) * scale;

        bipedLeftArm.rotateAngleY = 0;
        bipedRightArm.rotateAngleY = 0;

        bipedLeftLeg.rotateAngleY = 0;
        bipedRightLeg.rotateAngleY = 0;
    }

    protected float getLegOutset() {
        if (isSleeping) return 3.6f;
        if (isCrouching()) return 1;
        return 5;
    }

    protected float getLegSpread() {
        return rainboom ? 2 : 1;
    }

    /**
     * Adjusts legs as if holding an item. Delegates to the correct arm/leg/limb as neccessary.
     *
     * @param swing
     */
    protected void holdItem(float swing) {
        boolean both = leftArmPose == ArmPose.ITEM && rightArmPose == ArmPose.ITEM;

        alignArmForAction(bipedLeftArm, leftArmPose, rightArmPose, both, swing, 1);
        alignArmForAction(bipedRightArm, rightArmPose, leftArmPose, both, swing, -1);
    }

    /**
     * Aligns an arm for the appropriate arm pose
     *
     * @param arm   The arm model to align
     * @param pose  The post to align to
     * @param both  True if we have something in both hands
     * @param swing     Degree to which each 'limb' swings.
     */
    protected void alignArmForAction(ModelRenderer arm, ArmPose pose, ArmPose complement, boolean both, float swing, float reflect) {
        switch (pose) {
            case ITEM:
                float swag = 1;
                if (!isFlying && both) {
                    swag -= (float)Math.pow(swing, 2);
                }
                float mult = 1 - swag/2;
                arm.rotateAngleX = arm.rotateAngleX * mult - (PI / 10) * swag;
                arm.rotateAngleZ = -reflect * (PI / 15);
                if (isSneak) {
                    arm.rotationPointX -= reflect * 2;
                }
            case EMPTY:
                arm.rotateAngleY = 0;
                break;
            case BLOCK:
                arm.rotateAngleX = (arm.rotateAngleX / 2 - 0.9424779F) - 0.3F;
                arm.rotateAngleY = reflect * PI / 9;
                arm.rotationPointX += reflect;
                arm.rotationPointZ += 3;
                if (isSneak) {
                    arm.rotationPointY += 4;
                }
                break;
            case BOW_AND_ARROW:
                aimBow(arm, swing);
                break;
            default:
        }
    }

    protected void aimBow(ModelRenderer arm, float ticks) {
        arm.rotateAngleX = ROTATE_270 + bipedHead.rotateAngleX + (MathHelper.sin(ticks * 0.067F) * 0.05F);
        arm.rotateAngleY = bipedHead.rotateAngleY - 0.06F;
        arm.rotateAngleZ = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
        if (isSneak) {
            arm.rotationPointY += 4;
        }
    }


    /**
     * Animates arm swinging. Delegates to the correct arm/leg/limb as neccessary.
     *
     * @param entity     The entity we are being called for.
     */
    protected void swingItem(Entity entity) {
        if (swingProgress > 0 && !isSleeping) {
            EnumHandSide mainSide = getMainHand(entity);

            swingArm(getArmForSide(mainSide));
        }
    }

    /**
     * Animates arm swinging.
     *
     * @param arm       The arm to swing
     */
    protected void swingArm(ModelRenderer arm) {
        float swing = 1 - (float)Math.pow(1 - swingProgress, 3);

        float deltaX = MathHelper.sin(swing * PI);
        float deltaZ = MathHelper.sin(swingProgress * PI);

        float deltaAim = deltaZ * (0.7F - bipedHead.rotateAngleX) * 0.75F;

        arm.rotateAngleX -= deltaAim + deltaX * 1.2F;
        arm.rotateAngleY += bipedBody.rotateAngleY * 2;
        arm.rotateAngleZ = -deltaZ * 0.4F;
    }

    /**
     * Animates the walking animation.
     *
     * @param ticks       Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     */
    protected void swingArms(float ticks) {
        if (isSleeping) return;

        float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(ticks * 0.067F) * 0.05F;

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

    public void init(float yOffset, float stretch) {
        boxList.clear();

        initHead(yOffset, stretch);
        initBody(yOffset, stretch);
        initLegs(yOffset, stretch);
        initTail(yOffset, stretch);
    }

    protected void initHead(float yOffset, float stretch) {


        bipedHead = new PonyRenderer(this, 0, 0)
                                 .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                 .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                 .box(-4, -4, -4, 8, 8, 8, stretch)
                     .tex(12, 16).box(-4, -6, 1, 2, 2, 2, stretch)
                          .flip().box( 2, -6, 1, 2, 2, 2, stretch);

        bipedHeadwear = new PonyRenderer(this, 32, 0)
                                     .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                     .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                     .box(-4, -4, -4, 8, 8, 8, stretch + 0.5F);

        snout = new PonySnout(this);
        snout.init(yOffset, stretch);
    }

    protected void initTail(float yOffset, float stretch) {
        tail = new PonyTail(this);
        tail.init(yOffset, stretch);
    }


    /**
     * Creates the main torso and neck.
     */
    protected void initBody(float yOffset, float stretch) {
        if (textureHeight == 64) {
            bipedBodyWear = new ModelRenderer(this, 16, 32);
        }

        bipedBody = new PonyRenderer(this, 16, 16)
                    .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                    .box(-4, 4, -2, 8, 8, 4, stretch);

        bipedBodyWear.addBox(-4, 4, -2, 8, 8, 4, stretch + 0.25F);
        bipedBodyWear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        upperTorso = new PlaneRenderer(this, 24, 0);
        upperTorso.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
                  .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                    .tex(24, 0)    .addEastPlane( 4, -4, -4, 8, 8, stretch)
                    .tex(4,  0)    .addEastPlane( 4, -4,  4, 8, 4, stretch)
                    .tex(56, 0)  .addBottomPlane(-4,  4, -4, 8, 8, stretch)
                    .tex(36, 16)   .addBackPlane(-4, -4,  8, 8, 4, stretch)
                                   .addBackPlane(-4,  0,  8, 8, 4, stretch)
                                 .addBottomPlane(-4,  4,  4, 8, 4, stretch)
                .flipZ().tex(32, 20).addTopPlane(-4, -4, -4, 8, 12, stretch)
                        .tex(24, 0).addWestPlane(-4, -4, -4, 8, 8, stretch)
                        .tex(4, 0) .addWestPlane(-4, -4,  4, 8, 4, stretch)
                // Tail stub
              .child(0)
                .tex(32, 0).addTopPlane(-1, 2, 2, 2, 6, stretch)
                        .addBottomPlane(-1, 4, 2, 2, 6, stretch)
                          .addEastPlane( 1, 2, 2, 2, 6, stretch)
                          .addBackPlane(-1, 2, 8, 2, 2, stretch)
                  .flipZ().addWestPlane(-1, 2, 2, 2, 6, stretch)
                  .rotate(0.5F, 0, 0);

        neck = new PlaneRenderer(this, 0, 16)
            .at(NECK_CENTRE_X, NECK_CENTRE_Y, NECK_CENTRE_Z)
            .rotate(NECK_ROT_X, 0, 0).around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
            .addFrontPlane(0, 0, 0, 4, 4, stretch)
            .addBackPlane(0, 0, 4, 4, 4, stretch)
            .addEastPlane(4, 0, 0, 4, 4, stretch)
            .addWestPlane(0, 0, 0, 4, 4, stretch);
    }

    protected void preInitLegs() {
        bipedLeftArm = new ModelRenderer(this, 32, 48);
        bipedRightArm = new ModelRenderer(this, 40, 16);

        bipedLeftLeg = new ModelRenderer(this, 16, 48);
        bipedRightLeg = new ModelRenderer(this, 0, 16);
    }

    protected void preInitLegwear() {
        bipedLeftArmwear = new ModelRenderer(this, 48, 48);
        bipedRightArmwear = new ModelRenderer(this, 40, 32);

        bipedLeftLegwear = new ModelRenderer(this, 0, 48);
        bipedRightLegwear = new ModelRenderer(this, 0, 32);
    }

    protected void initLegs(float yOffset, float stretch) {
        preInitLegs();
        preInitLegwear();

        int armWidth = getArmWidth();
        int armDepth = getArmDepth();

        float rarmX = getLegRotationX();
        float rarmY = getArmRotationY();

        float armX = THIRDP_ARM_CENTRE_X;
        float armY = THIRDP_ARM_CENTRE_Y;
        float armZ = BODY_CENTRE_Z / 2 - 1 - armDepth;

        bipedLeftArm .addBox(armX, armY, armZ, armWidth, 12, armDepth, stretch);
        bipedRightArm.addBox(armX - armWidth, armY, armZ, armWidth, 12, armDepth, stretch);

        bipedLeftLeg .addBox(armX, armY, armZ, armWidth, 12, armDepth, stretch);
        bipedRightLeg.addBox(armX - armWidth, armY, armZ, armWidth, 12, armDepth, stretch);

        bipedLeftArm .setRotationPoint( rarmX, yOffset + rarmY, 0);
        bipedRightArm.setRotationPoint(-rarmX, yOffset + rarmY, 0);

        bipedLeftLeg .setRotationPoint( rarmX, yOffset, 0);
        bipedRightLeg.setRotationPoint(-rarmX, yOffset, 0);

        bipedLeftArmwear.addBox(armX, armY, armZ, armWidth, 12, armDepth, stretch + 0.25f);
        bipedLeftArmwear.setRotationPoint(rarmX, yOffset + rarmY, 0);

        bipedRightArmwear.addBox(armX - armWidth, armY, armZ, armWidth, 12, armDepth, stretch + 0.25f);
        bipedRightArmwear.setRotationPoint(-rarmX, yOffset + rarmY, 0);

        bipedLeftLegwear.addBox(armX, armY, armZ, armWidth, 12, armDepth, stretch + 0.25f);
        bipedRightLegwear.setRotationPoint(rarmX, yOffset, 0);

        bipedRightLegwear.addBox(armX - armWidth, armY, armZ, armWidth, 12, armDepth, stretch + 0.25f);
        bipedRightLegwear.setRotationPoint(-rarmX, yOffset, 0);
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

    public ArmPose getArmPoseForSide(EnumHandSide side) {
        return side == EnumHandSide.RIGHT ? rightArmPose : leftArmPose;
    }

    @Override
    public IPonyData getMetadata() {
        return metadata;
    }

    @Override
    public boolean isCrouching() {
        return !rainboom && isSneak && !isFlying;
    }

    @Override
    public boolean isGoingFast() {
        return rainboom;
    }

    @Override
    public boolean hasHeadGear() {
        return headGear;
    }

    @Override
    public boolean isFlying() {
        return isFlying && canFly();
    }

    @Override
    public boolean isElytraFlying() {
        return isElytraFlying;
    }

    @Override
    public boolean isSleeping() {
        return isSleeping;
    }

    @Override
    public boolean isRiding() {
        return isRiding;
    }

    @Override
    public boolean isSwimming() {
        return isSwimming;
    }

    @Override
    public boolean isChild() {
        return getSize() == PonySize.FOAL;
    }

    @Override
    public PonySize getSize() {
        return isChild ? PonySize.FOAL : metadata.getSize();
    }

    @Override
    public float getSwingAmount() {
        return swingProgress;
    }

    @Override
    public float getRiderYOffset() {

        if (isChild()) {
            return 0.25F;
        }

        switch (getSize()) {
            case NORMAL: return 0.4F;
            case FOAL:
            case TALL:
            case LARGE:
            default: return 0.25F;
        }
    }

    /**
     * Sets the model's various rotation angles.
     *
     * @param entity    The entity we're being called for.
     * @param move      Entity motion parameter - i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param swing     Degree to which each 'limb' swings.
     * @param ticks       Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     * @param headYaw   Horizontal head motion in radians.
     * @param headPitch Vertical head motion in radians.
     * @param scale     Scaling factor used to render this model. Determined by the return value of {@link RenderLivingBase.prepareScale}. Usually {@code 0.0625F}.
     */
    @Override
    public void render(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {

        pushMatrix();
        transform(BodyPart.HEAD);
        renderHead(entity, move, swing, ticks, headYaw, headPitch, scale);
        popMatrix();

        pushMatrix();
        transform(BodyPart.NECK);
        renderNeck(scale);
        popMatrix();

        pushMatrix();
        transform(BodyPart.BODY);
        renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);
        popMatrix();

        pushMatrix();
        transform(BodyPart.LEGS);
        renderLegs(scale);
        popMatrix();
    }

    /**
     *
     * Called to render the head.
     *
     * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
     *
     */
    protected void renderHead(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        bipedHead.render(scale);
        bipedHeadwear.render(scale);
        bipedHead.postRender(scale);
    }

    protected void renderNeck(float scale) {
        GlStateManager.scale(0.9, 0.9, 0.9);
        neck.render(scale);
    }

    /**
    *
    * Called to render the head.
    *
    * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
    *
    */
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        bipedBody.render(scale);
        if (textureHeight == 64) {
            bipedBodyWear.render(scale);
        }
        upperTorso.render(scale);
        bipedBody.postRender(scale);
        tail.renderPart(scale);
    }

    protected void renderLegs(float scale) {
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
        if (isRiding()) translate(0, -0.4F, -0.2F);

        if (isSleeping) {
            rotate(90, 1, 0, 0);
            rotate(180, 0, 1, 0);
        }

        if (part == BodyPart.HEAD) {
            rotate(motionPitch, 1, 0, 0);
        }

        getSize().getTranformation().transform(this, part);
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
            isElytraFlying = pony.isElytraFlying;
            isSwimming = pony.isSwimming;
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
