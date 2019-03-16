package com.minelittlepony.model.ponies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;

import java.util.Random;

public class ModelFools extends AbstractPonyModel {

    private static final ResourceLocation CHICKEN_TEXTURES = new ResourceLocation("textures/entity/chicken.png");

    private ModelChicken chicken = new ModelChicken();

    public ModelFools(boolean arms) {
        super(arms);
    }

    @Override
    public void init(float y, float scale) {
        chicken = new ModelChicken();
    }

    @Override
    public void render(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {

        GlStateManager.pushMatrix();
        transform(BodyPart.BODY);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GlStateManager.scale(4, 4, 4);
        GlStateManager.translate(0, -1.15F, 0);

        long factor = entity.getUniqueID().getMostSignificantBits();
        Random rand = new Random(factor);

        GlStateManager.color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

        TextureManager tex = Minecraft.getMinecraft().getRenderManager().renderEngine;
        tex.bindTexture(CHICKEN_TEXTURES);

        chicken.render(entity, move, swing, ticks, headYaw, headPitch, scale);

        GL11.glPopAttrib();
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        chicken.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    }

}
