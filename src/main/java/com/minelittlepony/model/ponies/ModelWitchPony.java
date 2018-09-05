package com.minelittlepony.model.ponies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.model.player.ModelZebra;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.render.model.PonyRenderer;

public class ModelWitchPony extends ModelZebra {

    private static final ResourceLocation WITCH_TEXTURES = new ResourceLocation("textures/entity/witch.png");

    private PonyRenderer witchHat;

    public ModelWitchPony() {
        super(false);
    }

    @Override
    public void updateLivingState(EntityLivingBase entity, IPony pony) {
        super.updateLivingState(entity, pony);
        EntityWitch witch = ((EntityWitch) entity);

        if ("Filly".equals(entity.getCustomNameTag())) {
            isChild = true;
        }
        leftArmPose = ArmPose.EMPTY;
        rightArmPose = witch.getHeldItemMainhand().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        if (((EntityWitch)entity).isDrinkingPotion()) {
            float noseRot = MathHelper.sin(entity.ticksExisted);

            snout.rotate(noseRot * 4.5F * 0.02F, 0, noseRot * 2.5F * 0.02F);
        } else {
            snout.rotate(0, 0, 0);
        }


        if (rightArmPose != ArmPose.EMPTY) {
            float rot = (float)(Math.tan(ticks / 7) + Math.sin(ticks / 3));
            if (rot > 1) rot = 1;
            if (rot < -1) rot = -1;

            float legDrinkingAngle = -1 * PI/3 + rot;

            bipedRightArm.rotateAngleX = legDrinkingAngle;
            bipedRightArmwear.rotateAngleX = legDrinkingAngle;
            bipedRightArm.rotateAngleY = 0.1F;
            bipedRightArmwear.rotateAngleY = 0.1F;
            bipedRightArm.offsetZ = 0.1f;
            bipedRightArmwear.offsetZ = 0.1f;

            if (rot > 0) rot = 0;

            bipedHead.rotateAngleX = -rot / 2;
            bipedHeadwear.rotateAngleX = -rot / 2;
        } else {
            bipedRightArm.offsetZ = 0;
            bipedRightArmwear.offsetZ = 0;
        }


    }

    @Override
    public boolean isChild() {
        return isChild;
    }

    @Override
    protected void renderHead(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderHead(entity, move, swing, ticks, headYaw, headPitch, scale);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        TextureManager tex = Minecraft.getMinecraft().getRenderManager().renderEngine;
        tex.bindTexture(WITCH_TEXTURES);
        witchHat.render(scale * 1.3F);

        GlStateManager.popAttrib();
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        witchHat = new PonyRenderer(this).size(64, 128);
        witchHat.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                .tex(0, 64).box(-5, -6, -7, 10, 2, 10, stretch)
                .child(0).around(1.75F, -4, 2)
                    .tex(0, 76).box(-5, -5, -7, 7, 4, 7, stretch)
                    .rotate(-0.05235988F, 0, 0.02617994F)
                    .child(0).around(1.75F, -4, 2)
                        .tex(0, 87).box(-5, -4, -7, 4, 4, 4, stretch)
                        .rotate(-0.10471976F, 0, 0.05235988F)
                        .child(0).around(1.75F, -2, 2)
                            .tex(0, 95).box(-5, -2, -7, 1, 2, 1, stretch)
                            .rotate(-0.20943952F, 0, 0.10471976F);
    }
}
