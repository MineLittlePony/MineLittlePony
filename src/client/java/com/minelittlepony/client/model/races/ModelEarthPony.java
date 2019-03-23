package com.minelittlepony.client.model.races;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.util.render.PonyRenderer;

import net.minecraft.entity.Entity;

public class ModelEarthPony extends AbstractPonyModel {

    private final boolean smallArms;

    public PonyRenderer bipedCape;

    public ModelEarthPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        if (bipedCape != null) {
            bipedCape.rotationPointY = isSneak ? 2 : isRiding ? -4 : 0;
        }
    }

    @Override
    protected float getLegOutset() {
        if (smallArms) {
            if (isSleeping()) return 2.6f;
            if (isCrouching()) return 1;
            return 4;
        }
        return super.getLegOutset();
    }

    @Override
    protected int getArmWidth() {
        return smallArms ? 3 : super.getArmWidth();
    }

    @Override
    protected float getLegRotationX() {
        return smallArms ? 2 : super.getLegRotationX();
    }

    @Override
    protected float getArmRotationY() {
        return smallArms ? 8.5f : super.getArmRotationY();
    }

    @Override
    protected void initHead(float yOffset, float stretch) {
        super.initHead(yOffset, stretch);
        bipedCape = new PonyRenderer(this, 0, 0)
                .size(64, 32).box(-5, 0, -1, 10, 16, 1, stretch);
    }

    @Override
    public void renderCape(float scale) {
        bipedCape.render(scale);
    }
}
