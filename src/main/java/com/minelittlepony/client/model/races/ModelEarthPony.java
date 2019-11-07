package com.minelittlepony.client.model.races;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.util.render.Part;

import net.minecraft.entity.LivingEntity;

public class ModelEarthPony<T extends LivingEntity> extends AbstractPonyModel<T> {

    private final boolean smallArms;

    public Part bipedCape;

    public ModelEarthPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;

        if (smallArms) {
            attributes.armWidth = 3;
            attributes.armRotationX = 2F;
            attributes.armRotationY = 8.5F;
        }
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
            if (attributes.isSleeping) return 2.6f;
            if (attributes.isCrouching) return 1;
            return 4;
        }
        return super.getLegOutset();
    }

    @Override
    protected void initHead(float yOffset, float stretch) {
        super.initHead(yOffset, stretch);
        bipedCape = new Part(this, 0, 0)
                .size(64, 32).box(-5, 0, -1, 10, 16, 1, stretch);
    }

    @Override
    public void renderCape(float scale) {
        bipedCape.render(scale);
    }
}
