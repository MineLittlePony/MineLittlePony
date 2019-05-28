package com.minelittlepony.client.model.races;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.util.render.PonyRenderer;

import net.minecraft.entity.LivingEntity;

public class ModelEarthPony<T extends LivingEntity> extends AbstractPonyModel<T> {

    private final boolean smallArms;

    public PonyRenderer bipedCape;

    public ModelEarthPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (bipedCape != null) {
            bipedCape.rotationPointY = isSneaking ? 2 : isRiding ? -4 : 0;
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
