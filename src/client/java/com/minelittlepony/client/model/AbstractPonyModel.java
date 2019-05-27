package com.minelittlepony.client.model;

import com.minelittlepony.client.model.armour.ModelPonyArmor;
import com.minelittlepony.client.model.armour.PonyArmor;
import com.minelittlepony.client.model.components.PonySnout;
import com.minelittlepony.client.model.components.PonyTail;
import com.minelittlepony.client.pony.PonyData;
import com.minelittlepony.client.transform.PonyTransformation;
import com.minelittlepony.client.util.render.AbstractRenderer;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.client.util.render.plane.PlaneRenderer;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IPart;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.IPonyData;
import com.minelittlepony.pony.meta.Size;
import com.minelittlepony.util.math.MathUtil;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

import static com.mojang.blaze3d.platform.GlStateManager.*;

/**
 * Foundation class for all types of ponies.
 */
public abstract class AbstractPonyModel<T extends LivingEntity> extends ClientPonyModel<T> {

    public boolean isSleeping;
    public boolean isFlying;
    public boolean isElytraFlying;
    public boolean isSwimming;
    public boolean isCrouching;
    public boolean isRidingInteractive;
    public boolean headGear;

    /**
     * Associated pony data.
     */
    private IPonyData metadata = new PonyData();

    /**
     * Vertical pitch whilst flying.
     */
    public float motionPitch;

    /**
     * Flag indicating that this model is performing a rainboom (flight).
     */
    protected boolean rainboom;

    protected double motionLerp;

    public PlaneRenderer upperTorso;
    public PlaneRenderer neck;

    public IPart tail;
    public PonySnout snout;

    public UUID interpolatorId;

    public AbstractPonyModel(boolean arms) {
        super(0, arms);
    }

    @Override
    public IEquestrianArmour<?> createArmour() {
        return new PonyArmor<>(new ModelPonyArmor<>(), new ModelPonyArmor<>());
    }

    /**
     * Checks flying and speed conditions and sets rainboom to true if we're a species with wings and is going faaast.
     */
    private void checkRainboom(T entity, float swing) {
        Vec3d motion = entity.getVelocity();
        double zMotion = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        rainboom = (isFlying() && canFly()) || isElytraFlying();
        rainboom &= zMotion > 0.4F;

        motionLerp = MathUtil.clampLimit(zMotion * 30, 1);
    }

    @Override
    public void updateLivingState(T entity, IPony pony) {
        isChild = entity.isBaby();
        isSneaking = entity.isSneaking();
        isCrouching = pony.isCrouching(entity);
        isSleeping = entity.isSleeping();
        isFlying = pony.isFlying(entity);
        isElytraFlying = entity.isFallFlying();
        isSwimming = pony.isSwimming(entity);
        headGear = pony.isWearingHeadgear(entity);
        isRidingInteractive = pony.isRidingInteractive(entity);
        interpolatorId = entity.getUuid();
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
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        checkRainboom(entity, swing);

        super.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);

        float headRotateAngleY = isSleeping() ? (Math.abs(entity.getUuid().getMostSignificantBits()) % 2.8F) - 1.9F : headYaw / 57.29578F;
        float headRotateAngleX = isSleeping() ? 0.1f : headPitch / 57.29578F;

        headRotateAngleX = Math.min(headRotateAngleX, (float) (0.5f - Math.toRadians(motionPitch)));
        headRotateAngleX = Math.max(headRotateAngleX, (float) (-1.25f - Math.toRadians(motionPitch)));

        updateHeadRotation(headRotateAngleX, headRotateAngleY);

        shakeBody(move, swing, getWobbleAmount(), ticks);
        rotateLegs(move, swing, ticks, entity);

        if (!isSwimming() && !rainboom) {
            holdItem(swing);
        }
        swingItem(entity);

        if (isCrouching()) {
            ponyCrouch();
        } else if (isRiding) {
            ponyRide();
        } else {
            adjustBody(BODY_ROT_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);

            rightLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            leftLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            swingArms(ticks);
            setHead(0, 0, 0);
        }

        if (isSleeping) {
            ponySleep();
        }

        animateWears();

