package com.minelittlepony.model.ponies;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.model.components.PonySnout;
import com.minelittlepony.render.PonyRenderer;

import static com.minelittlepony.model.PonyModelConstants.HEAD_CENTRE_X;
import static com.minelittlepony.model.PonyModelConstants.HEAD_CENTRE_Y;
import static com.minelittlepony.model.PonyModelConstants.HEAD_CENTRE_Z;
import static com.minelittlepony.model.PonyModelConstants.HEAD_RP_X;
import static com.minelittlepony.model.PonyModelConstants.HEAD_RP_Y;
import static com.minelittlepony.model.PonyModelConstants.HEAD_RP_Z;
import static com.minelittlepony.model.PonyModelConstants.LEFT_WING_ROTATE_ANGLE_Z_SNEAK;

public class ModelEnderStallion extends ModelSkeletonPony {

    public boolean isCarrying;
    public boolean isAttacking;

    public boolean isAlicorn;
    public boolean isBoss;

    private PonyRenderer leftHorn;
    private PonyRenderer rightHorn;

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float move, float swing, float ticks) {
        rightArmPose = isCarrying ? ArmPose.BLOCK : ArmPose.EMPTY;
        leftArmPose = rightArmPose;

        isAlicorn = entity.getUniqueID().getLeastSignificantBits() % 3 == 0;
        isBoss = !isAlicorn && entity.getUniqueID().getLeastSignificantBits() % 90 == 0;

        leftHorn.isHidden = rightHorn.isHidden = !isBoss;
        horn.setVisible(!isBoss);

        tail.setVisible(false);
        snout.isHidden = true;
        bipedLeftArmwear.isHidden = true;
        bipedRightArmwear.isHidden = true;

        bipedLeftLegwear.isHidden = true;
        bipedRightLegwear.isHidden = true;

        leftHorn.rotateAngleX = 0.5F;
        rightHorn.rotateAngleX = 0.5F;
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);

    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        if (isAttacking) {
            bipedHead.rotationPointY -= 5;
        }
    }

    @Override
    public void render(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -1.15F, 0);
        super.render(entity, move, swing, ticks, headYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean canCast() {
        return true;
    }

    @Override
    public boolean canFly() {
        return isAlicorn;
    }

    @Override
    protected void initHead(float yOffset, float stretch) {
        bipedHead = new PonyRenderer(this, 0, 0)
                                 .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                 .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                 .box(-4, -4, -4, 8, 8, 8, stretch)
                     .tex(12, 16).box(-4, -6, 1, 2, 2, 2, stretch)
                          .flip().box( 2, -6, 1, 2, 2, 2, stretch);

        leftHorn = ((PonyRenderer)bipedHead).child().tex(0, 52);
        leftHorn.tex(0, 52)
                .rotate(0.1F, 0, -0.8F)
                .offset(-2, -10, -3)
                .box(0, 0, 0, 2, 6, 2, stretch)
                .child()
                    .rotate(0, 0, 0.9F)
                    .around(-3.9F, -6, 0.001F)
                    .box(0, 0, 0, 2, 6, 2, stretch);

        rightHorn = ((PonyRenderer)bipedHead).child().tex(0, 52);
        rightHorn.tex(8, 52)
                .rotate(0.1F, 0, 0.8F)
                .offset(0, -10, -3)
                .box(0, 0, 0, 2, 6, 2, stretch)
                .child()
                    .rotate(0, 0, -0.9F)
                    .around(3.9F, -6, 0.001F)
                    .box(0, 0, 0, 2, 6, 2, stretch);

        bipedHeadwear = new PonyRenderer(this, 32, 0)
                                     .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                     .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                     .box(-4, -4, -4, 8, 8, 8, stretch - 0.5F);

        snout = new PonySnout(this);
        snout.init(yOffset, stretch);
    }

    @Override
    protected void rotateArmHolding(ModelRenderer arm, float direction, float swingProgress, float ticks) {
        arm.rotateAngleX = -0.3707964F;
        arm.rotateAngleX += 0.4F + MathHelper.sin(ticks * 0.067F) / 10;
    }

    @Override
    protected void preInitLegs() {
        bipedLeftArm = new ModelRenderer(this, 0, 20);
        bipedRightArm = new ModelRenderer(this, 0, 20);

        bipedLeftLeg = new ModelRenderer(this, 0, 20);
        bipedRightLeg = new ModelRenderer(this, 0, 20);
    }

    @Override
    public boolean wingsAreOpen() {
        return isAttacking;
    }

    @Override
    protected float getLegRotationX() {
        return 3;
    }

    @Override
    protected float getArmRotationY() {
        return 14;
    }

    @Override
    protected int getArmLength() {
        return 30;
    }

    @Override
    public float getWingRotationFactor(float ticks) {
        return MathHelper.sin(ticks) + LEFT_WING_ROTATE_ANGLE_Z_SNEAK;
    }
}
