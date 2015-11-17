package com.brohoof.minelittlepony.model.pony;

import static net.minecraft.client.renderer.GlStateManager.blendFunc;
import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.popAttrib;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.rotate;
import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.model.ModelPony;
import com.brohoof.minelittlepony.renderer.AniParams;
import com.brohoof.minelittlepony.renderer.CompressiveRendering;
import com.brohoof.minelittlepony.renderer.HornGlowRenderer;
import com.brohoof.minelittlepony.renderer.PlaneRenderer;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

public class pm_newPonyAdv extends ModelPony {

    protected static final float HEAD_CENTRE_X = 0.0F;
    protected static final float HEAD_CENTRE_Y = -1.0F;
    protected static final float HEAD_CENTRE_Z = -2.0F;
    protected static final float BODY_CENTRE_X = 0.0F;
    protected static final float BODY_CENTRE_Y = 8.0F;
    protected static final float BODY_CENTRE_Z = 6.0F;
    protected static final float THIRDP_ARM_CENTRE_X = 0.0F;
    protected static final float THIRDP_ARM_CENTRE_Y = 10.0F;
    protected static final float THIRDP_ARM_CENTRE_Z = 0.0F;
    protected static final float FIRSTP_ARM_CENTRE_X = -1.0F;
    protected static final float FIRSTP_ARM_CENTRE_Y = 4.0F;
    protected static final float FIRSTP_ARM_CENTRE_Z = 0.0F;
    protected static final float HEAD_RP_X = 0.0F;
    protected static final float HEAD_RP_Y = 0.0F;
    protected static final float HEAD_RP_Z = 0.0F;
    protected static final float BODY_RP_Y_SNEAK = 7.0F;
    protected static final float BODY_RP_Y_NOTSNEAK = 0.0F;
    protected static final float BODY_RP_Z_SNEAK = -4.0F;
    protected static final float BODY_RP_Z_NOTSNEAK = 0.0F;
    protected static final float FRONT_LEG_RP_Y_SNEAK = 7.0F;
    protected static final float FRONT_LEG_RP_Y_NOTSNEAK = 8.0F;
    protected static final float WING_FOLDED_RP_Y = 13.0F;
    protected static final float WING_FOLDED_RP_Z = -3.0F;
    protected static final float LEFT_WING_RP_Y_SNEAK = 10.5F;
    protected static final float LEFT_WING_RP_Y_NOTSNEAK = 5.5F;
    protected static final float LEFT_WING_RP_Z_SNEAK = 2.0F;
    protected static final float LEFT_WING_RP_Z_NOTSNEAK = 3.0F;
    protected static final float RIGHT_WING_RP_Y_SNEAK = 11.5F;
    protected static final float RIGHT_WING_RP_Y_NOTSNEAK = 6.5F;
    protected static final float RIGHT_WING_RP_Z_SNEAK = 2.0F;
    protected static final float RIGHT_WING_RP_Z_NOTSNEAK = 3.0F;
    protected static final float TAIL_RP_X = 0.0F;
    protected static final float TAIL_RP_Y = 0.8F;
    protected static final float TAIL_RP_Z = 0.0F;
    protected static final float TAIL_RP_Z_SNEAK = 10.0F;
    protected static final float TAIL_RP_Z_NOTSNEAK = 14.0F;
    protected static final float LEFT_WING_EXT_RP_X = 4.5F;
    protected static final float LEFT_WING_EXT_RP_Y = 5.0F;
    protected static final float LEFT_WING_EXT_RP_Z = 6.0F;
    protected static final float RIGHT_WING_EXT_RP_X = -4.5F;
    protected static final float RIGHT_WING_EXT_RP_Y = 5.0F;
    protected static final float RIGHT_WING_EXT_RP_Z = 6.0F;
    protected static final float BODY_ROTATE_ANGLE_X_SNEAK = 0.4F;
    protected static final float BODY_ROTATE_ANGLE_X_NOTSNEAK = 0.0F;
    protected static final float EXT_WING_ROTATE_ANGLE_X = 2.5F;
    protected static final float LEFT_WING_ROTATE_ANGLE_Z_SNEAK = -6.0F;
    protected static final float RIGHT_WING_ROTATE_ANGLE_Z_SNEAK = 6.0F;
    protected static final float SNEAK_LEG_X_ROTATION_ADJUSTMENT = 0.4F;
    protected static final float ROTATE_270 = 4.712F;
    protected static final float ROTATE_90 = 1.571F;
    protected static final float RIDING_SHIFT_Y = -10.0F;
    protected static final float RIDING_SHIFT_Z = -10.0F;

    protected boolean rainboom;
    protected final float Pi = 3.1415927F;
    private float WingRotateAngleZ;
    protected float NeckRotX = 0.166F;
    public int tailstop = 0;

    public ModelRenderer bipedCape;
    public ModelRenderer[] headpiece;// horn? ears?
    public HornGlowRenderer[] hornglow;
    public PlaneRenderer[] Bodypiece;
    public PlaneRenderer[] VillagerBagPiece;
    public PlaneRenderer VillagerApron;
    public PlaneRenderer VillagerTrinket;
    public PlaneRenderer[] BodypieceNeck;
    public PlaneRenderer[] MuzzleFemale;
    public PlaneRenderer[] MuzzleMale;
    public ModelRenderer SteveArm;
    public ModelRenderer unicornarm;
    public PlaneRenderer[] Tail;
    public ModelRenderer[] LeftWing;
    public ModelRenderer[] RightWing;
    public ModelRenderer[] LeftWingExt;
    public ModelRenderer[] RightWingExt;
    public CompressiveRendering CompressiveLeftWing;
    public CompressiveRendering CompressiveRightWing;

    public pm_newPonyAdv(String texture) {
        super(texture);
    }

    @Override
    public void init(float yOffset, float stretch) {
        this.initTextures();
        this.initPositions(yOffset, stretch);
    }