        snout.setGender(metadata.getGender());
    }

    @Override
    public float getWobbleAmount() {

        if (getSwingAmount() <= 0) {
            return 0;
        }

        return MathHelper.sin(MathHelper.sqrt(getSwingAmount()) * PI * 2) * 0.04F;
    }

    /**
     * Aligns legs to a sneaky position.
     */
    protected void ponyCrouch() {
        adjustBody(BODY_ROT_X_SNEAK, BODY_RP_Y_SNEAK, BODY_RP_Z_SNEAK);
        setHead(0, 6, -2);

        rightArm.pitch -= LEG_ROT_X_SNEAK_ADJ;
        leftArm.pitch -= LEG_ROT_X_SNEAK_ADJ;

        leftLeg.rotationPointY = FRONT_LEG_RP_Y_SNEAK;
        rightLeg.rotationPointY = FRONT_LEG_RP_Y_SNEAK;
    }

    protected void ponySleep() {
        rightArm.pitch = ROTATE_270;
        leftArm.pitch = ROTATE_270;

        rightLeg.pitch = ROTATE_90;
        leftLeg.pitch = ROTATE_90;

        setHead(1, 2, isSneaking ? -1 : 1);

        AbstractRenderer.shiftRotationPoint(rightArm, 0, 2, 6);
        AbstractRenderer.shiftRotationPoint(leftArm, 0, 2, 6);
        AbstractRenderer.shiftRotationPoint(rightLeg, 0, 2, -8);
        AbstractRenderer.shiftRotationPoint(leftLeg, 0, 2, -8);
    }

    protected void ponyRide() {
        if (isRidingInteractive) {
            adjustBodyComponents(BODY_ROT_X_RIDING * 2, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
            adjustNeck(BODY_ROT_X_NOTSNEAK * 2, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK - 4);
            setHead(0, -2, -5);
        } else {
            adjustBodyComponents(BODY_ROT_X_RIDING, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
            adjustNeck(BODY_ROT_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);
            setHead(0, 0, 0);
        }

        leftLeg.rotationPointZ = 15;
        leftLeg.rotationPointY = 9;
        leftLeg.pitch = -PI / 4;
        leftLeg.yaw = -PI / 5;

        rightLeg.rotationPointZ = 15;
        rightLeg.rotationPointY = 9;
        rightLeg.pitch = -PI / 4;
        rightLeg.yaw =  PI / 5;

        leftArm.roll = -PI * 0.06f;
        rightArm.roll = PI * 0.06f;

        if (isRidingInteractive) {
            leftLeg.yaw = PI / 15;
            leftLeg.pitch = PI / 9;

            leftLeg.rotationPointZ = 10;
            leftLeg.rotationPointY = 7;

            rightLeg.yaw = -PI / 15;
            rightLeg.pitch = PI / 9;

            rightLeg.rotationPointZ = 10;
            rightLeg.rotationPointY = 7;

            leftArm.pitch = PI / 6;
            rightArm.pitch = PI / 6;

            leftArm.roll *= 2;
            rightArm.roll *= 2;
        }
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
        tail.setRotationAndAngles(isSwimming() || rainboom, interpolatorId, move, swing, bodySwing * 5, ticks);

        upperTorso.yaw = bodySwing;
        body.yaw = bodySwing;
        neck.yaw = bodySwing;
    }

    private void animateWears() {
        leftArmOverlay.copyRotation(leftArm);
        rightArmOverlay.copyRotation(rightArm);
        leftLegOverlay.copyRotation(leftLeg);
        rightLegOverlay.copyRotation(rightLeg);
        bodyOverlay.copyRotation(body);
    }

    @Override
    public Cuboid getHead() {
        return head;
    }

    @Override
    public void setPitch(float pitch) {
        motionPitch = pitch;
    }

    @Override
    public float getPitch() {
        return motionPitch;
    }

    /**
     * Sets the head rotation point.
     */
    protected void setHead(float posX, float posY, float posZ) {
        head.setRotationPoint(posX, posY, posZ);
        headwear.setRotationPoint(posX, posY, posZ);
    }

    /**
     * Called to update the head rotation.
     *
     * @param x     New rotation X
     * @param y     New rotation Y
     */
    protected void updateHeadRotation(float x, float y) {
        headwear.yaw = head.yaw = y;
        headwear.pitch = head.pitch = x;
    }

    /**
    *
    * Used to set the legs rotation based on walking/crouching animations.
    *
    * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
    *
    */
    protected void rotateLegs(float move, float swing, float ticks, T entity) {
        if (isSwimming()) {
            rotateLegsSwimming(move, swing, ticks, entity);
        } else if (isGoingFast()) {
            rotateLegsInFlight(move, swing, ticks, entity);
        } else {
            rotateLegsOnGround(move, swing, ticks, entity);
        }

        float sin = MathHelper.sin(body.yaw) * 5;
        float cos = MathHelper.cos(body.yaw) * 5;

        float spread = getLegSpread();

        rightArm.rotationPointZ = spread + sin;
        leftArm.rotationPointZ = spread - sin;

        float legRPX = cos - getLegOutset() - 0.001F;

        legRPX = metadata.getInterpolator(entity.getUuid()).interpolate("legOffset", legRPX, 3);

        rightArm.rotationPointX = -legRPX;
        rightLeg.rotationPointX = -legRPX;

        leftArm.rotationPointX = legRPX;
        leftLeg.rotationPointX = legRPX;

        rightArm.yaw += body.yaw;
        leftArm.yaw += body.yaw;

        rightArm.rotationPointY = leftArm.rotationPointY = 8;
        rightLeg.rotationPointZ = leftLeg.rotationPointZ = 10;
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
    protected void rotateLegsSwimming(float move, float swing, float ticks, T entity) {

        float legLeft = (ROTATE_90 + MathHelper.sin((move / 3) + 2 * PI/3) / 2) * (float)motionLerp;

        float left = (ROTATE_90 + MathHelper.sin((move / 3) + 2 * PI) / 2) * (float)motionLerp;
        float right = (ROTATE_90 + MathHelper.sin(move / 3) / 2) * (float)motionLerp;

        leftArm.pitch = -left;
        leftArm.yaw = -left/2;
        leftArm.roll = left/2;

        rightArm.pitch = -right;
        rightArm.yaw = right/2;
        rightArm.roll = -right/2;

        leftLeg.pitch = legLeft;
        rightLeg.pitch = right;

        leftLeg.yaw = 0;
        rightLeg.yaw = 0;
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

        leftArm.pitch = armX;
        rightArm.pitch = armX;

        leftLeg.pitch = legX;
        rightLeg.pitch = legX;

        leftArm.yaw = -0.2F;
        leftLeg.yaw = 0.2F;

        rightArm.yaw = 0.2F;
        rightLeg.yaw = -0.2F;

        rightArm.roll = 0;
        leftArm.roll = 0;
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
    protected void rotateLegsOnGround(float move, float swing, float ticks, T entity) {
        float angle = PI * (float) Math.pow(swing, 16);

        float baseRotation = move * 0.6662F; // magic number ahoy
        float scale = swing / 4;

        leftArm.pitch =  MathHelper.cos(baseRotation + angle) * scale;
        rightArm.pitch = MathHelper.cos(baseRotation + PI + angle / 2) * scale;

        leftLeg.pitch =  MathHelper.cos(baseRotation + PI - (angle * 0.4f)) * scale;
        rightLeg.pitch = MathHelper.cos(baseRotation + angle / 5) * scale;

        leftArm.yaw = 0;
        rightArm.yaw = 0;

        leftLeg.yaw = 0;
        rightLeg.yaw = 0;

        rightArm.roll = 0;
        leftArm.roll = 0;
    }

    protected float getLegOutset() {
        if (isSleeping()) {
            return 3.6f;
        }

        if (isCrouching()) {
            return 1;
        }

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

        alignArmForAction(leftArm, leftArmPose, rightArmPose, both, swing, 1);
        alignArmForAction(rightArm, rightArmPose, leftArmPose, both, swing, -1);
    }

    @Override
    public Cuboid getBodyPart(BodyPart part) {
        switch (part) {
            default:
            case HEAD: return head;
            case NECK: return neck;
            case TAIL:
            case LEGS:
            case BODY: return body;
        }
    }

    /**
     * Aligns an arm for the appropriate arm pose
     *
     * @param arm   The arm model to align
     * @param pose  The post to align to
     * @param both  True if we have something in both hands
     * @param swing     Degree to which each 'limb' swings.
     */
    protected void alignArmForAction(Cuboid arm, ArmPose pose, ArmPose complement, boolean both, float swing, float reflect) {
        switch (pose) {
            case ITEM:
                float swag = 1;
                if (!isFlying() && both) {
                    swag -= (float)Math.pow(swing, 2);
                }
                float mult = 1 - swag/2;
                arm.pitch = arm.pitch * mult - (PI / 10) * swag;
                arm.roll = -reflect * (PI / 15);
                if (isCrouching()) {
                    arm.rotationPointX -= reflect * 2;
                }
            case EMPTY:
                arm.yaw = 0;
                break;
            case BLOCK:
                arm.pitch = (arm.pitch / 2 - 0.9424779F) - 0.3F;
                arm.yaw = reflect * PI / 9;
                if (complement == pose) {
                    arm.yaw -= reflect * PI / 18;
                }
                arm.rotationPointX += reflect;
                arm.rotationPointZ += 3;
                if (isCrouching()) {
                    arm.rotationPointY += 4;
                }
                break;
            case BOW_AND_ARROW:
                aimBow(arm, swing);
                break;
            default:
        }
    }

    protected void aimBow(Cuboid arm, float ticks) {
        arm.pitch = ROTATE_270 + head.pitch + (MathHelper.sin(ticks * 0.067F) * 0.05F);
        arm.yaw = head.yaw - 0.06F;
        arm.roll = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;

        if (isSneaking) {
            arm.rotationPointY += 4;
        }
    }


    /**
     * Animates arm swinging. Delegates to the correct arm/leg/limb as neccessary.
     *
     * @param entity     The entity we are being called for.
     */
    protected void swingItem(T entity) {
        if (getSwingAmount() > 0 && !isSleeping()) {
            AbsoluteHand mainSide = getPreferedHand(entity);

            swingArm(getArm(mainSide));
        }
    }

    /**
     * Animates arm swinging.
     *
     * @param arm       The arm to swing
     */
    protected void swingArm(Cuboid arm) {
        float swing = 1 - (float)Math.pow(1 - getSwingAmount(), 3);

        float deltaX = MathHelper.sin(swing * PI);
        float deltaZ = MathHelper.sin(getSwingAmount() * PI);

        float deltaAim = deltaZ * (0.7F - head.pitch) * 0.75F;

        arm.pitch -= deltaAim + deltaX * 1.2F;
        arm.yaw += body.yaw * 2;
        arm.roll = -deltaZ * 0.4F;
    }

    /**
     * Animates the walking animation.
     *
     * @param ticks       Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     */
    protected void swingArms(float ticks) {
        if (isSleeping()) {
            return;
        }

        float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(ticks * 0.067F) * 0.05F;

        if (rightArmPose != ArmPose.EMPTY) {
            rightArm.roll += cos;
            rightArm.pitch += sin;
        }

        if (leftArmPose != ArmPose.EMPTY) {
            leftArm.roll += cos;
            leftArm.pitch += sin;
        }
    }

    protected void adjustBody(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);
        adjustNeck(rotateAngleX, rotationPointY, rotationPointZ);
    }

    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        body.pitch = rotateAngleX;
        body.rotationPointY = rotationPointY;
        body.rotationPointZ = rotationPointZ;

        upperTorso.pitch = rotateAngleX;
        upperTorso.rotationPointY = rotationPointY;
        upperTorso.rotationPointZ = rotationPointZ;
    }

    protected void adjustNeck(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        neck.setRotationPoint(NECK_ROT_X + rotateAngleX, rotationPointY, rotationPointZ);
    }

    @Override
    public void init(float yOffset, float stretch) {
        cuboidList.clear();

        initHead(yOffset, stretch);
        initBody(yOffset, stretch);
        initLegs(yOffset, stretch);
        initTail(yOffset, stretch);
    }

    protected void initHead(float yOffset, float stretch) {
        head = new PonyRenderer(this, 0, 0)
                                 .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                 .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                 .box(-4, -4, -4, 8, 8, 8, stretch);
        initEars(((PonyRenderer)head), yOffset, stretch);

        headwear = new PonyRenderer(this, 32, 0)
                                     .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                     .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                     .box(-4, -4, -4, 8, 8, 8, stretch + 0.7504F);

        snout = new PonySnout(this);
        snout.init(yOffset, stretch);
    }

    protected void initEars(PonyRenderer head, float yOffset, float stretch) {
        head.tex(12, 16).box(-4, -6, 1, 2, 2, 2, stretch)  // right ear
                 .flip().box( 2, -6, 1, 2, 2, 2, stretch); // left ear
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
            bodyOverlay.boxes.clear();
            cuboidList.add(bodyOverlay);
        }

        body = new PonyRenderer(this, 16, 16)
                    .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                    .box(-4, 4, -2, 8, 8, 4, stretch);

        bodyOverlay.addBox(-4, 4, -2, 8, 8, 4, stretch + 0.25F);
        bodyOverlay.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        upperTorso = new PlaneRenderer(this, 24, 0);
        upperTorso.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
                  .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                    .tex(24, 0)    .east( 4, -4, -4, 8, 8, stretch)
                    .tex(4,  0)    .east( 4, -4,  4, 8, 4, stretch)
                    .tex(56, 0)  .bottom(-4,  4, -4, 8, 8, stretch)
                    .tex(36, 16)  .north(-4, -4,  8, 8, 4, stretch)
                                  .north(-4,  0,  8, 8, 4, stretch)
                                 .bottom(-4,  4,  4, 8, 4, stretch)
                .flipZ().tex(32, 20).top(-4, -4, -4, 8, 12, stretch)
                        .tex(24, 0).west(-4, -4, -4, 8, 8, stretch)
                        .tex(4, 0) .west(-4, -4,  4, 8, 4, stretch)
                // Tail stub
              .child(0)
                .tex(32, 0).top(-1, 2, 2, 2, 6, stretch)
                        .bottom(-1, 4, 2, 2, 6, stretch)
                          .east( 1, 2, 2, 2, 6, stretch)
                         .south(-1, 2, 8, 2, 2, stretch)
                  .flipZ().west(-1, 2, 2, 2, 6, stretch)
                  .rotate(0.5F, 0, 0);

        neck = new PlaneRenderer(this, 0, 16)
            .at(NECK_CENTRE_X, NECK_CENTRE_Y, NECK_CENTRE_Z)
            .rotate(NECK_ROT_X, 0, 0).around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
            .north(0, 0, 0, 4, 4, stretch)
            .south(0, 0, 4, 4, 4, stretch)
             .east(4, 0, 0, 4, 4, stretch)
             .west(0, 0, 0, 4, 4, stretch);
    }

    protected void preInitLegs() {
        leftArm = new Cuboid(this, 32, 48);
        rightArm = new Cuboid(this, 40, 16);

        leftLeg = new Cuboid(this, 16, 48);
        rightLeg = new Cuboid(this, 0, 16);
    }

    protected void preInitLegwear() {
        leftArmOverlay.boxes.clear();
        cuboidList.add(leftArmOverlay);

        rightArmOverlay.boxes.clear();
        cuboidList.add(rightArmOverlay);

        leftLegOverlay.boxes.clear();
        cuboidList.add(leftLegOverlay);
        leftLegOverlay.setTextureOffset(0, 48);

        rightLegOverlay.boxes.clear();
        cuboidList.add(rightLegOverlay);
        rightLegOverlay.setTextureOffset(0, 32);
    }

    protected void initLegs(float yOffset, float stretch) {
        preInitLegs();
        preInitLegwear();

        int armLength = getArmLength();
        int armWidth = getArmWidth();
        int armDepth = getArmDepth();

        float rarmX = getLegRotationX();
        float rarmY = getArmRotationY();

        float armX = THIRDP_ARM_CENTRE_X;
        float armY = THIRDP_ARM_CENTRE_Y;
        float armZ = BODY_CENTRE_Z / 2 - 1 - armDepth;

        leftArm .addBox(armX, armY, armZ, armWidth, armLength, armDepth, stretch);
        rightArm.addBox(armX - armWidth, armY, armZ, armWidth, armLength, armDepth, stretch);

        leftLeg .addBox(armX, armY, armZ, armWidth, armLength, armDepth, stretch);
        rightLeg.addBox(armX - armWidth, armY, armZ, armWidth, armLength, armDepth, stretch);

        leftArm .setRotationPoint( rarmX, yOffset + rarmY, 0);
        rightArm.setRotationPoint(-rarmX, yOffset + rarmY, 0);

        leftLeg .setRotationPoint( rarmX, yOffset, 0);
        rightLeg.setRotationPoint(-rarmX, yOffset, 0);

        leftArmOverlay.addBox(armX, armY, armZ, armWidth, armLength, armDepth, stretch + 0.25f);
        leftArmOverlay.setRotationPoint(rarmX, yOffset + rarmY, 0);

        rightArmOverlay.addBox(armX - armWidth, armY, armZ, armWidth, armLength, armDepth, stretch + 0.25f);
        rightArmOverlay.setRotationPoint(-rarmX, yOffset + rarmY, 0);

        leftArmOverlay.addBox(armX, armY, armZ, armWidth, armLength, armDepth, stretch + 0.25f);
        rightLegOverlay.setRotationPoint(rarmX, yOffset, 0);

        rightLegOverlay.addBox(armX - armWidth, armY, armZ, armWidth, armLength, armDepth, stretch + 0.25f);
        rightLegOverlay.setRotationPoint(-rarmX, yOffset, 0);
    }

    protected int getArmWidth() {
        return 4;
    }

    protected int getArmDepth() {
        return 4;
    }

    protected int getArmLength() {
        return 12;
    }

    protected float getLegRotationX() {
        return 3;
    }

    protected float getArmRotationY() {
        return 8;
    }

    public ArmPose getArmPoseForSide(AbsoluteHand side) {
        return side == AbsoluteHand.RIGHT ? rightArmPose : leftArmPose;
    }

    @Override
    public IPonyData getMetadata() {
        return metadata;
    }

    @Override
    public void apply(IPonyData meta) {
        metadata = meta;
    }

    @Override
    public boolean isCrouching() {
        return isCrouching;
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
        return getSize() == Size.FOAL;
    }

    @Override
    public Size getSize() {
        return isChild ? Size.FOAL : getMetadata().getSize();
    }

    @Override
    public float getSwingAmount() {
        return handSwingProgress;
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
            case BULKY:
            default: return 0.25F;
        }
    }

    @Override
    public float getModelHeight() {
        return 2;
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
    public void render(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {

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
    protected void renderHead(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        head.render(scale);
        headwear.render(scale);
        head.applyTransform(scale);
    }

    protected void renderNeck(float scale) {
        scalef(0.9F, 0.9F, 0.9F);
        neck.render(scale);
    }

    /**
    *
    * Called to render the head.
    *
    * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
    *
    */
    protected void renderBody(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        body.render(scale);
        if (textureHeight == 64) {
            bodyOverlay.render(scale);
        }
        upperTorso.render(scale);
        body.applyTransform(scale);
        tail.renderPart(scale, entity.getUuid());
    }

    protected void renderLegs(float scale) {
        if (!isSneaking) body.applyTransform(scale);

        leftArm.render(scale);
        rightArm.render(scale);
        leftLeg.render(scale);
        rightLeg.render(scale);

        if (textureHeight == 64) {
            leftArmOverlay.render(scale);
            rightArmOverlay.render(scale);
            leftLegOverlay.render(scale);
            rightArmOverlay.render(scale);
        }
    }

    @Override
    public void transform(BodyPart part) {
        if (isSleeping()) {
            rotatef(90, 1, 0, 0);
            rotatef(180, 0, 1, 0);
        }

        if (part == BodyPart.HEAD) {
            rotatef(motionPitch, 1, 0, 0);
        }

        PonyTransformation.forSize(getSize()).transform(this, part);
    }

    /**
     * Copies this model's attributes from some other.
     */
    @Override
    public void setAttributes(BipedEntityModel<T> model) {
        super.setAttributes(model);

        if (model instanceof AbstractPonyModel) {
            AbstractPonyModel<T> pony = (AbstractPonyModel<T>) model;
            isFlying = pony.isFlying;
            isCrouching = pony.isCrouching;
            isElytraFlying = pony.isElytraFlying;
            isSwimming = pony.isSwimming;
            isSleeping = pony.isSleeping;
            headGear = pony.headGear;
            metadata = pony.metadata;
            motionPitch = pony.motionPitch;
            rainboom = pony.rainboom;
        }
    }
}
