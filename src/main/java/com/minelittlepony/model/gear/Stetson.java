package com.minelittlepony.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.pony.data.PonyWearable;
import com.minelittlepony.render.model.PlaneRenderer;
import com.minelittlepony.render.model.PonyRenderer;

public class Stetson extends AbstractGear implements IStackable {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minelittlepony", "textures/models/stetson.png");

    private PlaneRenderer rimshot;

    @Override
    public void init(float yOffset, float stretch) {
        rimshot = new PlaneRenderer(this).size(64, 64)
                .tex(16, 33).top(-9, yOffset - 4, -12, 16, 17, stretch)
                .tex(0, 33).bottom(-9, yOffset - 3.999F, -12, 16, 17, stretch)
                .rotate(-0.3F, 0, 0.1F)
                .child(new PonyRenderer(this).size(64, 64)
                    .tex(0, 0).box(-5, yOffset - 8, -6, 9, 4, 9, stretch)
                    .tex(0, 13).box(-6, yOffset - 6, -7, 11, 2, 11, stretch));

        rimshot.child()
            .around(-9, yOffset - 4, -12)
            .tex(0, 27).south(0, yOffset - 6, 0, 16, 6, stretch)
            .rotate(0.4F, 0, 0);
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public ResourceLocation getTexture(Entity entity) {
        return TEXTURE;
    }

    @Override
    public void renderPart(float scale) {
        rimshot.render(scale);
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(PonyWearable.STETSON);
    }

    @Override
    public float getStackingOffset() {
        return 0;
    }
}
