package com.minelittlepony.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.pony.data.PonyWearable;
import com.minelittlepony.render.model.PonyRenderer;

public class Muffin extends AbstractGear {

    private static final ResourceLocation TEXTURE = new ResourceLocation("minelittlepony", "textures/models/muffin.png");

    private PonyRenderer crown;

    @Override
    public void init(float yOffset, float stretch) {
        crown = new PonyRenderer(this, 0, 0).size(64, 44)
                .around(-4, -12, -6)
                .box(0, 0, 0, 8, 4, 8, stretch)
                .box(3, -1.5F, 3, 2, 2, 2, stretch)
                .tex(0, 12).box(1.5F, -1, 1.5F, 5, 1, 5, stretch)
                .tex(0, 18).box(2, 1, 1, 4, 7, 6, stretch)
                .tex(0, 18).box(1, 1, 2, 6, 7, 4, stretch);
    }

    @Override
    public void renderPart(float scale) {
        crown.render(scale);
    }

    @Override
    public void setLivingAnimations(IModel model, Entity entity) {
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(PonyWearable.MUFFIN);
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public ResourceLocation getTexture(Entity entity) {
        return TEXTURE;
    }
}
