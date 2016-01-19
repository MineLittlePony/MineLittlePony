package com.brohoof.minelittlepony.model.pony.armor;

import com.brohoof.minelittlepony.model.part.PonyEars;
import com.brohoof.minelittlepony.model.pony.ModelPlayerPony;
import com.brohoof.minelittlepony.renderer.AniParams;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;

public class ModelPonyArmor extends ModelPlayerPony {

    public ModelRenderer Bodypiece;
    public ModelRenderer extBody;
    public ModelRenderer[] extHead;
    public ModelRenderer[] extLegs;

    public ModelPonyArmor() {
        super(false);
        this.isArmour = true;
        this.textureHeight = 32;
    }

    @Override
    protected void addParts() {
        modelParts.add(new PonyEars());
    }

    @Override
    public void animate(AniParams aniparams) {
        this.checkRainboom(aniparams.swing);
        this.rotateHead(aniparams.horz, aniparams.vert);
        float bodySwingRotation = 0.0F;
        if (this.swingProgress > -9990.0F && !this.metadata.getRace().hasHorn()) {
            bodySwingRotation = MathHelper.sin(MathHelper.sqrt_float(this.swingProgress) * 3.1415927F * 2.0F) * 0.2F;
        }

        this.bipedBody.rotateAngleY = bodySwingRotation * 0.2F;
        this.Bodypiece.rotateAngleY = bodySwingRotation * 0.2F;
        this.extBody.rotateAngleY = bodySwingRotation * 0.2F;
        this.setLegs(aniparams.move, aniparams.swing, aniparams.tick);
        this.holdItem();
        this.swingItem(this.swingProgress);
        if (this.isSneak && !this.isFlying) {
            this.adjustBody(BODY_ROTATE_ANGLE_X_SNEAK, BODY_RP_Y_SNEAK, BODY_RP_Z_SNEAK);
            this.sneakLegs();
            this.setHead(0.0F, 6.0F, -2.0F);
        } else {
            this.adjustBody(BODY_ROTATE_ANGLE_X_NOTSNEAK, BODY_RP_Y_NOTSNEAK,
                    BODY_RP_Z_NOTSNEAK);
            this.bipedRightLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.bipedLeftLeg.rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.extLegs[0].rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.extLegs[1].rotationPointY = FRONT_LEG_RP_Y_NOTSNEAK;
            this.swingArms(aniparams.tick);
            this.setHead(0.0F, 0.0F, 0.0F);
        }

        if (this.isSleeping) {
            this.ponySleep();
        }

        if (this.aimedBow) {
            this.aimBow(aniparams.tick);
        }

        this.fixSpecialRotationPoints(aniparams.move);
    }

    @Override
    protected void setHead(float posX, float posY, float posZ) {
        this.setRotationPoint(this.bipedHead, posX, posY, posZ);
        this.setRotationPoint(this.bipedHeadwear, posX, posY, posZ);
        this.setRotationPoint(this.extHead[0], posX, posY, posZ);
        this.setRotationPoint(this.extHead[1], posX, posY, posZ);
    }

