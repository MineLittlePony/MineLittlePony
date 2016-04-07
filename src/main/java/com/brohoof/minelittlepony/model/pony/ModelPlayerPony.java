package com.brohoof.minelittlepony.model.pony;

import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;

import com.brohoof.minelittlepony.model.AbstractPonyModel;
import com.brohoof.minelittlepony.model.BodyPart;
import com.brohoof.minelittlepony.model.PonyModelConstants;
import com.brohoof.minelittlepony.model.part.PegasusWings;
import com.brohoof.minelittlepony.model.part.PonyEars;
import com.brohoof.minelittlepony.model.part.PonySnout;
import com.brohoof.minelittlepony.model.part.UnicornHorn;
import com.brohoof.minelittlepony.renderer.PlaneRenderer;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

public class ModelPlayerPony extends AbstractPonyModel implements PonyModelConstants {

    private final boolean smallArms;
    public boolean rainboom;

    public ModelRenderer bipedCape;

    public PlaneRenderer[] Bodypiece;
    public PlaneRenderer[] BodypieceNeck;
    public ModelRenderer unicornarm;
    public PlaneRenderer[] Tail;

    public ModelPlayerPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;
        addParts();
    }

    protected void addParts() {
        modelParts.add(new PonyEars());
        modelParts.add(new PonySnout());
        modelParts.add(new UnicornHorn());
        modelParts.add(new PegasusWings());
    }

    @Override
    public void animate(float move, float swing, float tick, float horz, float vert) {
        this.checkRainboom(swing);
        this.rotateHead(horz, vert);
        this.swingTailZ(move, swing);
        float bodySwingRotation = 0.0F;
        if (this.swingProgress > -9990.0F && (!this.metadata.getRace().hasHorn() || this.metadata.getGlowColor() == 0)) {
            bodySwingRotation = MathHelper.sin(MathHelper.sqrt_float(this.swingProgress) * 3.1415927F * 2.0F) * 0.2F;
        }

        this.bipedBody.rotateAngleY = bodySwingRotation * 0.2F;

        int k1;
        for (k1 = 0; k1 < this.Bodypiece.length; ++k1) {
            this.Bodypiece[k1].rotateAngleY = bodySwingRotation * 0.2F;
        }

        for (k1 = 0; k1 < this.BodypieceNeck.length; ++k1) {
            this.BodypieceNeck[k1].rotateAngleY = bodySwingRotation * 0.2F;
        }

        int tailstop = this.Tail.length - this.metadata.getTail().getSize() * 5;
        if (tailstop <= 1) {
            tailstop = 0;
        }

        for (k1 = 0; k1 < tailstop; ++k1) {
            this.Tail[k1].rotateAngleY = bodySwingRotation;
        }

        this.bipedHead.offsetY = 0f;
        this.bipedHead.offsetZ = 0f;
        this.bipedHeadwear.offsetY = 0f;
        this.bipedHeadwear.offsetZ = 0f;
        this.setLegs(move, swing, tick);
        this.holdItem();
        this.swingItem(this.swingProgress);
        if (this.isSneak && !this.isFlying) {
            this.adjustBody(BODY_ROTATE_ANGLE_X_SNEAK, BODY_RP_Y_SNEAK, BODY_RP_Z_SNEAK);
            this.sneakLegs();
            this.setHead(0.0F, 6.0F, -2.0F);
            this.sneakTail();
        } else if (this.isRiding) {

            this.adjustBodyComponents(BODY_ROTATE_ANGLE_X_RIDING, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
            this.adjustNeck((float) (Math.PI * 1), 9, 1);
            this.bipedHead.offsetY = .1f;
            this.bipedHead.offsetZ = .1f;
            this.bipedHeadwear.offsetY = .1f;
            this.bipedHeadwear.offsetZ = .1f;
            this.bipedLeftLeg.rotationPointZ = 15;
            this.bipedLeftLeg.rotationPointY = 21;
            this.bipedLeftLeg.rotateAngleX = (float) (Math.PI * 1.5);
            this.bipedLeftLeg.rotateAngleY = -.2f;

            this.bipedRightLeg.rotationPointZ = 15;
            this.bipedRightLeg.rotationPointY = 21;
            this.bipedRightLeg.rotateAngleX = (float) (Math.PI * 1.5);
            this.bipedRightLeg.rotateAngleY = .2f;

            for (int i = 0; i < Tail.length; ++i) {
                setRotationPoint(this.Tail[i], TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_NOTSNEAK);
                this.Tail[i].rotationPointZ = 15;
                this.Tail[i].rotationPointY = 14;
            }
        } else {

            this.adjustBody(BODY_ROTATE_ANGLE_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);

            this.bipedRightLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.bipedLeftLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.swingArms(tick);
            this.setHead(0.0F, 0.0F, 0.0F);

            for (k1 = 0; k1 < tailstop; ++k1) {
                setRotationPoint(this.Tail[k1], TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_NOTSNEAK);
                if (this.rainboom) {
                    this.Tail[k1].rotateAngleX = ROTATE_90 + 0.1F * MathHelper.sin(move);
                } else {
                    this.Tail[k1].rotateAngleX = 0.5F * swing;
                }
            }

            if (!this.rainboom) {
                this.swingTailX(tick);
            }
        }

        if (this.rainboom) {

            for (k1 = 0; k1 < tailstop; ++k1) {
                this.Tail[k1].rotationPointY += 6.0F;
                ++this.Tail[k1].rotationPointZ;
            }
        }

        if (this.isSleeping) {
            this.ponySleep();
        }

        if (this.aimedBow) {
            this.aimBow(tick);
        }

        this.fixSpecialRotations();
        this.fixSpecialRotationPoints(move);

        animateWears();

    }

    private void animateWears() {
        copyModelAngles(bipedLeftArm, bipedLeftArmwear);
        copyModelAngles(bipedRightArm, bipedRightArmwear);
        copyModelAngles(bipedLeftLeg, bipedLeftLegwear);
        copyModelAngles(bipedRightLeg, bipedRightLegwear);
        copyModelAngles(bipedBody, bipedBodyWear);
    }

    protected void checkRainboom(float swing) {
        this.rainboom = this.metadata.getRace() != null && this.metadata.getRace().hasWings() && this.isFlying && swing >= 0.9999F;
    }

    protected void setHead(float posX, float posY, float posZ) {
        setRotationPoint(this.bipedHead, posX, posY, posZ);
        setRotationPoint(this.bipedHeadwear, posX, posY, posZ);
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

        final float max = 0.5f;
        final float min = -1.25f;
        headRotateAngleX = Math.min(headRotateAngleX, max);
        headRotateAngleX = Math.max(headRotateAngleX, min);
        this.bipedHead.rotateAngleY = headRotateAngleY;
        this.bipedHead.rotateAngleX = headRotateAngleX;
        this.bipedHeadwear.rotateAngleY = headRotateAngleY;
        this.bipedHeadwear.rotateAngleX = headRotateAngleX;
    }

    protected void setLegs(float move, float swing, float tick) {
        this.rotateLegs(move, swing, tick);
        this.adjustLegs();
    }

    protected void rotateLegs(float move, float swing, float tick) {
        float rightArmRotateAngleX;
        float leftArmRotateAngleX;
        float rightLegRotateAngleX;
        float leftLegRotateAngleX;
        if (this.isFlying && this.metadata.getRace().hasWings()) {
            if (this.rainboom) {
                rightArmRotateAngleX = ROTATE_270;
                leftArmRotateAngleX = ROTATE_270;
                rightLegRotateAngleX = ROTATE_90;
                leftLegRotateAngleX = ROTATE_90;
            } else {
                rightArmRotateAngleX = MathHelper.sin(0.0F - swing * 0.5F);
                leftArmRotateAngleX = MathHelper.sin(0.0F - swing * 0.5F);
                rightLegRotateAngleX = MathHelper.sin(swing * 0.5F);
                leftLegRotateAngleX = MathHelper.sin(swing * 0.5F);
            }

            this.steveRightArm.rotateAngleY = 0.2F;
            this.bipedRightArm.rotateAngleY = 0.2F;
            this.bipedLeftArm.rotateAngleY = -0.2F;
            this.bipedRightLeg.rotateAngleY = -0.2F;
            this.bipedLeftLeg.rotateAngleY = 0.2F;

        } else {
            float swag = (float) Math.pow(swing, 16.0D);
            float raQuad = 3.1415927F * swag * 0.5F;
            float laQuad = 3.1415927F * swag;
            float rlQuad = 3.1415927F * swag * 0.2F;
            float llQuad = 3.1415927F * swag * -0.4F;
            rightArmRotateAngleX = MathHelper.cos(move * 0.6662F + 3.1415927F + raQuad) * 0.45F * swing;
            leftArmRotateAngleX = MathHelper.cos(move * 0.6662F + laQuad) * 0.45F * swing;
            rightLegRotateAngleX = MathHelper.cos(move * 0.6662F + rlQuad) * 0.45F * swing;
            leftLegRotateAngleX = MathHelper.cos(move * 0.6662F + 3.1415927F + llQuad) * 0.45F * swing;
            this.steveRightArm.rotateAngleY = 0.0F;
            this.unicornarm.rotateAngleY = 0.0F;

            this.bipedRightArm.rotateAngleY = 0.0F;
            this.bipedLeftArm.rotateAngleY = 0.0F;
            this.bipedRightLeg.rotateAngleY = 0.0F;
            this.bipedLeftLeg.rotateAngleY = 0.0F;
        }

        this.bipedRightArm.rotateAngleX = rightArmRotateAngleX;
        this.steveRightArm.rotateAngleX = rightArmRotateAngleX;
        this.unicornarm.rotateAngleX = 0.0F;

        this.bipedLeftArm.rotateAngleX = leftArmRotateAngleX;
        this.bipedRightLeg.rotateAngleX = rightLegRotateAngleX;
        this.bipedLeftLeg.rotateAngleX = leftLegRotateAngleX;
        this.bipedRightArm.rotateAngleZ = 0.0F;

        this.steveRightArm.rotateAngleZ = 0.0F;
        this.unicornarm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
    }

    protected void adjustLegs() {
        float sinBodyRotateAngleYFactor = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
        float cosBodyRotateAngleYFactor = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
        float legOutset = 4.0F;
        if (this.isSneak && !this.isFlying) {
            legOutset = smallArms ? 1.0F : 0F;
        }

        if (this.isSleeping) {
            legOutset = 2.6F;
        }

        if (this.rainboom) {
            this.bipedRightArm.rotationPointZ = sinBodyRotateAngleYFactor + 2.0F;
            this.steveRightArm.rotationPointZ = sinBodyRotateAngleYFactor + 2.0F;
            this.bipedLeftArm.rotationPointZ = 0.0F - sinBodyRotateAngleYFactor + 2.0F;
        } else {
            this.bipedRightArm.rotationPointZ = sinBodyRotateAngleYFactor + 1.0F;
            this.steveRightArm.rotationPointZ = sinBodyRotateAngleYFactor + 1.0F;
            this.bipedLeftArm.rotationPointZ = 0.0F - sinBodyRotateAngleYFactor + 1.0F;
        }
        this.steveRightArm.rotationPointX = 0.0F - cosBodyRotateAngleYFactor;

        this.bipedRightArm.rotationPointX = 0.0F - cosBodyRotateAngleYFactor - 1.0F + legOutset;
        this.bipedLeftArm.rotationPointX = cosBodyRotateAngleYFactor + 2.0F - legOutset;
        this.bipedRightLeg.rotationPointX = 0.0F - cosBodyRotateAngleYFactor - 1.0F + legOutset;
        this.bipedLeftLeg.rotationPointX = cosBodyRotateAngleYFactor + 1.0F - legOutset;

        this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
        this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
        this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;

        this.bipedRightArm.rotationPointY = 8.0F;
        this.bipedLeftArm.rotationPointY = 8.0F;
        this.bipedRightLeg.rotationPointZ = 10.0F;
        this.bipedLeftLeg.rotationPointZ = 10.0F;
    }

    protected void swingTailZ(float move, float swing) {
        int tailstop = this.Tail.length - this.metadata.getTail().getSize() * 5;
        if (tailstop <= 1) {
            tailstop = 0;
        }

        for (int j = 0; j < tailstop; ++j) {
            if (this.rainboom) {
                this.Tail[j].rotateAngleZ = 0.0F;
            } else {
                this.Tail[j].rotateAngleZ = MathHelper.cos(move * 0.8F) * 0.2F * swing;
            }
        }

    }

    protected void swingTailX(float tick) {
        float sinTickFactor = MathHelper.sin(tick * 0.067F) * 0.05F;
        int tailstop = this.Tail.length - this.metadata.getTail().getSize() * 5;
        if (tailstop <= 1) {
            tailstop = 0;
        }

        for (int l6 = 0; l6 < tailstop; ++l6) {
            this.Tail[l6].rotateAngleX += sinTickFactor;
        }

    }

    protected void holdItem() {
        if (this.heldItemRight != 0 && !this.rainboom && (!this.metadata.getRace().hasHorn() || this.metadata.getGlowColor() == 0)) {
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.3141593F;
            this.steveRightArm.rotateAngleX = this.steveRightArm.rotateAngleX * 0.5F - 0.3141593F;
        }

    }

    protected void swingItem(float swingProgress) {
        if (swingProgress > -9990.0F && !this.isSleeping) {
            float f16 = 1.0F - swingProgress;
            f16 *= f16 * f16;
            f16 = 1.0F - f16;
            float f22 = MathHelper.sin(f16 * 3.1415927F);
            float f28 = MathHelper.sin(swingProgress * 3.1415927F);
            float f33 = f28 * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            if (this.metadata.getRace().hasHorn() && this.metadata.getGlowColor() != 0 && this.heldItemRight != 0) {
                this.unicornarm.rotateAngleX = (float) (this.unicornarm.rotateAngleX - (f22 * 1.2D + f33));
                this.unicornarm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
                this.unicornarm.rotateAngleZ = f28 * -0.4F;
            } else {
                this.bipedRightArm.rotateAngleX = (float) (this.bipedRightArm.rotateAngleX - (f22 * 1.2D + f33));
                this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
                this.bipedRightArm.rotateAngleZ = f28 * -0.4F;
                this.steveRightArm.rotateAngleX = (float) (this.steveRightArm.rotateAngleX - (f22 * 1.2D + f33));
                this.steveRightArm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
                this.steveRightArm.rotateAngleZ = f28 * -0.4F;
            }
        }

    }

    protected void swingArms(float tick) {
        if (this.heldItemRight != 0 && !this.isSleeping) {
            float cosTickFactor = MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
            float sinTickFactor = MathHelper.sin(tick * 0.067F) * 0.05F;
            if (this.metadata.getRace().hasHorn() && this.metadata.getGlowColor() != 0) {
                this.unicornarm.rotateAngleZ += cosTickFactor;
                this.unicornarm.rotateAngleX += sinTickFactor;
            } else {
                this.bipedRightArm.rotateAngleZ += cosTickFactor;
                this.bipedRightArm.rotateAngleX += sinTickFactor;
                this.steveRightArm.rotateAngleZ += cosTickFactor;
                this.steveRightArm.rotateAngleX += sinTickFactor;
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

        int k3;
        for (k3 = 0; k3 < this.Bodypiece.length; ++k3) {
            this.Bodypiece[k3].rotateAngleX = rotateAngleX;
            this.Bodypiece[k3].rotationPointY = rotationPointY;
            this.Bodypiece[k3].rotationPointZ = rotationPointZ;
        }
    }

    protected void adjustNeck(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        for (int k3 = 0; k3 < this.BodypieceNeck.length; ++k3) {
            this.BodypieceNeck[k3].rotateAngleX = NECK_ROT_X + rotateAngleX;
            this.BodypieceNeck[k3].rotationPointY = rotationPointY;
            this.BodypieceNeck[k3].rotationPointZ = rotationPointZ;
        }

    }

    protected void sneakLegs() {
        this.steveRightArm.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        this.unicornarm.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;

        this.bipedRightArm.rotateAngleX -= SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        this.bipedLeftArm.rotateAngleX -= SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        this.bipedRightLeg.rotationPointY = FRONT_LEG_RP_Y_SNEAK;
        this.bipedLeftLeg.rotationPointY = FRONT_LEG_RP_Y_SNEAK;

    }

    protected void sneakTail() {
        int tailstop = this.Tail.length - this.metadata.getTail().getSize() * 5;
        if (tailstop <= 1) {
            tailstop = 0;
        }

        for (int i7 = 0; i7 < tailstop; ++i7) {
            setRotationPoint(this.Tail[i7], TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_SNEAK);
            this.Tail[i7].rotateAngleX = 0.0F;
        }

    }

    protected void ponySleep() {
        this.bipedRightArm.rotateAngleX = ROTATE_270;
        this.bipedLeftArm.rotateAngleX = ROTATE_270;
        this.bipedRightLeg.rotateAngleX = ROTATE_90;
        this.bipedLeftLeg.rotateAngleX = ROTATE_90;
        float headPosX;
        float headPosY;
        float headPosZ;
        if (this.isSneak) {
            headPosY = 2.0F;
            headPosZ = -1.0F;
            headPosX = 1.0F;
        } else {
            headPosY = 2.0F;
            headPosZ = 1.0F;
            headPosX = 1.0F;
        }

        this.setHead(headPosX, headPosY, headPosZ);
        shiftRotationPoint(this.bipedRightArm, 0.0F, 2.0F, 6.0F);
        shiftRotationPoint(this.bipedLeftArm, 0.0F, 2.0F, 6.0F);
        shiftRotationPoint(this.bipedRightLeg, 0.0F, 2.0F, -8.0F);
        shiftRotationPoint(this.bipedLeftLeg, 0.0F, 2.0F, -8.0F);
    }

    protected void aimBow(float tick) {
        if (this.metadata.getRace().hasHorn() && this.metadata.getGlowColor() != 0) {
            this.aimBowUnicorn(tick);
        } else {
            this.aimBowPony(tick);
        }

    }

    protected void aimBowPony(float tick) {
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedRightArm.rotateAngleY = -0.06F + this.bipedHead.rotateAngleY;
        this.bipedRightArm.rotateAngleX = ROTATE_270 + this.bipedHead.rotateAngleX;
        this.bipedRightArm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.05F;
        shiftRotationPoint(this.bipedRightArm, 0.0F, 0.0F, 1.0F);

        this.bipedRightArmwear.rotateAngleZ = 0.0F;
        this.bipedRightArmwear.rotateAngleY = -0.06F + this.bipedHead.rotateAngleY;
        this.bipedRightArmwear.rotateAngleX = ROTATE_270 + this.bipedHead.rotateAngleX;
        this.bipedRightArmwear.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArmwear.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.05F;
        shiftRotationPoint(this.bipedRightArmwear, 0.0F, 0.0F, 1.0F);
    }

    protected void aimBowUnicorn(float tick) {
        this.unicornarm.rotateAngleZ = 0.0F;
        this.unicornarm.rotateAngleY = -0.06F + this.bipedHead.rotateAngleY;
        this.unicornarm.rotateAngleX = ROTATE_270+ this.bipedHead.rotateAngleX;
        this.unicornarm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        this.unicornarm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.05F;
    }

    protected void fixSpecialRotations() {
        this.Bodypiece[9].rotateAngleX += 0.5F;
        this.Bodypiece[10].rotateAngleX += 0.5F;
        this.Bodypiece[11].rotateAngleX += 0.5F;
        this.Bodypiece[12].rotateAngleX += 0.5F;
        this.Bodypiece[13].rotateAngleX += 0.5F;
    }

    protected void fixSpecialRotationPoints(float move) {}

    @Override
    public void render() {

        pushMatrix();
        this.transform(BodyPart.HEAD);
        this.renderHead();
        popMatrix();

        pushMatrix();
        this.transform(BodyPart.NECK);
        this.renderNeck();
        popMatrix();

        pushMatrix();
        this.transform(BodyPart.BODY);
        this.renderBody();
        this.renderTail();
        popMatrix();

        pushMatrix();
        this.transform(BodyPart.LEGS);
        this.renderLegs();
        popMatrix();
    }

    protected void renderHead() {
        this.bipedHead.render(this.scale);
        this.bipedHeadwear.render(this.scale);
    }

    protected void renderNeck() {
        for (PlaneRenderer element : this.BodypieceNeck) {
            element.render(this.scale);
        }

    }

    protected void renderBody() {
        this.bipedBody.render(this.scale);
        if (this.textureHeight == 64) {
            this.bipedBodyWear.render(this.scale);
        }
        for (int k1 = 0; k1 < this.Bodypiece.length; ++k1) {
            this.Bodypiece[k1].render(this.scale);
        }

    }

    protected void renderTail() {
        int var3 = this.Tail.length - this.metadata.getTail().getSize() * 5;
        if (var3 <= 1) {
            var3 = 0;
        }

        for (int k = 0; k < var3; ++k) {
            this.Tail[k].render(this.scale);
        }

    }

    protected void renderLegs() {
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
        this.Tail = new PlaneRenderer[21];

        this.Bodypiece = new PlaneRenderer[14];
        this.BodypieceNeck = new PlaneRenderer[4];
        this.initHeadTextures();
        this.initBodyTextures();
        this.initLegTextures();
        this.initTailTextures();
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
        this.Bodypiece[1] = new PlaneRenderer(this, 24, 0);

        this.Bodypiece[0] = new PlaneRenderer(this, 24, 0);
        this.Bodypiece[1] = new PlaneRenderer(this, 24, 0);
        this.Bodypiece[2] = new PlaneRenderer(this, 32, 20);
        this.Bodypiece[2].mirrorxy = true;
        this.Bodypiece[3] = new PlaneRenderer(this, 56, 0);
        this.Bodypiece[4] = new PlaneRenderer(this, 4, 0);
        this.Bodypiece[5] = new PlaneRenderer(this, 4, 0);
        this.Bodypiece[6] = new PlaneRenderer(this, 36, 16);
        this.Bodypiece[7] = new PlaneRenderer(this, 36, 16);
        this.Bodypiece[8] = new PlaneRenderer(this, 36, 16);
        this.Bodypiece[9] = new PlaneRenderer(this, 32, 0);
        this.Bodypiece[10] = new PlaneRenderer(this, 32, 0);
        this.Bodypiece[11] = new PlaneRenderer(this, 32, 0);
        this.Bodypiece[11].mirror = true;
        this.Bodypiece[12] = new PlaneRenderer(this, 32, 0);
        this.Bodypiece[13] = new PlaneRenderer(this, 32, 0);
        // neck
        this.BodypieceNeck[0] = new PlaneRenderer(this, 0, 16);
        this.BodypieceNeck[1] = new PlaneRenderer(this, 0, 16);
        this.BodypieceNeck[2] = new PlaneRenderer(this, 0, 16);
        this.BodypieceNeck[3] = new PlaneRenderer(this, 0, 16);

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

        this.unicornarm = new ModelRenderer(this, 40, 32).setTextureSize(64, 64);

        this.boxList.remove(this.steveRightArm);
        this.boxList.remove(this.unicornarm);
    }

    protected void initTailTextures() {
        // upper
        this.Tail[0] = new PlaneRenderer(this, 32, 0);
        this.Tail[1] = new PlaneRenderer(this, 36, 0);
        this.Tail[2] = new PlaneRenderer(this, 32, 0);
        this.Tail[3] = new PlaneRenderer(this, 36, 0);
        this.Tail[4] = new PlaneRenderer(this, 32, 0);
        this.Tail[5] = new PlaneRenderer(this, 32, 0);
        this.Tail[6] = new PlaneRenderer(this, 36, 4);
        this.Tail[7] = new PlaneRenderer(this, 32, 4);
        this.Tail[8] = new PlaneRenderer(this, 36, 4);
        this.Tail[9] = new PlaneRenderer(this, 32, 4);
        this.Tail[10] = new PlaneRenderer(this, 32, 0);
        this.Tail[11] = new PlaneRenderer(this, 36, 0);
        this.Tail[12] = new PlaneRenderer(this, 32, 0);
        this.Tail[13] = new PlaneRenderer(this, 36, 0);
        this.Tail[14] = new PlaneRenderer(this, 32, 0);
        this.Tail[15] = new PlaneRenderer(this, 32, 0);
        this.Tail[16] = new PlaneRenderer(this, 36, 4);
        this.Tail[17] = new PlaneRenderer(this, 32, 4);
        this.Tail[18] = new PlaneRenderer(this, 36, 4);
        this.Tail[19] = new PlaneRenderer(this, 32, 4);
        this.Tail[20] = new PlaneRenderer(this, 32, 0);
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        this.initHeadPositions(yOffset, stretch);
        this.initBodyPositions(yOffset, stretch);
        this.initLegPositions(yOffset, stretch);
        this.initTailPositions(yOffset, stretch);
    }

    protected void initHeadPositions(float yOffset, float stretch) {
        this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, stretch);
        this.bipedHead.addBox(-4.0F + HEAD_CENTRE_X, -4 + HEAD_CENTRE_Y, -4.0F + HEAD_CENTRE_Z, 8, 8, 8, stretch);
        this.bipedHead.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.bipedHeadwear.addBox(-4.0F + HEAD_CENTRE_X, -4.0F + HEAD_CENTRE_Y, -4.0F + HEAD_CENTRE_Z, 8, 8, 8, stretch + 0.5F);
        this.bipedHeadwear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
    }

    protected void initBodyPositions(float yOffset, float stretch) {
        this.bipedBody.addBox(-4.0F, 4.0F, -2.0F, 8, 8, 4, stretch);
        this.bipedBody.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.bipedBodyWear.addBox(-4.0F, 4.0F, -2.0F, 8, 8, 4, stretch + 0.25F);
        this.bipedBodyWear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        this.Bodypiece[0].addSidePlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, -4.0F + BODY_CENTRE_Z, 0, 8, 8, stretch);
        this.Bodypiece[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[1].addSidePlane(4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, -4.0F + BODY_CENTRE_Z, 0, 8, 8, stretch);
        this.Bodypiece[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[2].addTopPlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, -4.0F + BODY_CENTRE_Z, 8, 0, 12, stretch);
        this.Bodypiece[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[3].addBottomPlane(-4.0F + BODY_CENTRE_X, 4.0F + BODY_CENTRE_Y, -4.0F + BODY_CENTRE_Z, 8, 0, 8, stretch);
        this.Bodypiece[3].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[4].addSidePlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, 4.0F + BODY_CENTRE_Z, 0, 8, 4, stretch);
        this.Bodypiece[4].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[5].addSidePlane(4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, 4.0F + BODY_CENTRE_Z, 0, 8, 4, stretch);
        this.Bodypiece[5].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[6].addBackPlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, 8.0F + BODY_CENTRE_Z, 8, 4, 0, stretch);
        this.Bodypiece[6].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[7].addBackPlane(-4.0F + BODY_CENTRE_X, 0.0F + BODY_CENTRE_Y, 8.0F + BODY_CENTRE_Z, 8, 4, 0, stretch);
        this.Bodypiece[7].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[8].addBottomPlane(-4.0F + BODY_CENTRE_X, 4.0F + BODY_CENTRE_Y, 4.0F + BODY_CENTRE_Z, 8, 0, 4, stretch);
        this.Bodypiece[8].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[9].addTopPlane(-1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z, 2, 0, 6, stretch);
        this.Bodypiece[9].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[10].addBottomPlane(-1.0F + BODY_CENTRE_X, 4.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z, 2, 0, 6, stretch);
        this.Bodypiece[10].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[11].addSidePlane(-1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z, 0, 2, 6, stretch);
        this.Bodypiece[11].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[12].addSidePlane(1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z, 0, 2, 6, stretch);
        this.Bodypiece[12].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[13].addBackPlane(-1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 8.0F + BODY_CENTRE_Z, 2, 2, 0, stretch);
        this.Bodypiece[13].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        this.BodypieceNeck[0].addBackPlane(-2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y, -8.8F + BODY_CENTRE_Z, 4, 4, 0, stretch);
        this.BodypieceNeck[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck[1].addBackPlane(-2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y, -4.8F + BODY_CENTRE_Z, 4, 4, 0, stretch);
        this.BodypieceNeck[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck[2].addSidePlane(-2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y, -8.8F + BODY_CENTRE_Z, 0, 4, 4, stretch);
        this.BodypieceNeck[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck[3].addSidePlane(2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y, -8.8F + BODY_CENTRE_Z, 0, 4, 4, stretch);
        this.BodypieceNeck[3].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck[0].rotateAngleX = NECK_ROT_X;
        this.BodypieceNeck[1].rotateAngleX = NECK_ROT_X;
        this.BodypieceNeck[2].rotateAngleX = NECK_ROT_X;
        this.BodypieceNeck[3].rotateAngleX = NECK_ROT_X;
    }

    protected void initLegPositions(float yOffset, float stretch) {
        if (this.smallArms) {
            this.bipedRightArm.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 3, 12, 4, stretch);
            this.bipedRightArm.setRotationPoint(-2.0F, 8.5F + yOffset, 0.0F);
            if (bipedRightArmwear != null) {
                this.bipedRightArmwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 3, 12, 4, stretch + 0.25f);
                this.bipedRightArmwear.setRotationPoint(-3.0F, 8.5F + yOffset, 0.0F);
            }
            this.bipedLeftArm.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 3, 12, 4, stretch);
            this.bipedLeftArm.setRotationPoint(3.0F, 8.5F + yOffset, 0.0F);
            if (this.bipedLeftArmwear != null) {
                this.bipedLeftArmwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 3, 12, 4, stretch + 0.25f);
                this.bipedLeftArmwear.setRotationPoint(3.0F, 8.5F + yOffset, 0.0F);
            }
        } else {
            this.bipedRightArm.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch);
            this.bipedRightArm.setRotationPoint(-3.0F, 8.0F + yOffset, 0.0F);
            if (bipedRightArmwear != null) {
                this.bipedRightArmwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch + 0.25f);
                this.bipedRightArmwear.setRotationPoint(-3.0F, 8.0F + yOffset, 0.0F);
            }
            this.bipedLeftArm.addBox(-3.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch);
            this.bipedLeftArm.setRotationPoint(3.0F, 8.0F + yOffset, 0.0F);
            if (this.bipedLeftArmwear != null) {
                this.bipedLeftArmwear.addBox(-3.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch + 0.25f);
                this.bipedLeftArmwear.setRotationPoint(3.0F, 8.0F + yOffset, 0.0F);
            }
        }
        this.bipedRightLeg.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch);
        this.bipedRightLeg.setRotationPoint(-3.0F, 0.0F + yOffset, 0.0F);

        if (bipedRightLegwear != null) {
            this.bipedRightLegwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch + 0.25f);
            this.bipedRightLegwear.setRotationPoint(-3.0F, 0.0F + yOffset, 0.0F);

        }

        this.bipedLeftLeg.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch);
        if (this.bipedLeftLegwear != null) {
            this.bipedLeftLegwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch + 0.25f);
        }
        this.unicornarm.addBox(-2.0F + FIRSTP_ARM_CENTRE_X, -6.0F + FIRSTP_ARM_CENTRE_Y, -2.0F + FIRSTP_ARM_CENTRE_Z, 4, 12, 4, stretch + .25f);
        this.unicornarm.setRotationPoint(-5.0F, 2.0F + yOffset, 0.0F);
    }

    protected void initTailPositions(float yOffset, float stretch) {
        this.Tail[0].addTopPlane(-2.0F, 1.0F, 2.0F, 4, 0, 4, stretch);
        this.Tail[0].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[1].addSidePlane(-2.0F, 1.0F, 2.0F, 0, 4, 4, stretch);
        this.Tail[1].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[2].addBackPlane(-2.0F, 1.0F, 2.0F, 4, 4, 0, stretch);
        this.Tail[2].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[3].addSidePlane(2.0F, 1.0F, 2.0F, 0, 4, 4, stretch);
        this.Tail[3].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[4].addBackPlane(-2.0F, 1.0F, 6.0F, 4, 4, 0, stretch);
        this.Tail[4].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[5].addTopPlane(-2.0F, 5.0F, 2.0F, 4, 0, 4, stretch);
        this.Tail[5].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[6].addSidePlane(-2.0F, 5.0F, 2.0F, 0, 4, 4, stretch);
        this.Tail[6].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[7].addBackPlane(-2.0F, 5.0F, 2.0F, 4, 4, 0, stretch);
        this.Tail[7].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[8].addSidePlane(2.0F, 5.0F, 2.0F, 0, 4, 4, stretch);
        this.Tail[8].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[9].addBackPlane(-2.0F, 5.0F, 6.0F, 4, 4, 0, stretch);
        this.Tail[9].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[10].addTopPlane(-2.0F, 9.0F, 2.0F, 4, 0, 4, stretch);
        this.Tail[10].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[11].addSidePlane(-2.0F, 9.0F, 2.0F, 0, 4, 4, stretch);
        this.Tail[11].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[12].addBackPlane(-2.0F, 9.0F, 2.0F, 4, 4, 0, stretch);
        this.Tail[12].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[13].addSidePlane(2.0F, 9.0F, 2.0F, 0, 4, 4, stretch);
        this.Tail[13].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[14].addBackPlane(-2.0F, 9.0F, 6.0F, 4, 4, 0, stretch);
        this.Tail[14].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[15].addTopPlane(-2.0F, 13.0F, 2.0F, 4, 0, 4, stretch);
        this.Tail[15].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[16].addSidePlane(-2.0F, 13.0F, 2.0F, 0, 4, 4, stretch);
        this.Tail[16].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[17].addBackPlane(-2.0F, 13.0F, 2.0F, 4, 4, 0, stretch);
        this.Tail[17].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[18].addSidePlane(2.0F, 13.0F, 2.0F, 0, 4, 4, stretch);
        this.Tail[18].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[19].addBackPlane(-2.0F, 13.0F, 6.0F, 4, 4, 0, stretch);
        this.Tail[19].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
        this.Tail[20].addTopPlane(-2.0F, 17.0F, 2.0F, 4, 0, 4, stretch);
        this.Tail[20].setRotationPoint(TAIL_RP_X, TAIL_RP_Y + yOffset, TAIL_RP_Z);
    }

    @Override
    public void renderCape(float scale) {
        this.bipedCape.render(scale);
    }

}
