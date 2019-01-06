package com.minelittlepony.model.player;

import com.minelittlepony.model.components.PegasusWings;
import net.minecraft.entity.Entity;

import com.minelittlepony.model.capabilities.IModelPegasus;

public class ModelPegasus extends ModelEarthPony implements IModelPegasus {

    public PegasusWings<ModelPegasus> wings;

    public ModelPegasus(boolean smallArms) {
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
        wings.setRotationAndAngles(rainboom, entity.getUniqueID(), move, swing, 0, ticks);
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);
        wings.renderPart(scale, entity.getUniqueID());
    }
}
