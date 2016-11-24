package com.minelittlepony.model.pony;

import com.minelittlepony.model.PonyModelConstants;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public abstract class ModelIllagerPony extends ModelBase {

    public boolean isUnicorn;
    public int glowColor;

    public ModelRenderer illagerHead;
    public ModelRenderer illagerBody;
    public ModelRenderer leftForeLeg;
    public ModelRenderer rightForeLeg;
    public ModelRenderer leftHindLeg;
    public ModelRenderer rightHindLeg;
    public ModelRenderer tail;
    public ModelRenderer horn;

    ModelIllagerPony() {
        textureWidth = 64;
        textureHeight = 64;

        this.setTextureOffset("head.ear1", 24, 4);
        this.setTextureOffset("head.ear2", 0, 4);
        this.setTextureOffset("head.snuzzle.mare1", 48, 21);
        this.setTextureOffset("head.snuzzle.mare2", 49, 20);
        this.setTextureOffset("head.snuzzle.stallion", 48, 16);

        illagerHead = new ModelRenderer(this, "head");
        illagerHead.setRotationPoint(0F, 1F, -5F);
        illagerHead.setTextureOffset(0, 0).addBox(-4F, -6F, -5F, 8, 8, 8);
        // headwear
        illagerHead.setTextureOffset(32, 0).addBox(-4F, -6F, -5F, 8, 8, 8, 0.5F);
        illagerHead.addBox("ear1", 2F, -8F, 0F, 2, 2, 2);
        illagerHead.addBox("ear2", -4F, -8F, 0F, 2, 2, 2);
        illagerHead.addBox("snuzzle.mare1", -2F, -0F, -6F, 4, 2, 1);
        illagerHead.addBox("snuzzle.mare2", -1F, -1F, -6F, 2, 1, 1);
        illagerHead.addBox("snuzzle.stallion", -2F, -1F, -6F, 4, 3, 1);

        horn = new ModelRenderer(this, 60, 3);
        horn.addBox(-0.5F, -11.0F, -2F, 1, 4, 1);
        horn.rotateAngleX = 0.5F;

        this.illagerHead.addChild(horn);

        ModelRenderer illagerNeck = new ModelRenderer(this, 0, 40);
        illagerNeck.addBox(-2F, 0F, -6F, 4, 4, 4);
        illagerNeck.rotateAngleX = PonyModelConstants.NECK_ROT_X;

        illagerBody = new ModelRenderer(this);
        illagerBody.setTextureOffset(0, 16).addBox(-4F, 4F, -5F, 8, 8, 16);
        illagerBody.addChild(illagerNeck);

        leftForeLeg = new ModelRenderer(this, 32, 16);
        leftForeLeg.addBox(0F, 0F, -2F, 4, 12, 4).setRotationPoint(0F, 12F, -3F);

        rightForeLeg = new ModelRenderer(this, 32, 48);
        rightForeLeg.addBox(-2F, 0F, -2F, 4, 12, 4).setRotationPoint(-2F, 12F, -3F);

        leftHindLeg = new ModelRenderer(this, 0, 16);
        leftHindLeg.addBox(-2F, 0F, -2F, 4, 12, 4).setRotationPoint(2F, 12F, 8F);

        rightHindLeg = new ModelRenderer(this, 0, 48);
        rightHindLeg.addBox(-2F, 0F, -2F, 4, 12, 4).setRotationPoint(-2F, 12F, 8F);

        ModelRenderer tailStub = new ModelRenderer(this, 52, 24);
        tailStub.addBox(-0.5F, -1F, -0.5F, 2, 4, 2).setRotationPoint(0F, 0F, 2F);
        tailStub.rotateAngleX = (float) Math.PI / -3;


        this.setTextureOffset("tail.1qtr", 48, 24);
        this.setTextureOffset("tail.half", 48, 32);
        this.setTextureOffset("tail.3qtr", 48, 40);
        this.setTextureOffset("tail.full", 48, 48);

        tail = new ModelRenderer(this, "tail");
        tail.setRotationPoint(0F, 4F, 11F);
        tail.addBox("1qtr", -1.5F, -2F, 2F, 4, 4, 4);
        tail.addBox("half", -1.5F, 2F, 2F, 4, 4, 4);
        tail.addBox("3qtr", -1.5F, 6F, 2F, 4, 4, 4);
        tail.addBox("full", -1.5F, 10F, 2F, 4, 4, 4);
        tail.addChild(tailStub);



    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        illagerHead.render(scale);
        illagerBody.render(scale);
        leftForeLeg.render(scale);
        rightForeLeg.render(scale);
        leftHindLeg.render(scale);
        rightHindLeg.render(scale);
        tail.render(scale);

//        mr.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {

        final float PI = (float) Math.PI;

        float headRotateAngleY = netHeadYaw * PI / 180;
        float headRotateAngleX = headPitch * PI / 180;

        final float max = 0.5f;
        final float min = -1.25f;
        headRotateAngleX = Math.min(headRotateAngleX, max);
        headRotateAngleX = Math.max(headRotateAngleX, min);
        this.illagerHead.rotateAngleY = headRotateAngleY;
        this.illagerHead.rotateAngleX = headRotateAngleX;

        this.tail.rotateAngleZ = MathHelper.cos(limbSwing * 0.8F) * 0.2F * limbSwingAmount;

        float bodySwingRotation = 0.0F;
        if (this.swingProgress > -9990.0F) {
            bodySwingRotation = MathHelper.sin(MathHelper.sqrt(this.swingProgress) * PI * 2.0F) * 0.2F;
        }

        this.illagerBody.rotateAngleY = bodySwingRotation * 0.2F;
        this.illagerBody.rotateAngleY = bodySwingRotation * 0.2F;


        this.tail.rotateAngleY = bodySwingRotation;


        this.setLegs(limbSwing, limbSwingAmount);
        this.tail.rotateAngleX = 0.5F * limbSwingAmount;


        this.tail.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;


    }

    private void setLegs(float move, float swing) {
        this.rotateLegs(move, swing);
//        this.adjustLegs();
    }

    private void rotateLegs(float move, float swing) {
        final float PI = (float) Math.PI;
        float swag = (float) Math.pow(swing, 16.0D);
        float raQuad = PI * swag * 0.5F;
        float laQuad = PI * swag;
        float rlQuad = PI * swag * 0.2F;
        float llQuad = PI * swag * -0.4F;


        this.rightForeLeg.rotateAngleX = MathHelper.cos(move * 0.6662F + (float) Math.PI + raQuad) * 0.45F * swing;
        this.leftForeLeg.rotateAngleX = MathHelper.cos(move * 0.6662F + laQuad) * 0.45F * swing;
        this.rightHindLeg.rotateAngleX = MathHelper.cos(move * 0.6662F + rlQuad) * 0.45F * swing;
        this.leftHindLeg.rotateAngleX = MathHelper.cos(move * 0.6662F + PI + llQuad) * 0.45F * swing;
        this.rightForeLeg.rotateAngleZ = 0.0F;

        this.leftForeLeg.rotateAngleZ = 0.0F;
    }

    public ModelRenderer getArm(EnumHandSide side) {
        return side == EnumHandSide.LEFT ? this.leftForeLeg : this.rightForeLeg;
    }
}
