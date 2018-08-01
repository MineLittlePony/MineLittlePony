package com.minelittlepony.model.player;

import com.minelittlepony.render.PonyRenderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelZebra extends ModelEarthPony {

    public PonyRenderer bristles;

    public ModelZebra(boolean useSmallArms) {
        super(useSmallArms);
    }

    @Override
    protected void renderHead(Entity entity, float move, float swing, float age, float headYaw, float headPitch, float scale) {
        GlStateManager.translate(0, -0.1F, 0);
        super.renderHead(entity, move, swing, age, headYaw, headPitch, scale);
    }

    @Override
    protected void renderNeck(float scale) {
        GlStateManager.scale(1, 1.1F, 1);
        super.renderNeck(scale);
    }

    @Override
    protected void initHead(float yOffset, float stretch) {
        super.initHead(yOffset, stretch);

        bristles = new PonyRenderer(this, 56, 32);
        bipedHead.addChild(bristles);

        bristles.offset(-1, -1, -3)
        .box(0, -10, 2, 2, 6, 2, stretch)
        .box(0, -10, 4, 2, 8, 2, stretch)
        .box(0, -8, 6, 2, 6, 2, stretch)
        .rotateAngleX = 0.3F;
        bristles.child(0).offset(-1.01F, 2, -7) //0.01 to prevent z-fighting
        .box(0, -10, 4, 2, 8, 2, stretch)
        .box(0, -8, 6, 2, 6, 2, stretch)
        .rotateAngleX = -1F;
    }
}
