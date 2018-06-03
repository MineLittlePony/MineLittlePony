package com.minelittlepony.model.player;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.components.PonyAccessory;
import com.minelittlepony.render.PonyRenderer;

import net.minecraft.entity.Entity;

public class ModelEarthPony extends AbstractPonyModel {

    private final boolean smallArms;

    public PonyRenderer bipedCape;
    public PonyAccessory accessory;

    public ModelEarthPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        if (metadata.hasAccessory()) {
            accessory = new PonyAccessory(this, yOffset, stretch);
        }
    }

    @Override
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        super.shakeBody(move, swing, bodySwing, ticks);
        if (accessory != null && metadata.hasAccessory()) {
            accessory.shakeBody(bodySwing);
        }
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);
        if (accessory != null && metadata.hasAccessory()) {
            accessory.render(scale);
        }
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        if (bipedCape != null) {
            bipedCape.rotationPointY = isSneak ? 2 : isRiding ? -4 : 0;
        }
    }

    protected float getLegOutset() {
        if (smallArms) {
            if (isSleeping) return 2.6f;
            if (isCrouching()) return 1;
            return 4;
        }
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

    protected void initHeadTextures() {
        super.initHeadTextures();
        bipedCape = new PonyRenderer(this, 0, 0).size(64, 32);
    }

    protected void initHeadPositions(float yOffset, float stretch) {
        super.initHeadPositions(yOffset, stretch);
        bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, stretch);
    }

    @Override
    public void renderCape(float scale) {
        bipedCape.render(scale);
    }
}
