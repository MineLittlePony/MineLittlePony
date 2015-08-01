package com.minelittlepony.minelp.model.pony;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.minelp.model.ModelPony;
import com.minelittlepony.minelp.renderer.AniParams;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class pm_Human extends ModelPony {

    public ModelRenderer bipedEars;
    public ModelRenderer cloak;

    public pm_Human(String texture) {
        super(texture);
    }

    @Override
    public void init(float yoffset, float stretch) {
        this.cloak = new ModelRenderer(this, 0, 0);
        this.cloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, stretch);
        this.bipedEars = new ModelRenderer(this, 24, 0);
        this.bipedEars.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, stretch);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, stretch);
        this.bipedHead.setRotationPoint(0.0F, 0.0F + yoffset, 0.0F);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, stretch + 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + yoffset, 0.0F);
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, stretch);
        this.bipedBody.setRotationPoint(0.0F, 0.0F + yoffset, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, stretch);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + yoffset, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, stretch);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + yoffset, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, stretch);
        this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F + yoffset, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, stretch);
        this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F + yoffset, 0.0F);
    }

    @Override
    public void animate(AniParams ani) {}

    @Override
    public void render(AniParams ani) {}

    @Override
    protected boolean doCancelRender() {
        return true;
    }


    @Override
    public void renderDrop(RenderManager rendermanager, ItemRenderer itemrenderer, EntityLivingBase entity) {
        this.renderDrop(itemrenderer, entity, this.bipedRightArm, 1.0F, -0.0625F, 0.4375F, 0.0625F);
    }

    @Override
    public void renderEars(EntityLivingBase entity, float par2) {
        for (int i = 0; i < 2; ++i) {
            float f1 = entity.renderYawOffset + (entity.prevRenderYawOffset - entity.renderYawOffset) * par2
                    - (entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * par2);
            float f2 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par2;
            GL11.glPushMatrix();
            GL11.glRotatef(f1, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.375F * (i * 2 - 1), 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.375F, 0.0F);
            GL11.glRotatef(-f2, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-f1, 0.0F, 1.0F, 0.0F);
            float f7 = 1.333333F;
            GL11.glScalef(f7, f7, f7);
            this.bipedEars.rotateAngleY = this.bipedHead.rotateAngleY;
            this.bipedEars.rotateAngleX = this.bipedHead.rotateAngleX;
            this.bipedEars.rotationPointX = 0.0F;
            this.bipedEars.rotationPointY = 0.0F;
            this.bipedEars.render(0.0625F);
            GL11.glPopMatrix();
        }

    }

    @Override
    public void renderCloak(EntityPlayer player, float par2) {
        this.renderCape(par2);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 0.125F);
        double d = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * par2
                - (player.prevPosX + (player.posX - player.prevPosX) * par2);
        double d1 = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * par2
                - (player.prevPosY + (player.posY - player.prevPosY) * par2);
        double d2 = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * par2
                - (player.prevPosZ + (player.posZ - player.prevPosZ) * par2);
        float f10 = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * par2;
        double d3 = MathHelper.sin(f10 * 3.1415927F / 180.0F);
        double d4 = (-MathHelper.cos(f10 * 3.1415927F / 180.0F));
        float f12 = (float) d1 * 10.0F;
        if (f12 < -6.0F) {
            f12 = -6.0F;
        }

        if (f12 > 32.0F) {
            f12 = 32.0F;
        }

        float f13 = (float) (d * d3 + d2 * d4) * 100.0F;
        float f14 = (float) (d * d4 - d2 * d3) * 100.0F;
        if (f13 < 0.0F) {
            f13 = 0.0F;
        }

        float f15 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * par2;
        f12 += MathHelper.sin((player.prevDistanceWalkedModified
                + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * par2) * 6.0F) * 32.0F * f15;
        if (player.isSneaking()) {
            f12 += 25.0F;
        }

        GL11.glRotatef(6.0F + f13 / 2.0F + f12, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(f14 / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-f14 / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        this.cloak.render(0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderStaticCloak(EntityLiving player, float par2) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 0.125F);
        GL11.glRotatef(3.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        this.cloak.render(0.0625F);
        GL11.glPopMatrix();
    }
}
