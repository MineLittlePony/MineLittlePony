package com.minelittlepony.model.player;

import com.minelittlepony.model.components.PegasusWings;
import com.minelittlepony.render.PonyRenderer;

import net.minecraft.entity.Entity;

import com.minelittlepony.model.capabilities.IModelPegasus;

public class ModelBatpony extends ModelEarthPony implements IModelPegasus {

    public PegasusWings<ModelBatpony> wings;

    public ModelBatpony(boolean smallArms) {
        super(smallArms);
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        wings = new PegasusWings<>(this, yOffset, stretch);
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);
        wings.setRotationAndAngles(rainboom, move, swing, 0, ticks);
        saddlebags.sethangingLow(wingsAreOpen());
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);
        wings.renderPart(scale);
    }


    @Override
    protected void initEars(PonyRenderer head, float yOffset, float stretch) {
        head.child()
            .rotate(0, 0, -0.1F)
            .tex(14, 16).box(-4, -6, 1, 1, 2, 2, stretch)  // right ear
              .tex(0, 3).box(-4, -6, 2.5F, 1, 1, 1, stretch)
              .tex(0, 5).box(-4, -6.7F, 2, 1, 1, 1, stretch);

        head.child().flip()
            .rotate(0, 0, 0.1F)
            .tex(14, 16).box( 3, -6, 1, 1, 2, 2, stretch)  // left ear
              .tex(0, 3).box( 3, -6, 2.5F, 1, 1, 1, stretch)
              .tex(0, 5).box( 3, -6.7F, 2, 1, 1, 1, stretch);
    }
}
