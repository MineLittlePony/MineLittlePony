package com.minelittlepony.model.player;

import com.minelittlepony.model.components.BatWings;
import com.minelittlepony.model.components.PegasusWings;
import com.minelittlepony.pony.data.PonyWearable;
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
        wings = new BatWings<>(this, yOffset, stretch);
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
            .tex(14, 16).box(-4, -6, 0.5F, 1, 2, 2, stretch)  // right ear
              .tex(0, 3).box(-4, -6.49F, 2.49F, 1, 1, 1, stretch)
              .tex(0, 5).box(-3, -5, 1.5F, 1, 1, 1, stretch);

        head.child().flip()
            .tex(14, 16).box( 3, -6, 0.5F, 1, 2, 2, stretch)  // left ear
              .tex(0, 3).box( 3, -6.49F, 2.49F, 1, 1, 1, stretch)
              .tex(0, 5).box( 2, -5, 1.5F, 1, 1, 1, stretch);
    }

    @Override
    public boolean isWearing(PonyWearable wearable) {
        if (wearable == PonyWearable.SADDLE_BAGS) {
            return false;
        }

        return super.isWearing(wearable);
    }
}
