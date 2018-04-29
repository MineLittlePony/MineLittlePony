package com.minelittlepony.model.player;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.render.PonyRenderer;

import net.minecraft.entity.Entity;

public class ModelEarthPony extends AbstractPonyModel {

    private final boolean smallArms;

    public PonyRenderer bipedCape;

    public ModelEarthPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;
    }

    @Override
    public void setRotationAngles(float move, float swing, float age, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, age, headYaw, headPitch, scale, entity);

        bipedCape.rotationPointY = isSneak ? 2 : isRiding ? -4 : 0;
    }

    protected float getLegOutset() {
        if (isCrouching() && smallArms) return 1;
        return super.getLegOutset();
    }

    protected int getArmWidth() {
        return smallArms ? 3 : super.getArmWidth();
    }

    protected float getLegRotationX() {
        return smallArms ? 2 : super.getLegRotationX();
    }

    protected float getArmRotationY() {
        return smallArms ? 8.5f : super.getArmRotationY();
    }

    protected void adjustLegs(float move, float swing, float tick) {
        super.adjustLegs(move, swing, tick);
        // Push the front legs back apart if we're a thin pony
        if (smallArms) {
            bipedLeftArm.rotationPointX++;
            bipedLeftLeg.rotationPointX++;
        }
    }

    protected void initHeadTextures() {
        super.initHeadTextures();
        bipedCape = new PonyRenderer(this, 0, 0).size(64, 32);
    }

    protected void initHeadPositions(float yOffset, float stretch) {
        super.initHeadPositions(yOffset, stretch);
        bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, stretch);
    }

    protected void initLegPositions(float yOffset, float stretch) {
        super.initLegPositions(yOffset, stretch);
        if (smallArms) {

        }
    }

    @Override
    public void renderCape(float scale) {
        bipedCape.render(scale);
    }
}
