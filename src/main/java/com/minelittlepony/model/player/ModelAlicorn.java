package com.minelittlepony.model.player;

import com.minelittlepony.model.components.PegasusWings;
import net.minecraft.entity.Entity;

import com.minelittlepony.model.capabilities.IModelPegasus;

public class ModelAlicorn extends ModelUnicorn implements IModelPegasus {

    public PegasusWings wings;

    public ModelAlicorn(boolean smallArms) {
        super(smallArms);
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        wings = new PegasusWings(this, yOffset, stretch);
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        if (canFly()) {
            wings.setRotationAndAngles(rainboom, move, swing, 0, ticks);
            saddlebags.sethangingLow(wingsAreOpen());
        }
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);
        if (canFly()) {
            wings.renderPart(scale);
        }
    }
}