    @Override
    public void animate(AniParams aniparams) {
        this.checkRainboom(aniparams.swing);
        this.rotateHead(aniparams.horz, aniparams.vert);
        this.swingTailZ(aniparams.move, aniparams.swing);
        float bodySwingRotation = 0.0F;
        if (this.swingProgress > -9990.0F && (!this.isUnicorn || this.glowColor == 0)) {
            bodySwingRotation = MathHelper.sin(MathHelper.sqrt_float(this.swingProgress) * 3.1415927F * 2.0F) * 0.2F;
        }

        this.bipedBody.rotateAngleY = bodySwingRotation * 0.2F;

        int k1;
        for (k1 = 0; k1 < this.Bodypiece.length; ++k1) {
            this.Bodypiece[k1].rotateAngleY = bodySwingRotation * 0.2F;
        }

        for (k1 = 0; k1 < this.VillagerBagPiece.length; ++k1) {
            this.VillagerBagPiece[k1].rotateAngleY = bodySwingRotation * 0.2F;
        }

        this.VillagerBagPiece[4].rotateAngleY += 4.712389F;
        this.VillagerBagPiece[5].rotateAngleY += 4.712389F;
        this.VillagerBagPiece[6].rotateAngleY += 4.712389F;
        this.VillagerBagPiece[7].rotateAngleY += 4.712389F;
        this.VillagerApron.rotateAngleY = bodySwingRotation * 0.2F;
        this.VillagerTrinket.rotateAngleY = bodySwingRotation * 0.2F;

        for (k1 = 0; k1 < this.BodypieceNeck.length; ++k1) {
            this.BodypieceNeck[k1].rotateAngleY = bodySwingRotation * 0.2F;
        }

        for (k1 = 0; k1 < this.LeftWing.length; ++k1) {
            this.LeftWing[k1].rotateAngleY = bodySwingRotation * 0.2F;
        }

        for (k1 = 0; k1 < this.RightWing.length; ++k1) {
            this.RightWing[k1].rotateAngleY = bodySwingRotation * 0.2F;
        }

        this.tailstop = 0;
        this.tailstop = this.Tail.length - this.wantTail * 5;
        if (this.tailstop <= 1) {
            this.tailstop = 0;
        }

        for (k1 = 0; k1 < this.tailstop; ++k1) {
            this.Tail[k1].rotateAngleY = bodySwingRotation;
        }

        this.setLegs(aniparams.move, aniparams.swing, aniparams.tick);
        this.holdItem();
        this.swingItem(this.swingProgress);
        if (this.issneak && !this.isFlying) {
            this.adjustBody(BODY_ROTATE_ANGLE_X_SNEAK, BODY_RP_Y_SNEAK, BODY_RP_Z_SNEAK);
            this.animatePegasusWingsSneaking();
            this.sneakLegs();
            this.setHead(0.0F, 6.0F, -2.0F);
            this.sneakTail();
        } else {
            this.adjustBody(BODY_ROTATE_ANGLE_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK, BODY_RP_Z_NOTSNEAK);
            if (this.isPegasus) {
                this.animatePegasusWingsNotSneaking(aniparams.tick);
            }

            this.bipedRightLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.bipedLeftLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.swingArms(aniparams.tick);
            this.setHead(0.0F, 0.0F, 0.0F);
            this.tailstop = 0;
            this.tailstop = this.Tail.length - this.wantTail * 5;
            if (this.tailstop <= 1) {
                this.tailstop = 0;
            }

            for (k1 = 0; k1 < this.tailstop; ++k1) {
                this.setRotationPoint(this.Tail[k1], TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_NOTSNEAK);
                if (this.rainboom) {
                    this.Tail[k1].rotateAngleX = ROTATE_90 + 0.1F * MathHelper.sin(aniparams.move);
                } else {
                    this.Tail[k1].rotateAngleX = 0.5F * aniparams.swing;
                }
            }

            if (!this.rainboom) {
                this.swingTailX(aniparams.tick);
            }
        }

        if (this.rainboom) {
            this.tailstop = 0;
            this.tailstop = this.Tail.length - this.wantTail * 5;
            if (this.tailstop <= 1) {
                this.tailstop = 0;
            }

            for (k1 = 0; k1 < this.tailstop; ++k1) {
                this.Tail[k1].rotationPointY += 6.0F;
                ++this.Tail[k1].rotationPointZ;
            }
        }

        if (this.isSleeping) {
            this.ponySleep();
        }

        if (this.aimedBow) {
            this.aimBow(aniparams.tick);
        }

        this.fixSpecialRotations();
        this.fixSpecialRotationPoints(aniparams.move);

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
        if (this.isPegasus && this.isFlying && swing >= 0.9999F) {
            this.rainboom = true;
        } else {
            this.rainboom = false;
        }

    }

    protected void setHead(float posX, float posY, float posZ) {
        this.setRotationPoint(this.bipedHead, posX, posY, posZ);
        this.setRotationPoint(this.bipedHeadwear, posX, posY, posZ);

        int j6;
        for (j6 = 0; j6 < this.headpiece.length; ++j6) {
            this.setRotationPoint(this.headpiece[j6], posX, posY, posZ);
        }

        for (j6 = 0; j6 < this.hornglow.length; ++j6) {
            this.setRotationPoint(this.hornglow[j6], posX, posY, posZ);
        }

        if (this.isMale) {
            for (j6 = 0; j6 < this.MuzzleMale.length; ++j6) {
                this.setRotationPoint(this.MuzzleMale[j6], posX, posY, posZ);
            }
        } else {
            for (j6 = 0; j6 < this.MuzzleFemale.length; ++j6) {
                this.setRotationPoint(this.MuzzleFemale[j6], posX, posY, posZ);
            }
        }

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

        if (headRotateAngleX > 0.5F) {
            headRotateAngleX = 0.5F;
        }

        if (headRotateAngleX < -0.5F) {
            headRotateAngleX = -0.5F;
        }

        this.bipedHead.rotateAngleY = headRotateAngleY;
        this.bipedHead.rotateAngleX = headRotateAngleX;
        int i;
        if (this.isMale) {
            for (i = 0; i < this.MuzzleMale.length; ++i) {
                this.MuzzleMale[i].rotateAngleY = headRotateAngleY;
                this.MuzzleMale[i].rotateAngleX = headRotateAngleX;
            }
        } else {
            for (i = 0; i < this.MuzzleFemale.length; ++i) {
                this.MuzzleFemale[i].rotateAngleY = headRotateAngleY;
                this.MuzzleFemale[i].rotateAngleX = headRotateAngleX;
            }
        }

        this.headpiece[0].rotateAngleY = headRotateAngleY;
        this.headpiece[0].rotateAngleX = headRotateAngleX;
        this.headpiece[1].rotateAngleY = headRotateAngleY;
        this.headpiece[1].rotateAngleX = headRotateAngleX;
        this.headpiece[2].rotateAngleY = headRotateAngleY;
        this.headpiece[2].rotateAngleX = headRotateAngleX;
        this.hornglow[0].rotateAngleY = headRotateAngleY;
        this.hornglow[0].rotateAngleX = headRotateAngleX;
        this.hornglow[1].rotateAngleY = headRotateAngleY;
        this.hornglow[1].rotateAngleX = headRotateAngleX;
        this.bipedHeadwear.rotateAngleY = headRotateAngleY;
        this.bipedHeadwear.rotateAngleX = headRotateAngleX;
        this.headpiece[2].rotateAngleX = headRotateAngleX + 0.5F;
        this.hornglow[0].rotateAngleX = headRotateAngleX + 0.5F;
        this.hornglow[1].rotateAngleX = headRotateAngleX + 0.5F;
    }

    protected void setLegs(float move, float swing, float tick) {
        this.rotateLegs(move, swing, tick);
        this.adjustLegs();
    }

    /**
     * @param tick
     */
    protected void rotateLegs(float move, float swing, float tick) {
        float rightArmRotateAngleX;
        float leftArmRotateAngleX;
        float rightLegRotateAngleX;
        float leftLegRotateAngleX;
        if (this.isFlying && this.isPegasus) {
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

            this.SteveArm.rotateAngleY = 0.2F;
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
            this.SteveArm.rotateAngleY = 0.0F;
            this.unicornarm.rotateAngleY = 0.0F;

            this.bipedRightArm.rotateAngleY = 0.0F;
            this.bipedLeftArm.rotateAngleY = 0.0F;
            this.bipedRightLeg.rotateAngleY = 0.0F;
            this.bipedLeftLeg.rotateAngleY = 0.0F;
        }

        this.bipedRightArm.rotateAngleX = rightArmRotateAngleX;
        this.SteveArm.rotateAngleX = rightArmRotateAngleX;
        this.unicornarm.rotateAngleX = 0.0F;

        this.bipedLeftArm.rotateAngleX = leftArmRotateAngleX;
        this.bipedRightLeg.rotateAngleX = rightLegRotateAngleX;
        this.bipedLeftLeg.rotateAngleX = leftLegRotateAngleX;
        this.bipedRightArm.rotateAngleZ = 0.0F;

        this.SteveArm.rotateAngleZ = 0.0F;
        this.unicornarm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
    }

