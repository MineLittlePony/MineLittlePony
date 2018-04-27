package com.minelittlepony.model.armour;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.ModelMobPony;
import com.minelittlepony.render.PonyRenderer;

public class ModelPonyArmor extends ModelMobPony {

    public PonyRenderer flankGuard;

    public PonyRenderer saddle;
    public PonyRenderer helmet;

    public PonyRenderer leftLegging;
    public PonyRenderer rightLegging;

    public ModelPonyArmor() {
        super();
        textureHeight = 32;
    }

    @Override
    protected void rotateLook(float limbSwing, float limbSwingAmount, float bodySwing, float ticks) {
        bipedBody.rotateAngleY = bodySwing * 0.2F;
    }

    @Override
    protected void adjustBodyRiding() {
        adjustBody(BODY_ROTATE_ANGLE_X_RIDING, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
    }

    @Override
    protected void setHead(float posX, float posY, float posZ) {
        super.setHead(posX, posY, posZ);
        helmet.setRotationPoint(posX, posY, posZ);
    }

    @Override
    protected void updateHeadRotation(float x, float y) {
        super.updateHeadRotation(x, y);
        helmet.rotateAngleX = x;
        helmet.rotateAngleY = y;
    }

    @Override
    protected void adjustBody(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        bipedBody.rotateAngleX = rotateAngleX;
        bipedBody.rotationPointY = rotationPointY;
        bipedBody.rotationPointZ = rotationPointZ;

        flankGuard.rotateAngleX = rotateAngleX;
        flankGuard.rotationPointY = rotationPointY;
        flankGuard.rotationPointZ = rotationPointZ;

        saddle.rotateAngleX = rotateAngleX;
        saddle.rotationPointY = rotationPointY;
        saddle.rotationPointZ = rotationPointZ;
    }

    @Override
    protected void renderHead(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        bipedHead.render(this.scale);
        helmet.render(this.scale);
        bipedHeadwear.render(this.scale);
    }

    @Override
    protected void renderNeck() {
    }

    @Override
    protected void renderBody(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        bipedBody.render(this.scale);
        flankGuard.render(this.scale);
        saddle.render(this.scale);
    }

    @Override
    protected void renderLegs() {
        if (!isSneak) {
            boolean isLegs = saddle.showModel;
            saddle.showModel = true;
            saddle.postRender(scale);
            saddle.showModel = isLegs;
        }
        bipedLeftArm.render(scale);
        bipedRightArm.render(scale);
        bipedLeftLeg.render(scale);
        bipedRightLeg.render(scale);
        rightLegging.render(scale);
        leftLegging.render(scale);
    }

    @Override
    protected void initTextures() {
        initHeadTextures();
        initBodyTextures();
        initLegTextures();
    }

    @Override
    protected void initHeadTextures() {
        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHeadwear = new ModelRenderer(this, 32, 0);
        helmet = new PonyRenderer(this, 0, 0);
    }

    @Override
    protected void initBodyTextures() {
        bipedBody = new PonyRenderer(this, 16, 16);
        flankGuard = new PonyRenderer(this, 0, 0);
        saddle = new PonyRenderer(this, 16, 8);
    }

    @Override
    protected void initLegTextures() {
        bipedRightArm = new PonyRenderer(this, 0, 16);
        bipedRightLeg = new ModelRenderer(this, 0, 16);

        bipedLeftArm = new PonyRenderer(this, 0, 16).mirror();
        bipedLeftLeg = new PonyRenderer(this, 0, 16).mirror();

        unicornArmRight = new PonyRenderer(this, 0, 16);
        unicornArmLeft = new PonyRenderer(this, 0, 16);

        leftLegging = new PonyRenderer(this, 48, 8);
        rightLegging = new PonyRenderer(this, 48, 8);
    }

    @Override
    protected void initTailPositions(float yOffset, float stretch) {

    }

    @Override
    protected void initHeadPositions(float yOffset, float stretch) {
        bipedHead    .addBox(HEAD_CENTRE_X - 4, HEAD_CENTRE_Y - 4, HEAD_CENTRE_Z - 4, 8, 8, 8, stretch * 1.1F);
        bipedHeadwear.addBox(HEAD_CENTRE_X - 4, HEAD_CENTRE_Y - 4, HEAD_CENTRE_Z - 4, 8, 8, 8, stretch * 1.1F + 0.5F);

        helmet.offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
               .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                  .box(-4, -6, 1, 2, 2, 2, stretch * 0.5F)
        .tex(0, 4).box( 2, -6, 1, 2, 2, 2, stretch * 0.5F);

        bipedHead    .setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        bipedHeadwear.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
    }

    @Override
    protected void initBodyPositions(float yOffset, float stretch) {
        bipedBody.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        bipedBody.addBox(-4.0F, 4.0F, -2.0F, 8, 8, 4, stretch);

        flankGuard.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                 .box(-4.0F, 4.0F,  6.0F, 8, 8, 8, stretch);
        saddle.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                 .box(-4.0F, 4.0F, -2.0F, 8, 8, 16, stretch);
    }

    @Override
    protected void initLegPositions(float yOffset, float stretch) {
        super.initLegPositions(yOffset, stretch);

        leftLegging.offset(THIRDP_ARM_CENTRE_X, THIRDP_ARM_CENTRE_Y, THIRDP_ARM_CENTRE_Z)
                       .around(3, yOffset, 0)
                       .box(-2, -6, -2, 4, 12, 4, stretch);
        rightLegging.offset(THIRDP_ARM_CENTRE_X, THIRDP_ARM_CENTRE_Y, THIRDP_ARM_CENTRE_Z)
                       .around(-3, yOffset, 0)
              .mirror().box(-2, -6, -2, 4, 12, 4, stretch);
    }

    protected void syncLegs() {
        rightLegging.rotateAt(bipedRightLeg).rotateTo(bipedRightLeg);
        leftLegging.rotateAt(bipedLeftLeg).rotateTo(bipedLeftLeg);
    }

    @Override
    protected void rotateLegs(float move, float swing, float tick, Entity entity) {
        super.rotateLegs(move, swing, tick, entity);
        syncLegs();
    }

    @Override
    protected void adjustLegs() {
        super.adjustLegs();
        syncLegs();
    }

    @Override
    protected void sneakLegs() {
        super.sneakLegs();
        syncLegs();
    }

    @Override
    protected void ponySleep() {
        super.ponySleep();
        syncLegs();
    }

    @Override
    public void setVisible(boolean invisible) {
        super.setVisible(invisible);
        flankGuard.showModel = invisible;
        saddle.showModel = invisible;
        helmet.showModel = invisible;
        leftLegging.showModel = invisible;
        rightLegging.showModel = invisible;
    }
}