    @Override
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
        this.extHead[0].rotateAngleY = headRotateAngleY;
        this.extHead[0].rotateAngleX = headRotateAngleX;
        this.extHead[1].rotateAngleY = headRotateAngleY;
        this.extHead[1].rotateAngleX = headRotateAngleX;
        this.bipedHeadwear.rotateAngleY = headRotateAngleY;
        this.bipedHeadwear.rotateAngleX = headRotateAngleX;
    }

    @Override
    protected void adjustBody(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        this.bipedBody.rotateAngleX = rotateAngleX;
        this.bipedBody.rotationPointY = rotationPointY;
        this.bipedBody.rotationPointZ = rotationPointZ;
        this.Bodypiece.rotateAngleX = rotateAngleX;
        this.Bodypiece.rotationPointY = rotationPointY;
        this.Bodypiece.rotationPointZ = rotationPointZ;
        this.extBody.rotateAngleX = rotateAngleX;
        this.extBody.rotationPointY = rotationPointY;
        this.extBody.rotationPointZ = rotationPointZ;
    }

    protected void ridingPony() {
        this.setHead(this.bipedHead.rotationPointX + 0.0F, this.bipedHead.rotationPointY + RIDING_SHIFT_Y,
                this.bipedHead.rotationPointZ + RIDING_SHIFT_Z);
        this.shiftRotationPoint(this.bipedBody, 0.0F, RIDING_SHIFT_Y, RIDING_SHIFT_Z);
        this.shiftRotationPoint(this.Bodypiece, 0.0F, RIDING_SHIFT_Y, RIDING_SHIFT_Z);
        this.shiftRotationPoint(this.extBody, 0.0F, RIDING_SHIFT_Y, RIDING_SHIFT_Z);
        this.shiftRotationPoint(this.bipedLeftArm, 0.0F, RIDING_SHIFT_Y, RIDING_SHIFT_Z);
        this.shiftRotationPoint(this.bipedRightArm, 0.0F, RIDING_SHIFT_Y, RIDING_SHIFT_Z);
        this.shiftRotationPoint(this.bipedLeftLeg, 0.0F, RIDING_SHIFT_Y, RIDING_SHIFT_Z);
        this.shiftRotationPoint(this.bipedRightLeg, 0.0F, RIDING_SHIFT_Y, RIDING_SHIFT_Z);
        this.shiftRotationPoint(this.extLegs[0], 0.0F, RIDING_SHIFT_Y, RIDING_SHIFT_Z);
        this.shiftRotationPoint(this.extLegs[1], 0.0F, RIDING_SHIFT_Y, RIDING_SHIFT_Z);
    }

    @Override
    protected void renderHead() {
        this.bipedHead.render(this.scale);
        this.extHead[0].render(this.scale);
        this.extHead[1].render(this.scale);
        this.bipedHeadwear.render(this.scale);
    }

    @Override
    protected void renderNeck() {}

    @Override
    protected void renderBody() {
        this.bipedBody.render(this.scale);
        this.Bodypiece.render(this.scale);
        this.extBody.render(this.scale);
    }

    @Override
    protected void renderTail() {}

    @Override
    protected void renderLegs() {
        this.bipedLeftArm.render(this.scale);
        this.bipedRightArm.render(this.scale);
        this.bipedLeftLeg.render(this.scale);
        this.bipedRightLeg.render(this.scale);
        this.extLegs[0].render(this.scale);
        this.extLegs[1].render(this.scale);
    }

    @Override
    protected void initTextures() {
        this.extHead = new ModelRenderer[2];
        this.extLegs = new ModelRenderer[2];
        this.initHeadTextures();
        this.initBodyTextures();
        this.initLegTextures();
    }

    @Override
    protected void initHeadTextures() {
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.extHead[0] = new ModelRenderer(this, 0, 0);
        this.extHead[1] = new ModelRenderer(this, 0, 4);
    }

    @Override
    protected void initBodyTextures() {
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.Bodypiece = new ModelRenderer(this, 0, 0);
        this.extBody = new ModelRenderer(this, 16, 8);
    }

    @Override
    protected void initLegTextures() {
        this.bipedRightArm = new ModelRenderer(this, 0, 16);
        this.bipedLeftArm = new ModelRenderer(this, 0, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.steveRightArm = new ModelRenderer(this, 0, 16);
        this.unicornarm = new ModelRenderer(this, 0, 16);
        this.extLegs[0] = new ModelRenderer(this, 48, 8);
        this.extLegs[1] = new ModelRenderer(this, 48, 8);
        this.extLegs[1].mirror = true;
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        this.initHeadPositions(yOffset, stretch);
        this.initBodyPositions(yOffset, stretch);
        this.initLegPositions(yOffset, stretch);
    }

    @Override
    protected void initHeadPositions(float yOffset, float stretch) {
        this.bipedHead.addBox(-4.0F + HEAD_CENTRE_X, -4.0F + HEAD_CENTRE_Y,
                -4.0F + HEAD_CENTRE_Z, 8, 8, 8,
                stretch * 1.1F);
        this.bipedHead.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.extHead[0].addBox(-4.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y,
                1.0F + HEAD_CENTRE_Z, 2, 2,
                2, stretch * 0.5F);
        this.extHead[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset,
                HEAD_RP_Z);
        this.extHead[1].addBox(2.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y,
                1.0F + HEAD_CENTRE_Z, 2, 2,
                2, stretch * 0.5F);
        this.extHead[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset,
                HEAD_RP_Z);
        this.bipedHeadwear.addBox(-4.0F + HEAD_CENTRE_X, -4.0F + HEAD_CENTRE_Y,
                -4.0F + HEAD_CENTRE_Z, 8, 8, 8,
                stretch * 1.1F + 0.5F);
        this.bipedHeadwear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset,
                HEAD_RP_Z);
    }

    @Override
    protected void initBodyPositions(float yOffset, float stretch) {
        this.bipedBody.addBox(-4.0F, 4.0F, -2.0F, 8, 8, 4, stretch);
        this.bipedBody.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.Bodypiece.addBox(-4.0F, 4.0F, 6.0F, 8, 8, 8, stretch);
        this.Bodypiece.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset,
                HEAD_RP_Z);
        this.extBody.addBox(-4.0F, 4.0F, -2.0F, 8, 8, 16, stretch);
        this.extBody.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset,
                HEAD_RP_Z);
    }

    @Override
    protected void initLegPositions(float yOffset, float stretch) {
        super.initLegPositions(yOffset, stretch);
        this.extLegs[0].addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y,
                -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch);
        this.extLegs[0].setRotationPoint(-3.0F, 0.0F + yOffset, 0.0F);
        this.extLegs[1].addBox(-2.0F + THIRDP_ARM_CENTRE_X, -6.0F + THIRDP_ARM_CENTRE_Y,
                -2.0F + THIRDP_ARM_CENTRE_Z, 4, 12, 4, stretch);
        this.extLegs[1].setRotationPoint(3.0F, 0.0F + yOffset, 0.0F);
    }

    protected void syncLegs() {
        this.extLegs[0].rotateAngleX = this.bipedRightLeg.rotateAngleX;
        this.extLegs[0].rotateAngleY = this.bipedRightLeg.rotateAngleY;
        this.extLegs[0].rotateAngleZ = this.bipedRightLeg.rotateAngleZ;
        this.extLegs[0].rotationPointX = this.bipedRightLeg.rotationPointX;
        this.extLegs[0].rotationPointY = this.bipedRightLeg.rotationPointY;
        this.extLegs[0].rotationPointZ = this.bipedRightLeg.rotationPointZ;
        this.extLegs[1].rotateAngleX = this.bipedLeftLeg.rotateAngleX;
        this.extLegs[1].rotateAngleY = this.bipedLeftLeg.rotateAngleY;
        this.extLegs[1].rotateAngleZ = this.bipedLeftLeg.rotateAngleZ;
        this.extLegs[1].rotationPointX = this.bipedLeftLeg.rotationPointX;
        this.extLegs[1].rotationPointY = this.bipedLeftLeg.rotationPointY;
        this.extLegs[1].rotationPointZ = this.bipedLeftLeg.rotationPointZ;
    }

    @Override
    protected void rotateLegs(float move, float swing, float tick) {
        super.rotateLegs(move, swing, tick);
        this.syncLegs();
    }

    @Override
    protected void adjustLegs() {
        super.adjustLegs();
        this.syncLegs();
    }

    @Override
    protected void sneakLegs() {
        super.sneakLegs();
        this.syncLegs();
    }

    @Override
    protected void ponySleep() {
        super.ponySleep();
        this.syncLegs();
    }

    @Override
    protected void setModelVisibilities(AbstractClientPlayer clientPlayer) {}

    @Override
    public void setInvisible(boolean invisible) {
        super.setInvisible(invisible);
        this.Bodypiece.showModel = invisible;
        extBody.showModel = invisible;
        for (ModelRenderer m : extHead) {
            m.showModel = invisible;
        }
        for (ModelRenderer m : extLegs) {
            m.showModel = invisible;
        }
    }
}