    protected void adjustLegs() {
        float sinBodyRotateAngleYFactor = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
        float cosBodyRotateAngleYFactor = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
        float legOutset = 4.0F;
        if (this.issneak && !this.isFlying) {
            legOutset = 0.0F;
        }

        if (this.isSleeping) {
            legOutset = 2.6F;
        }

        if (this.rainboom) {
            this.bipedRightArm.rotationPointZ = sinBodyRotateAngleYFactor +
                    2.0F;
            this.SteveArm.rotationPointZ = sinBodyRotateAngleYFactor + 2.0F;
            this.bipedLeftArm.rotationPointZ = 0.0F - sinBodyRotateAngleYFactor + 2.0F;
        } else {
            this.bipedRightArm.rotationPointZ = sinBodyRotateAngleYFactor + 1.0F;
            this.SteveArm.rotationPointZ = sinBodyRotateAngleYFactor + 1.0F;
            this.bipedLeftArm.rotationPointZ = 0.0F - sinBodyRotateAngleYFactor + 1.0F;
        }
        this.SteveArm.rotationPointX = 0.0F - cosBodyRotateAngleYFactor;

        this.bipedRightArm.rotationPointX = 0.0F - cosBodyRotateAngleYFactor - 1.0F + legOutset;
        this.bipedLeftArm.rotationPointX = cosBodyRotateAngleYFactor + 1.0F - legOutset;
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
        this.tailstop = 0;
        this.tailstop = this.Tail.length - this.wantTail * 5;
        if (this.tailstop <= 1) {
            this.tailstop = 0;
        }

        for (int j = 0; j < this.tailstop; ++j) {
            if (this.rainboom) {
                this.Tail[j].rotateAngleZ = 0.0F;
            } else {
                this.Tail[j].rotateAngleZ = MathHelper.cos(move * 0.8F) * 0.2F * swing;
            }
        }

    }

    protected void swingTailX(float tick) {
        float sinTickFactor = MathHelper.sin(tick * 0.067F) * 0.05F;
        this.tailstop = 0;
        this.tailstop = this.Tail.length - this.wantTail * 5;
        if (this.tailstop <= 1) {
            this.tailstop = 0;
        }

        for (int l6 = 0; l6 < this.tailstop; ++l6) {
            this.Tail[l6].rotateAngleX += sinTickFactor;
        }

    }

    protected void holdItem() {
        if (this.heldItemRight != 0 && !this.rainboom && (!this.isUnicorn || this.glowColor == 0)) {
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.3141593F;
            this.SteveArm.rotateAngleX = this.SteveArm.rotateAngleX * 0.5F - 0.3141593F;
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
            if (this.isUnicorn && this.glowColor != 0 && this.heldItemRight != 0) {
                this.unicornarm.rotateAngleX = (float) (this.unicornarm.rotateAngleX
                        - (f22 * 1.2D + f33));
                this.unicornarm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
                this.unicornarm.rotateAngleZ = f28 * -0.4F;
            } else {
                this.bipedRightArm.rotateAngleX = (float) (this.bipedRightArm.rotateAngleX
                        - (f22 * 1.2D + f33));
                this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
                this.bipedRightArm.rotateAngleZ = f28 * -0.4F;
                this.SteveArm.rotateAngleX = (float) (this.SteveArm.rotateAngleX
                        - (f22 * 1.2D + f33));
                this.SteveArm.rotateAngleY += this.bipedBody.rotateAngleY *
                        2.0F;
                this.SteveArm.rotateAngleZ = f28 * -0.4F;
            }
        }

    }

