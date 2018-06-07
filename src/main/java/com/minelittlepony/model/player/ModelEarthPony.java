package com.minelittlepony.model.player;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.components.SaddleBags;
import com.minelittlepony.pony.data.PonyWearable;
import com.minelittlepony.render.PonyRenderer;
import net.minecraft.entity.Entity;

public class ModelEarthPony extends AbstractPonyModel {

    private final boolean smallArms;

    public SaddleBags saddlebags;

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
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        super.shakeBody(move, swing, bodySwing, ticks);
        saddlebags.setRotationAndAngles(rainboom, move, swing, bodySwing, ticks);
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (isWearing(PonyWearable.SADDLE_BAGS)) {
            saddlebags.renderPart(scale);
        }
    }

    @Override
    protected void initTextures() {
        super.initTextures();
        saddlebags = new SaddleBags(this);
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        super.initPositions(yOffset, stretch);

        saddlebags.init(yOffset, stretch);
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
