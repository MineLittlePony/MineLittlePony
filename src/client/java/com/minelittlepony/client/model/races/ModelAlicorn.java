package com.minelittlepony.client.model.races;

import com.minelittlepony.client.model.components.PegasusWings;
import com.minelittlepony.common.model.IPegasus;

import net.minecraft.entity.Entity;

public class ModelAlicorn extends ModelUnicorn implements IPegasus {

    public PegasusWings<ModelAlicorn> wings;

    public ModelAlicorn(boolean smallArms) {
        super(smallArms);
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        initWings(yOffset, stretch);
    }

    protected void initWings(float yOffset, float stretch) {
        wings = new PegasusWings<>(this, yOffset, stretch);
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        if (canFly()) {
            wings.setRotationAndAngles(rainboom, entity.getUniqueID(), move, swing, 0, ticks);
        }
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);
        if (canFly()) {
            wings.renderPart(scale, entity.getUniqueID());
        }
    }
}