    protected void swingArms(float tick) {
        if (this.heldItemRight != 0 && !this.isSleeping) {
            float cosTickFactor = MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
            float sinTickFactor = MathHelper.sin(tick * 0.067F) * 0.05F;
            if (this.isUnicorn && this.glowColor != 0) {
                this.unicornarm.rotateAngleZ += cosTickFactor;
                this.unicornarm.rotateAngleX += sinTickFactor;
            } else {
                this.bipedRightArm.rotateAngleZ += cosTickFactor;
                this.bipedRightArm.rotateAngleX += sinTickFactor;
                this.SteveArm.rotateAngleZ += cosTickFactor;
                this.SteveArm.rotateAngleX += sinTickFactor;
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

        for (k3 = 0; k3 < this.VillagerBagPiece.length; ++k3) {
            this.VillagerBagPiece[k3].rotateAngleX = rotateAngleX;
            this.VillagerBagPiece[k3].rotationPointY = rotationPointY;
            this.VillagerBagPiece[k3].rotationPointZ = rotationPointZ;
        }

        this.VillagerApron.rotateAngleX = rotateAngleX;
        this.VillagerApron.rotationPointY = rotationPointY;
        this.VillagerApron.rotationPointZ = rotationPointZ;
        this.VillagerTrinket.rotateAngleX = rotateAngleX;
        this.VillagerTrinket.rotationPointY = rotationPointY;
        this.VillagerTrinket.rotationPointZ = rotationPointZ;
    }

    protected void adjustNeck(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        for (int k3 = 0; k3 < this.BodypieceNeck.length; ++k3) {
            this.BodypieceNeck[k3].rotateAngleX = this.NeckRotX + rotateAngleX;
            this.BodypieceNeck[k3].rotationPointY = rotationPointY;
            this.BodypieceNeck[k3].rotationPointZ = rotationPointZ;
        }

    }

    protected void sneakLegs() {
        this.SteveArm.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        this.unicornarm.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;

        this.bipedRightArm.rotateAngleX -= SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        this.bipedLeftArm.rotateAngleX -= SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        this.bipedRightLeg.rotationPointY = FRONT_LEG_RP_Y_SNEAK;
        this.bipedLeftLeg.rotationPointY = FRONT_LEG_RP_Y_SNEAK;

    }

    protected void sneakTail() {
        this.tailstop = 0;
        this.tailstop = this.Tail.length - this.wantTail * 5;
        if (this.tailstop <= 1) {
            this.tailstop = 0;
        }

        for (int i7 = 0; i7 < this.tailstop; ++i7) {
            this.setRotationPoint(this.Tail[i7], TAIL_RP_X, TAIL_RP_Y, TAIL_RP_Z_SNEAK);
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
        if (this.issneak) {
            headPosY = 2.0F;
            headPosZ = -1.0F;
            headPosX = 1.0F;
        } else {
            headPosY = 2.0F;
            headPosZ = 1.0F;
            headPosX = 1.0F;
        }

        this.setHead(headPosX, headPosY, headPosZ);
        this.shiftRotationPoint(this.bipedRightArm, 0.0F, 2.0F, 6.0F);
        this.shiftRotationPoint(this.bipedLeftArm, 0.0F, 2.0F, 6.0F);
        this.shiftRotationPoint(this.bipedRightLeg, 0.0F, 2.0F, -8.0F);
        this.shiftRotationPoint(this.bipedLeftLeg, 0.0F, 2.0F, -8.0F);
    }

    protected void aimBow(float tick) {
        if (this.isUnicorn && this.glowColor != 0) {
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
        this.shiftRotationPoint(this.bipedRightArm, 0.0F, 0.0F, 1.0F);

        this.bipedRightArmwear.rotateAngleZ = 0.0F;
        this.bipedRightArmwear.rotateAngleY = -0.06F + this.bipedHead.rotateAngleY;
        this.bipedRightArmwear.rotateAngleX = ROTATE_270 + this.bipedHead.rotateAngleX;
        this.bipedRightArmwear.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArmwear.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.05F;
        this.shiftRotationPoint(this.bipedRightArmwear, 0.0F, 0.0F, 1.0F);
    }

    protected void aimBowUnicorn(float tick) {
        this.unicornarm.rotateAngleZ = 0.0F;
        this.unicornarm.rotateAngleY = -0.06F + this.bipedHead.rotateAngleY;
        this.unicornarm.rotateAngleX = ROTATE_270 + this.bipedHead.rotateAngleX;
        this.unicornarm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        this.unicornarm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.05F;
    }

    protected void animatePegasusWingsSneaking() {
        int k5;
        for (k5 = 0; k5 < this.LeftWingExt.length; ++k5) {
            this.LeftWingExt[k5].rotationPointY = LEFT_WING_RP_Y_SNEAK;
            this.LeftWingExt[k5].rotationPointZ = LEFT_WING_RP_Z_SNEAK;
            this.LeftWingExt[k5].rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
            this.LeftWingExt[k5].rotateAngleZ = LEFT_WING_ROTATE_ANGLE_Z_SNEAK;
        }

        for (k5 = 0; k5 < this.LeftWingExt.length; ++k5) {
            this.RightWingExt[k5].rotationPointY = RIGHT_WING_RP_Y_SNEAK;
            this.RightWingExt[k5].rotationPointZ = RIGHT_WING_RP_Z_SNEAK;
            this.RightWingExt[k5].rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
            this.RightWingExt[k5].rotateAngleZ = RIGHT_WING_ROTATE_ANGLE_Z_SNEAK;
        }

    }

    protected void animatePegasusWingsNotSneaking(float tick) {
        int l5;
        if (!this.isFlying) {
            for (l5 = 0; l5 < this.LeftWing.length; ++l5) {
                this.LeftWing[l5].rotationPointY = WING_FOLDED_RP_Y;
                this.LeftWing[l5].rotationPointZ = WING_FOLDED_RP_Z;
            }

            for (l5 = 0; l5 < this.RightWing.length; ++l5) {
                this.RightWing[l5].rotationPointY = WING_FOLDED_RP_Y;
                this.RightWing[l5].rotationPointZ = WING_FOLDED_RP_Z;
            }
        } else {
            this.WingRotateAngleZ = MathHelper.sin(tick * 0.536F) * 1.0F;

            for (l5 = 0; l5 < this.LeftWingExt.length; ++l5) {
                this.LeftWingExt[l5].rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
                this.LeftWingExt[l5].rotateAngleZ = -this.WingRotateAngleZ - ROTATE_270 - 0.4F;
                this.LeftWingExt[l5].rotationPointY = LEFT_WING_RP_Y_NOTSNEAK;
                this.LeftWingExt[l5].rotationPointZ = LEFT_WING_RP_Z_NOTSNEAK;
            }

            for (l5 = 0; l5 < this.RightWingExt.length; ++l5) {
                this.RightWingExt[l5].rotateAngleX = EXT_WING_ROTATE_ANGLE_X;
                this.RightWingExt[l5].rotateAngleZ = this.WingRotateAngleZ + ROTATE_270 + 0.4F;
                this.RightWingExt[l5].rotationPointY = RIGHT_WING_RP_Y_NOTSNEAK;
                this.RightWingExt[l5].rotationPointZ = RIGHT_WING_RP_Z_NOTSNEAK;
            }
        }

    }

    protected void fixSpecialRotations() {
        this.LeftWingExt[1].rotateAngleX -= 0.85F;
        this.LeftWingExt[2].rotateAngleX -= 0.75F;
        this.LeftWingExt[3].rotateAngleX -= 0.5F;
        this.LeftWingExt[5].rotateAngleX -= 0.85F;
        this.RightWingExt[1].rotateAngleX -= 0.85F;
        this.RightWingExt[2].rotateAngleX -= 0.75F;
        this.RightWingExt[3].rotateAngleX -= 0.5F;
        this.RightWingExt[5].rotateAngleX -= 0.85F;
        this.Bodypiece[9].rotateAngleX += 0.5F;
        this.Bodypiece[10].rotateAngleX += 0.5F;
        this.Bodypiece[11].rotateAngleX += 0.5F;
        this.Bodypiece[12].rotateAngleX += 0.5F;
        this.Bodypiece[13].rotateAngleX += 0.5F;
    }

    /**
     * @param move
     */
    protected void fixSpecialRotationPoints(float move) {}

    public void shiftRotationPoint(PlaneRenderer aPlaneRenderer, float shiftX, float shiftY, float shiftZ) {
        aPlaneRenderer.rotationPointX += shiftX;
        aPlaneRenderer.rotationPointY += shiftY;
        aPlaneRenderer.rotationPointZ += shiftZ;
    }

    public void shiftRotationPoint(ModelRenderer aRenderer, float shiftX, float shiftY, float shiftZ) {
        aRenderer.rotationPointX += shiftX;
        aRenderer.rotationPointY += shiftY;
        aRenderer.rotationPointZ += shiftZ;
    }

    public void setRotationPoint(HornGlowRenderer aRenderer, float setX, float setY, float setZ) {
        aRenderer.rotationPointX = setX;
        aRenderer.rotationPointY = setY;
        aRenderer.rotationPointZ = setZ;
    }

    public void setRotationPoint(PlaneRenderer aPlaneRenderer, float setX, float setY, float setZ) {
        aPlaneRenderer.rotationPointX = setX;
        aPlaneRenderer.rotationPointY = setY;
        aPlaneRenderer.rotationPointZ = setZ;
    }

    public void setRotationPoint(ModelRenderer aRenderer, float setX, float setY, float setZ) {
        aRenderer.rotationPointX = setX;
        aRenderer.rotationPointY = setY;
        aRenderer.rotationPointZ = setZ;
    }

    @Override
    public void render(AniParams aniparams) {
        if (this.isRiding && !this.isArmour) {
            translate(0.0F, -0.56F, -0.46F);
        }

        if (this.isSleeping && !this.isArmour) {
            rotate(90.0F, 0.0F, 1.0F, 0.0F);
            rotate(270.0F, 0.0F, 0.0F, 1.0F);
            rotate(90.0F, 0.0F, 1.0F, 0.0F);
            rotate(180.0F, 0.0F, 0.0F, 1.0F);
            rotate(180.0F, 0.0F, 1.0F, 0.0F);
        }

        if (this.size == 0) {
            if (this.issneak && !this.isFlying && !this.isArmour) {
                translate(0.0F, -0.12F, 0.0F);
            }

            if (this.isSleeping && !this.isArmour) {
                translate(0.0F, -1.0F, 0.25F);
            }

            pushMatrix();
            translate(0.0F, 0.76F, 0.0F);
            scale(0.9F, 0.9F, 0.9F);
            this.renderHead();
            if (this.issneak && !this.isFlying) {
                translate(0.0F, -0.01F, 0.15F);
            }

            this.renderNeck();
            popMatrix();

            pushMatrix();
            translate(0.0F, 0.76F, -0.04F);
            scale(0.6F, 0.6F, 0.6F);
            this.renderBody();
            this.renderTail();
            popMatrix();

            pushMatrix();
            translate(0.0F, 0.89F, 0.0F);
            scale(0.6F, 0.41F, 0.6F);
            if (this.issneak && !this.isFlying) {
                translate(0.0F, 0.12F, 0.0F);
            }

            if (this.rainboom) {
                translate(0.0F, -0.08F, 0.0F);
            }

            this.renderLegs();
            popMatrix();

        } else if (this.size == 2) {
            if (this.isSleeping && !this.isArmour) {
                translate(0.0F, -0.47F, 0.2F);
            }

            pushMatrix();
            translate(0.0F, -0.17F, -0.04F);
            if (this.isSleeping && !this.isArmour) {
                translate(0.0F, 0.0F, -0.1F);
            }

            if (this.issneak && !this.isFlying) {
                translate(0.0F, 0.15F, 0.0F);
            }

            this.renderHead();
            popMatrix();
            pushMatrix();
            translate(0.0F, -0.15F, -0.07F);
            if (this.issneak && !this.isFlying) {
                translate(0.0F, 0.0F, -0.05F);
            }

            this.renderNeck();
            popMatrix();
            pushMatrix();
            translate(0.0F, -0.2F, -0.04F);
            scale(1.15F, 1.2F, 1.2F);
            this.renderBody();
            popMatrix();
            pushMatrix();
            translate(0.0F, -0.2F, 0.08F);
            this.renderTail();
            popMatrix();
            pushMatrix();
            translate(0.0F, -0.14F, 0.0F);
            scale(1.15F, 1.12F, 1.15F);
            this.renderLegs();
            popMatrix();
        } else if (this.size == 3) {
            if (this.isSleeping && !this.isArmour) {
                translate(0.0F, -0.43F, 0.25F);
            }

            pushMatrix();
            translate(0.0F, -0.15F, 0.01F);
            if (this.issneak && !this.isFlying) {
                translate(0.0F, 0.05F, 0.0F);
            }

            this.renderHead();
            popMatrix();
            pushMatrix();
            translate(0.0F, -0.19F, -0.01F);
            scale(1.0F, 1.1F, 1.0F);
            if (this.issneak && !this.isFlying) {
                translate(0.0F, -0.06F, -0.04F);
            }

            this.renderNeck();
            popMatrix();
            pushMatrix();
            translate(0.0F, -0.1F, 0.0F);
            scale(1.0F, 1.0F, 1.0F);
            this.renderBody();
            this.renderTail();
            popMatrix();
            pushMatrix();
            translate(0.0F, -0.25F, 0.03F);
            scale(1.0F, 1.18F, 1.0F);
            if (this.rainboom) {
                translate(0.0F, 0.05F, 0.0F);
            }

            this.renderLegs();
            popMatrix();
        } else {
            if (this.isSleeping && !this.isArmour) {
                translate(0.0F, -0.535F, 0.25F);
            }

            this.renderHead();
            this.renderNeck();
            this.renderBody();
            this.renderTail();
            this.renderLegs();
        }

    }

    protected void renderHead() {
        this.bipedHead.render(this.scale);
        this.headpiece[0].render(this.scale);
        this.headpiece[1].render(this.scale);
        if (PonyManager.getInstance().getShowSnuzzles() == 1) {
            int red;
            if (this.isMale) {
                for (red = 0; red < this.MuzzleMale.length; ++red) {
                    this.MuzzleMale[red].render(this.scale);
                }
            } else {
                for (red = 0; red < this.MuzzleFemale.length; ++red) {
                    this.MuzzleFemale[red].render(this.scale);
                }
            }
        }

        this.bipedHeadwear.render(this.scale);
        if (this.isUnicorn) {
            this.headpiece[2].render(this.scale);
            if (this.heldItemRight != 0 && this.glowColor != 0) {
                GL11.glPushAttrib(24577);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                float var4 = (this.glowColor >> 16 & 255) / 255.0F;
                float green = (this.glowColor >> 8 & 255) / 255.0F;
                float blue = (this.glowColor & 255) / 255.0F;
                blendFunc(GL11.GL_SRC_ALPHA, 1);
                color(var4, green, blue, 0.4F);
                this.hornglow[0].render(this.scale);
                color(var4, green, blue, 0.2F);
                this.hornglow[1].render(this.scale);
                popAttrib();
            }
        }

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
        int k1;
        for (k1 = 0; k1 < this.Bodypiece.length; ++k1) {
            this.Bodypiece[k1].render(this.scale);
        }

        if (this.isVillager) {
            if (this.villagerProfession < 2) {
                for (k1 = 0; k1 < this.VillagerBagPiece.length; ++k1) {
                    this.VillagerBagPiece[k1].render(this.scale);
                }
            } else if (this.villagerProfession == 2) {
                this.VillagerTrinket.render(this.scale);
            } else if (this.villagerProfession > 2) {
                this.VillagerApron.render(this.scale);
            }
        }

        if (this.isPegasus) {
            if (!this.isFlying && !this.issneak) {
                this.setExtendingWings(true);

                for (k1 = 0; k1 < this.LeftWing.length; ++k1) {
                    this.LeftWing[k1].render(this.scale);
                }

                for (k1 = 0; k1 < this.RightWing.length; ++k1) {
                    this.RightWing[k1].render(this.scale);
                }
            } else {
                this.setExtendingWings(false);

                for (k1 = 0; k1 < this.LeftWingExt.length; ++k1) {
                    this.LeftWingExt[k1].render(this.scale);
                }

                for (k1 = 0; k1 < this.RightWingExt.length; ++k1) {
                    this.RightWingExt[k1].render(this.scale);
                }
            }
        }

    }

    protected void renderTail() {
        int var3 = this.Tail.length - this.wantTail * 5;
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

    protected void initTextures() {
        this.Tail = new PlaneRenderer[21];
        this.headpiece = new ModelRenderer[3];
        this.hornglow = new HornGlowRenderer[2];
        this.MuzzleFemale = new PlaneRenderer[10];
        this.MuzzleMale = new PlaneRenderer[5];
        this.Bodypiece = new PlaneRenderer[14];
        this.VillagerBagPiece = new PlaneRenderer[14];
        this.BodypieceNeck = new PlaneRenderer[4];
        this.LeftWing = new ModelRenderer[3];
        this.RightWing = new ModelRenderer[3];
        this.LeftWingExt = new ModelRenderer[6];
        this.RightWingExt = new ModelRenderer[6];
        this.initHeadTextures();
        this.initMuzzleTextures();
        this.initBodyTextures();
        this.initLegTextures();
        this.initTailTextures();
        this.initWingTextures();
    }

    protected void initHeadTextures() {
        this.bipedCape = new ModelRenderer(this, 0, 0).setTextureSize(64, 32);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.headpiece[0] = new ModelRenderer(this, 12, 16);
        this.headpiece[1] = new ModelRenderer(this, 12, 16);
        this.headpiece[1].mirror = true;
        this.headpiece[2] = new ModelRenderer(this, 0, 3);
        this.hornglow[0] = new HornGlowRenderer(this, 0, 3);
        this.hornglow[1] = new HornGlowRenderer(this, 0, 3);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.boxList.remove(this.headpiece[2]);
    }

    protected void initMuzzleTextures() {
        this.MuzzleFemale[0] = new PlaneRenderer(this, 10, 14);
        this.MuzzleFemale[1] = new PlaneRenderer(this, 11, 13);
        this.MuzzleFemale[2] = new PlaneRenderer(this, 9, 14);
        this.MuzzleFemale[3] = new PlaneRenderer(this, 14, 14);
        this.MuzzleFemale[4] = new PlaneRenderer(this, 11, 12);
        this.MuzzleFemale[5] = new PlaneRenderer(this, 18, 7);
        this.MuzzleFemale[6] = new PlaneRenderer(this, 9, 14);
        this.MuzzleFemale[7] = new PlaneRenderer(this, 14, 14);
        this.MuzzleFemale[8] = new PlaneRenderer(this, 11, 12);
        this.MuzzleFemale[9] = new PlaneRenderer(this, 12, 12);
        this.MuzzleMale[0] = new PlaneRenderer(this, 10, 13);
        this.MuzzleMale[1] = new PlaneRenderer(this, 10, 13);
        this.MuzzleMale[2] = new PlaneRenderer(this, 18, 7);
        this.MuzzleMale[3] = new PlaneRenderer(this, 10, 13);
        this.MuzzleMale[4] = new PlaneRenderer(this, 13, 13);
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
        this.BodypieceNeck[0] = new PlaneRenderer(this, 0, 8);
        this.BodypieceNeck[1] = new PlaneRenderer(this, 0, 8);
        this.BodypieceNeck[2] = new PlaneRenderer(this, 0, 8);
        this.BodypieceNeck[3] = new PlaneRenderer(this, 0, 8);

        this.VillagerBagPiece[0] = new PlaneRenderer(this, 56, 19);
        this.VillagerBagPiece[1] = new PlaneRenderer(this, 56, 19);
        this.VillagerBagPiece[2] = new PlaneRenderer(this, 56, 19);
        this.VillagerBagPiece[3] = new PlaneRenderer(this, 56, 19);
        this.VillagerBagPiece[4] = new PlaneRenderer(this, 56, 16);
        this.VillagerBagPiece[5] = new PlaneRenderer(this, 56, 16);
        this.VillagerBagPiece[6] = new PlaneRenderer(this, 56, 22);
        this.VillagerBagPiece[7] = new PlaneRenderer(this, 56, 22);
        this.VillagerBagPiece[8] = new PlaneRenderer(this, 56, 25);
        this.VillagerBagPiece[9] = new PlaneRenderer(this, 56, 25);
        this.VillagerBagPiece[10] = new PlaneRenderer(this, 59, 25);
        this.VillagerBagPiece[11] = new PlaneRenderer(this, 59, 25);
        this.VillagerBagPiece[12] = new PlaneRenderer(this, 56, 31);
        this.VillagerBagPiece[13] = new PlaneRenderer(this, 56, 31);
        this.VillagerApron = new PlaneRenderer(this, 56, 16);
        this.VillagerTrinket = new PlaneRenderer(this, 0, 3);
    }

    protected void initLegTextures() {
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);

        if (this.textureHeight == 64) {
            this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftLeg = new ModelRenderer(this, 16, 48);

            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
            this.bipedRightLegwear = new ModelRenderer(this, 0, 32);

            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
            this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
        } else {
            this.bipedLeftArm = new ModelRenderer(this, 40, 16);
            this.bipedLeftArm.mirror = true;
            this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
            this.bipedLeftLeg.mirror = true;
        }
        this.SteveArm = new ModelRenderer(this, 40, 16).setTextureSize(64, 64);
        this.unicornarm = new ModelRenderer(this, 40, 32).setTextureSize(64, 64);
        this.boxList.remove(this.SteveArm);
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

    protected void initWingTextures() {
        this.LeftWing[0] = new ModelRenderer(this, 56, 16);
        this.LeftWing[0].mirror = true;
        this.LeftWing[1] = new ModelRenderer(this, 56, 16);
        this.LeftWing[1].mirror = true;
        this.LeftWing[2] = new ModelRenderer(this, 56, 16);
        this.LeftWing[2].mirror = true;
        this.RightWing[0] = new ModelRenderer(this, 56, 16);
        this.RightWing[1] = new ModelRenderer(this, 56, 16);
        this.RightWing[2] = new ModelRenderer(this, 56, 16);
        this.LeftWingExt[0] = new ModelRenderer(this, 56, 19);
        this.LeftWingExt[0].mirror = true;
        this.LeftWingExt[1] = new ModelRenderer(this, 56, 19);
        this.LeftWingExt[1].mirror = true;
        this.LeftWingExt[2] = new ModelRenderer(this, 56, 19);
        this.LeftWingExt[2].mirror = true;
        this.LeftWingExt[3] = new ModelRenderer(this, 56, 19);
        this.LeftWingExt[3].mirror = true;
        this.LeftWingExt[4] = new ModelRenderer(this, 56, 19);
        this.LeftWingExt[4].mirror = true;
        this.LeftWingExt[5] = new ModelRenderer(this, 56, 19);
        this.LeftWingExt[5].mirror = true;
        this.RightWingExt[0] = new ModelRenderer(this, 56, 19);
        this.RightWingExt[1] = new ModelRenderer(this, 56, 19);
        this.RightWingExt[2] = new ModelRenderer(this, 56, 19);
        this.RightWingExt[3] = new ModelRenderer(this, 56, 19);
        this.RightWingExt[4] = new ModelRenderer(this, 56, 19);
        this.RightWingExt[5] = new ModelRenderer(this, 56, 19);
        this.compressWings();
    }

    protected void initPositions(float yOffset, float stretch) {
        this.initHeadPositions(yOffset, stretch);
        this.initMuzzlePositions(yOffset, stretch);
        this.initBodyPositions(yOffset, stretch);
        this.initLegPositions(yOffset, stretch);
        this.initTailPositions(yOffset, stretch);
        this.initWingPositions(yOffset, stretch);
    }

    protected void initHeadPositions(float yOffset, float stretch) {
        this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, stretch);
        this.bipedHead.addBox(-4.0F + HEAD_CENTRE_X, -4 + HEAD_CENTRE_Y, -4.0F + HEAD_CENTRE_Z,
                8, 8, 8, stretch);
        this.bipedHead.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.headpiece[0].addBox(-4.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);
        this.headpiece[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.headpiece[1].addBox(2.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);
        this.headpiece[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.headpiece[2].addBox(-0.5F + HEAD_CENTRE_X, -10.0F + HEAD_CENTRE_Y, -1.5F + HEAD_CENTRE_Z, 1, 4, 1,
                stretch);
        this.headpiece[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.hornglow[0].addBox(-0.5F + HEAD_CENTRE_X, -10.0F + HEAD_CENTRE_Y, -1.5F + HEAD_CENTRE_Z, 1, 4, 1,
                stretch + 0.5F);
        this.hornglow[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.hornglow[1].addBox(-0.5F + HEAD_CENTRE_X, -10.0F + HEAD_CENTRE_Y, -1.5F + HEAD_CENTRE_Z, 1, 3, 1,
                stretch + 0.8F);
        this.hornglow[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.bipedHeadwear.addBox(-4.0F + HEAD_CENTRE_X, -4.0F + HEAD_CENTRE_Y, -4.0F + HEAD_CENTRE_Z, 8, 8, 8,
                stretch + 0.5F);
        this.bipedHeadwear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
    }

    protected void initMuzzlePositions(float yOffset, float stretch) {
        this.MuzzleFemale[0].addBackPlane(-2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 4, 2, 0, stretch);
        this.MuzzleFemale[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleFemale[1].addBackPlane(-1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 2, 1, 0, stretch);
        this.MuzzleFemale[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleFemale[2].addTopPlane(-2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 1, 0, 1, stretch);
        this.MuzzleFemale[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleFemale[3].addTopPlane(1.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 1, 0, 1, stretch);
        this.MuzzleFemale[3].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleFemale[4].addTopPlane(-1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 2, 0, 1, stretch);
        this.MuzzleFemale[4].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleFemale[5].addBottomPlane(-2.0F + HEAD_CENTRE_X, 4.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        this.MuzzleFemale[5].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleFemale[6].addSidePlane(-2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 0, 2, 1, stretch);
        this.MuzzleFemale[6].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleFemale[7].addSidePlane(2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 0, 2, 1, stretch);
        this.MuzzleFemale[7].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleFemale[8].addSidePlane(-1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 0, 1, 1, stretch);
        this.MuzzleFemale[8].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleFemale[9].addSidePlane(1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 0, 1, 1, stretch);
        this.MuzzleFemale[9].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleMale[0].addBackPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 4, 3, 0, stretch);
        this.MuzzleMale[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleMale[1].addTopPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        this.MuzzleMale[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleMale[2].addBottomPlane(-2.0F + HEAD_CENTRE_X, 4.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        this.MuzzleMale[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleMale[3].addSidePlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 0, 3, 1, stretch);
        this.MuzzleMale[3].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.MuzzleMale[4].addSidePlane(2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y,
                -5.0F + HEAD_CENTRE_Z, 0, 3, 1, stretch);
        this.MuzzleMale[4].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
    }

    protected void initBodyPositions(float yOffset, float stretch) {
        this.bipedBody.addBox(-4.0F, 4.0F, -2.0F, 8, 8, 4, stretch);
        this.bipedBody.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.bipedBodyWear.addBox(-4.0F, 4.0F, -2.0F, 8, 8, 4, stretch + 0.25F);
        this.bipedBodyWear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        this.Bodypiece[0].addSidePlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 0, 8, 8, stretch);
        this.Bodypiece[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[1].addSidePlane(4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 0, 8, 8, stretch);
        this.Bodypiece[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[2].addTopPlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 8, 0, 12, stretch);
        this.Bodypiece[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[3].addBottomPlane(-4.0F + BODY_CENTRE_X, 4.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 8, 0, 8, stretch);
        this.Bodypiece[3].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[4].addSidePlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y,
                4.0F + BODY_CENTRE_Z, 0, 8, 4, stretch);
        this.Bodypiece[4].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[5].addSidePlane(4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y, 4.0F + BODY_CENTRE_Z,
                0, 8, 4, stretch);
        this.Bodypiece[5].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[6].addBackPlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y,
                8.0F + BODY_CENTRE_Z, 8, 4, 0, stretch);
        this.Bodypiece[6].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[7].addBackPlane(-4.0F + BODY_CENTRE_X, 0.0F + BODY_CENTRE_Y, 8.0F + BODY_CENTRE_Z,
                8, 4, 0, stretch);
        this.Bodypiece[7].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[8].addBottomPlane(-4.0F + BODY_CENTRE_X, 4.0F + BODY_CENTRE_Y,
                4.0F + BODY_CENTRE_Z, 8, 0, 4, stretch);
        this.Bodypiece[8].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[9].addTopPlane(-1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z,
                2, 0, 6, stretch);
        this.Bodypiece[9].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[10].addBottomPlane(-1.0F + BODY_CENTRE_X, 4.0F + BODY_CENTRE_Y,
                2.0F + BODY_CENTRE_Z, 2, 0, 6, stretch);
        this.Bodypiece[10].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[11].addSidePlane(-1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y,
                2.0F + BODY_CENTRE_Z, 0, 2, 6, stretch);
        this.Bodypiece[11].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[12].addSidePlane(1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y, 2.0F + BODY_CENTRE_Z,
                0, 2, 6, stretch);
        this.Bodypiece[12].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece[13].addBackPlane(-1.0F + BODY_CENTRE_X, 2.0F + BODY_CENTRE_Y,
                8.0F + BODY_CENTRE_Z, 2, 2, 0, stretch);
        this.Bodypiece[13].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[0].addSidePlane(-7.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 0, 6, 8, stretch);
        this.VillagerBagPiece[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[1].addSidePlane(-4.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 0, 6, 8, stretch);
        this.VillagerBagPiece[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[2].addSidePlane(4.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 0, 6, 8, stretch);
        this.VillagerBagPiece[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[3].addSidePlane(7.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 0, 6, 8, stretch);
        this.VillagerBagPiece[3].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[4].addTopPlane(2.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                -2.0F + BODY_CENTRE_Z, 8, 0, 3, stretch);
        this.VillagerBagPiece[4].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[5].addTopPlane(2.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                -13.0F + BODY_CENTRE_Z, 8, 0, 3, stretch);
        this.VillagerBagPiece[5].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[6].addBottomPlane(2.0F + BODY_CENTRE_X, 1.0F + BODY_CENTRE_Y,
                -2.0F + BODY_CENTRE_Z, 8, 0, 3, stretch);
        this.VillagerBagPiece[6].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[7].addBottomPlane(2.0F + BODY_CENTRE_X, 1.0F + BODY_CENTRE_Y,
                -13.0F + BODY_CENTRE_Z, 8, 0, 3, stretch);
        this.VillagerBagPiece[7].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[8].addBackPlane(-7.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 3, 6, 0, stretch);
        this.VillagerBagPiece[8].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[9].addBackPlane(4.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                -4.0F + BODY_CENTRE_Z, 3, 6, 0, stretch);
        this.VillagerBagPiece[9].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[10].addBackPlane(-7.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                4.0F + BODY_CENTRE_Z, 3, 6, 0, stretch);
        this.VillagerBagPiece[10].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[11].addBackPlane(4.0F + BODY_CENTRE_X, -5.0F + BODY_CENTRE_Y,
                4.0F + BODY_CENTRE_Z, 3, 6, 0, stretch);
        this.VillagerBagPiece[11].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[12].addTopPlane(-4.0F + BODY_CENTRE_X, -4.5F + BODY_CENTRE_Y,
                -1.0F + BODY_CENTRE_Z, 8, 0, 1, stretch);
        this.VillagerBagPiece[13].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerBagPiece[13].addTopPlane(-4.0F + BODY_CENTRE_X, -4.5F + BODY_CENTRE_Y,
                0.0F + BODY_CENTRE_Z, 8, 0, 1, stretch);
        this.VillagerBagPiece[13].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerApron.addBackPlane(-4.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y,
                -9.0F + BODY_CENTRE_Z, 8, 10, 0, stretch);
        this.VillagerApron.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.VillagerTrinket.addBackPlane(-2.0F + BODY_CENTRE_X, -4.0F + BODY_CENTRE_Y,
                -9.0F + BODY_CENTRE_Z, 4, 5, 0, stretch);
        this.VillagerTrinket.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck[0].addBackPlane(-2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y,
                -8.8F + BODY_CENTRE_Z, 4, 4, 0, stretch);
        this.BodypieceNeck[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck[1].addBackPlane(-2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y,
                -4.8F + BODY_CENTRE_Z, 4, 4, 0, stretch);
        this.BodypieceNeck[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck[2].addSidePlane(-2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y,
                -8.8F + BODY_CENTRE_Z, 0, 4, 4, stretch);
        this.BodypieceNeck[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck[3].addSidePlane(2.0F + BODY_CENTRE_X, -6.8F + BODY_CENTRE_Y,
                -8.8F + BODY_CENTRE_Z, 0, 4, 4, stretch);
        this.BodypieceNeck[3].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.BodypieceNeck[0].rotateAngleX = this.NeckRotX;
        this.BodypieceNeck[1].rotateAngleX = this.NeckRotX;
        this.BodypieceNeck[2].rotateAngleX = this.NeckRotX;
        this.BodypieceNeck[3].rotateAngleX = this.NeckRotX;
    }

    protected void initLegPositions(float yOffset, float stretch) {
        this.bipedRightArm.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z,
                4,
                12, 4, stretch);
        this.bipedRightArm.setRotationPoint(-3.0F, 8.0F + yOffset, 0.0F);
        if (bipedRightArmwear != null) {
            this.bipedRightArmwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y,
                    -2.0F + THIRDP_ARM_CENTRE_Z, 4,
                    12, 4, stretch + 0.25f);
            this.bipedRightArmwear.setRotationPoint(-3.0F, 8.0F + yOffset, 0.0F);
        }
        this.bipedLeftArm.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z,
                4,
                12, 4, stretch);
        this.bipedLeftArm.setRotationPoint(3.0F, 8.0F + yOffset, 0.0F);
        if (this.bipedLeftArmwear != null) {
            this.bipedLeftArmwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y,
                    -2.0F + THIRDP_ARM_CENTRE_Z, 4,
                    12, 4, stretch + 0.25f);
            this.bipedLeftArmwear.setRotationPoint(3.0F, 8.0F + yOffset, 0.0F);
        }
        this.bipedRightLeg.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z,
                4, 12, 4, stretch);
        this.bipedRightLeg.setRotationPoint(-3.0F, 0.0F + yOffset, 0.0F);

        if (bipedRightLegwear != null) {
            this.bipedRightLegwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y,
                    -2.0F + THIRDP_ARM_CENTRE_Z,
                    4, 12, 4, stretch + 0.25f);
            this.bipedRightLegwear.setRotationPoint(-3.0F, 0.0F + yOffset, 0.0F);

        }

        this.bipedLeftLeg.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y, -2.0F + THIRDP_ARM_CENTRE_Z,
                4, 12, 4, stretch);
        if (this.bipedLeftLegwear != null) {
            this.bipedLeftLegwear.addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y,
                    -2.0F + THIRDP_ARM_CENTRE_Z,
                    4, 12, 4, stretch + 0.25f);
        }
        this.SteveArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, stretch);
        this.SteveArm.setRotationPoint(-5.0F, 2.0F + yOffset, 0.0F);
        this.unicornarm.addBox(-2.0F + FIRSTP_ARM_CENTRE_X, -6.0F + FIRSTP_ARM_CENTRE_Y, -2.0F + FIRSTP_ARM_CENTRE_Z, 4,
                12, 4, stretch + .25f);
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

    protected void initWingPositions(float yOffset, float stretch) {
        this.LeftWing[0].addBox(4.0F, 5.0F, 2.0F, 2, 6, 2, stretch);
        this.LeftWing[0].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.LeftWing[0].rotateAngleX = ROTATE_90;
        this.LeftWing[1].addBox(4.0F, 5.0F, 4.0F, 2, 8, 2, stretch);
        this.LeftWing[1].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.LeftWing[1].rotateAngleX = ROTATE_90;
        this.LeftWing[2].addBox(4.0F, 5.0F, 6.0F, 2, 6, 2, stretch);
        this.LeftWing[2].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.LeftWing[2].rotateAngleX = ROTATE_90;
        this.RightWing[0].addBox(-6.0F, 5.0F, 2.0F, 2, 6, 2, stretch);
        this.RightWing[0].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.RightWing[0].rotateAngleX = ROTATE_90;
        this.RightWing[1].addBox(-6.0F, 5.0F, 4.0F, 2, 8, 2, stretch);
        this.RightWing[1].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.RightWing[1].rotateAngleX = ROTATE_90;
        this.RightWing[2].addBox(-6.0F, 5.0F, 6.0F, 2, 6, 2, stretch);
        this.RightWing[2].setRotationPoint(HEAD_RP_X, WING_FOLDED_RP_Y + yOffset, WING_FOLDED_RP_Z);
        this.RightWing[2].rotateAngleX = ROTATE_90;
        this.LeftWingExt[0].addBox(0.0F, 6.0F, 0.0F, 1, 8, 2, stretch + 0.1F);
        this.LeftWingExt[0].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset,
                LEFT_WING_EXT_RP_Z);
        this.LeftWingExt[1].addBox(0.0F, -1.2F, -0.2F, 1, 8, 2, stretch - 0.2F);
        this.LeftWingExt[1].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset,
                LEFT_WING_EXT_RP_Z);
        this.LeftWingExt[2].addBox(0.0F, 1.8F, 1.3F, 1, 8, 2, stretch - 0.1F);
        this.LeftWingExt[2].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset,
                LEFT_WING_EXT_RP_Z);
        this.LeftWingExt[3].addBox(0.0F, 5.0F, 2.0F, 1, 8, 2, stretch);
        this.LeftWingExt[3].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset,
                LEFT_WING_EXT_RP_Z);
        this.LeftWingExt[4].addBox(0.0F, 0.0F, -0.2F, 1, 6, 2, stretch + 0.3F);
        this.LeftWingExt[4].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset,
                LEFT_WING_EXT_RP_Z);
        this.LeftWingExt[5].addBox(0.0F, 0.0F, 0.2F, 1, 3, 2, stretch + 0.2F);
        this.LeftWingExt[5].setRotationPoint(LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + yOffset,
                LEFT_WING_EXT_RP_Z);
        this.RightWingExt[0].addBox(0.0F, 6.0F, 0.0F, 1, 8, 2, stretch + 0.1F);
        this.RightWingExt[0].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset,
                RIGHT_WING_EXT_RP_Z);
        this.RightWingExt[1].addBox(0.0F, -1.2F, -0.2F, 1, 8, 2, stretch - 0.2F);
        this.RightWingExt[1].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset,
                RIGHT_WING_EXT_RP_Z);
        this.RightWingExt[2].addBox(0.0F, 1.8F, 1.3F, 1, 8, 2, stretch - 0.1F);
        this.RightWingExt[2].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset,
                RIGHT_WING_EXT_RP_Z);
        this.RightWingExt[3].addBox(0.0F, 5.0F, 2.0F, 1, 8, 2, stretch);
        this.RightWingExt[3].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset,
                RIGHT_WING_EXT_RP_Z);
        this.RightWingExt[4].addBox(0.0F, 0.0F, -0.2F, 1, 6, 2, stretch + 0.3F);
        this.RightWingExt[4].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset,
                RIGHT_WING_EXT_RP_Z);
        this.RightWingExt[5].addBox(0.0F, 0.0F, 0.2F, 1, 3, 2, stretch + 0.2F);
        this.RightWingExt[5].setRotationPoint(RIGHT_WING_EXT_RP_X, RIGHT_WING_EXT_RP_Y + yOffset,
                RIGHT_WING_EXT_RP_Z);
    }

    @Override
    public void renderCape(float scale) {
        this.bipedCape.render(scale);
    }

    protected void compressWings() {
        this.CompressiveLeftWing = new CompressiveRendering(this);
        this.CompressiveRightWing = new CompressiveRendering(this);
        this.CompressiveLeftWing.addCompressed(this.LeftWing[0]);
        this.CompressiveLeftWing.addCompressed(this.LeftWing[1]);
        this.CompressiveLeftWing.addCompressed(this.LeftWing[2]);
        this.CompressiveRightWing.addCompressed(this.RightWing[0]);
        this.CompressiveRightWing.addCompressed(this.RightWing[1]);
        this.CompressiveRightWing.addCompressed(this.RightWing[2]);
        this.CompressiveLeftWing.addExpanded(this.LeftWingExt[0]);
        this.CompressiveLeftWing.addExpanded(this.LeftWingExt[1]);
        this.CompressiveLeftWing.addExpanded(this.LeftWingExt[2]);
        this.CompressiveLeftWing.addExpanded(this.LeftWingExt[3]);
        this.CompressiveLeftWing.addExpanded(this.LeftWingExt[4]);
        this.CompressiveLeftWing.addExpanded(this.LeftWingExt[5]);
        this.CompressiveRightWing.addExpanded(this.RightWingExt[0]);
        this.CompressiveRightWing.addExpanded(this.RightWingExt[1]);
        this.CompressiveRightWing.addExpanded(this.RightWingExt[2]);
        this.CompressiveRightWing.addExpanded(this.RightWingExt[3]);
        this.CompressiveRightWing.addExpanded(this.RightWingExt[4]);
        this.CompressiveRightWing.addExpanded(this.RightWingExt[5]);
        this.CompressiveLeftWing.setChance(2);
        this.CompressiveRightWing.setChance(2);
    }

    @Override
    public ModelRenderer getRandomModelBox(Random par1Random) {
        Object part = this.boxList.get(par1Random.nextInt(this.boxList.size()));
        return part instanceof ModelRenderer ? (ModelRenderer) part
                : ((CompressiveRendering) part).getARenderer(par1Random);
    }

    public void setExtendingWings(boolean isCompressed) {
        this.CompressiveLeftWing.setIsCompressed(isCompressed);
        this.CompressiveRightWing.setIsCompressed(isCompressed);
    }

    public void setHasWings_Compression(boolean pegasus) {
        if (pegasus) {
            this.CompressiveLeftWing.init_Safe();
            this.CompressiveRightWing.init_Safe();
        } else {
            this.CompressiveLeftWing.deInit_Safe();
            this.CompressiveRightWing.deInit_Safe();
        }
    }

}
