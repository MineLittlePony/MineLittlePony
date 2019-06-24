package com.minelittlepony.client.model.races;

import com.minelittlepony.client.model.components.PegasusWings;
import com.minelittlepony.model.IPegasus;

import net.minecraft.entity.LivingEntity;

public class ModelAlicorn<T extends LivingEntity> extends ModelUnicorn<T> implements IPegasus {

    public PegasusWings<ModelAlicorn<T>> wings;

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
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (canFly()) {
            wings.setRotationAndAngles(attributes.isGoingFast, attributes.interpolatorId, move, swing, 0, ticks);
        }
    }

    @Override
    protected void renderBody(float scale) {
        super.renderBody(scale);

        if (canFly()) {
            wings.renderPart(scale, attributes.interpolatorId);
        }
    }
}
