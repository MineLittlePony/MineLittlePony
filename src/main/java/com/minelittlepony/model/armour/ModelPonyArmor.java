package com.minelittlepony.model.armour;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.ModelMobPony;
import com.minelittlepony.render.PonyRenderer;

public class ModelPonyArmor extends ModelMobPony {

    public PonyRenderer Bodypiece, extBody, extLegLeft, extLegRight, extHead;
    
    public ModelPonyArmor() {
        super();
        this.textureHeight = 32;
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
        extHead.setRotationPoint(posX, posY, posZ);
    }

    @Override
    protected void updateHeadRotation(float x, float y) {
        super.updateHeadRotation(x, y);
        extHead.rotateAngleX = x;
        extHead.rotateAngleX = x;
    }

    @Override
    protected void adjustBody(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        bipedBody.rotateAngleX = rotateAngleX;
        bipedBody.rotationPointY = rotationPointY;
        bipedBody.rotationPointZ = rotationPointZ;
        
        Bodypiece.rotateAngleX = rotateAngleX;
        Bodypiece.rotationPointY = rotationPointY;
        Bodypiece.rotationPointZ = rotationPointZ;
        
        extBody.rotateAngleX = rotateAngleX;
        extBody.rotationPointY = rotationPointY;
        extBody.rotationPointZ = rotationPointZ;
    }

    @Override
    protected void renderHead(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        bipedHead.render(this.scale);
        extHead.render(this.scale);
        bipedHeadwear.render(this.scale);
    }

    @Override
    protected void renderNeck() {
    }

    @Override
    protected void renderBody(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        bipedBody.render(this.scale);
        Bodypiece.render(this.scale);
        extBody.render(this.scale);
    }

    @Override
    protected void renderLegs() {
        if (!isSneak) {
            boolean isLegs = this.extBody.showModel;
            extBody.showModel = true;
            extBody.postRender(scale);
            extBody.showModel = isLegs;
        }
        bipedLeftArm.render(scale);
        bipedRightArm.render(scale);
        bipedLeftLeg.render(scale);
        bipedRightLeg.render(scale);
        extLegRight.render(scale);
        extLegLeft.render(scale);
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
        extHead = new PonyRenderer(this, 0, 0);
    }

    @Override
    protected void initBodyTextures() {
        bipedBody = new PonyRenderer(this, 16, 16);
        Bodypiece = new PonyRenderer(this, 0, 0);
        extBody = new PonyRenderer(this, 16, 8);
    }

    @Override
    protected void initLegTextures() {
        bipedRightArm = new PonyRenderer(this, 0, 16);
        bipedRightLeg = new ModelRenderer(this, 0, 16);
        
        bipedLeftArm = new PonyRenderer(this, 0, 16).mirror();
        bipedLeftLeg = new PonyRenderer(this, 0, 16).mirror();
        
        unicornArmRight = new PonyRenderer(this, 0, 16);
        unicornArmLeft = new PonyRenderer(this, 0, 16);
        
        extLegLeft = new PonyRenderer(this, 48, 8);
        extLegRight = new PonyRenderer(this, 48, 8);
    }

    @Override
    protected void initTailPositions(float yOffset, float stretch) {
        
    }

    @Override
    protected void initHeadPositions(float yOffset, float stretch) {
        bipedHead    .addBox(HEAD_CENTRE_X - 4, HEAD_CENTRE_Y - 4, HEAD_CENTRE_Z - 4, 8, 8, 8, stretch * 1.1F);
        bipedHeadwear.addBox(HEAD_CENTRE_X - 4, HEAD_CENTRE_Y - 4, HEAD_CENTRE_Z - 4, 8, 8, 8, stretch * 1.1F + 0.5F);
        
        extHead.offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
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
        
        Bodypiece.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                 .box(-4.0F, 4.0F,  6.0F, 8, 8, 8, stretch);
        extBody.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)  
                 .box(-4.0F, 4.0F, -2.0F, 8, 8, 16, stretch);
    }

    @Override
    protected void initLegPositions(float yOffset, float stretch) {
        super.initLegPositions(yOffset, stretch);
        extLegLeft.offset(THIRDP_ARM_CENTRE_X, THIRDP_ARM_CENTRE_Y, THIRDP_ARM_CENTRE_Z)
                       .around(-3, yOffset, 0)
                       .box(-2, -6, -2, 4, 12, 4, stretch);
        extLegRight.offset(THIRDP_ARM_CENTRE_X, THIRDP_ARM_CENTRE_Y, THIRDP_ARM_CENTRE_Z)
                       .around(3, yOffset, 0)
              .mirror().box(-2, -6, -2, 4, 12, 4, stretch);
    }

    protected void syncLegs() {
        extLegRight.rotateAt(bipedRightLeg).rotateTo(bipedRightLeg);
        extLegLeft.rotateAt(bipedLeftLeg).rotateTo(bipedLeftLeg);
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

    public void setVisible(boolean invisible) {
        super.setVisible(invisible);
        Bodypiece.showModel = invisible;
        extBody.showModel = invisible;
        extHead.showModel = invisible;
        extLegLeft.showModel = invisible;
        extLegRight.showModel = invisible;
    }
}
