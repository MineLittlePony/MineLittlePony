package com.minelittlepony.model.pony;

import com.minelittlepony.renderer.HornGlowRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEvoker;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.GlStateManager.*;

public class ModelEvokerPony extends ModelIllagerPony {

    private HornGlowRenderer[] hornglow;

    public ModelEvokerPony() {
        hornglow = new HornGlowRenderer[2];
        for (int i = 0; i < hornglow.length; i++) {
            hornglow[i] = new HornGlowRenderer(this, 60, 3);
            hornglow[i].setRotationPoint(0F, 1F, -5F);
        }
        hornglow[0].addBox(-0.5F, -12.0F, 3F, 1, 4, 1, 0.5F);
        hornglow[1].addBox(-0.5F, -12.0F, 3F, 1, 3, 1, 0.8F);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        EntityEvoker evoker = (EntityEvoker) entityIn;

        if (isUnicorn && evoker.isCastingSpell()) {
            GL11.glPushAttrib(24577);
            disableTexture2D();
            disableLighting();
            enableBlend();

            float red = (glowColor >> 16 & 255) / 255.0F;
            float green = (glowColor >> 8 & 255) / 255.0F;
            float blue = (glowColor & 255) / 255.0F;
            blendFunc(GL11.GL_SRC_ALPHA, 1);

            this.illagerHead.postRender(scale);
            this.horn.postRender(scale);

            color(red, green, blue, 0.4F);
            this.hornglow[0].render(scale);
            color(red, green, blue, 0.2F);
            this.hornglow[1].render(scale);

            enableTexture2D();
            enableLighting();
            disableBlend();
            popAttrib();
        }
    }

}
