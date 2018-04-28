package com.minelittlepony.model.player;

import com.minelittlepony.model.components.PegasusWings;
import net.minecraft.entity.Entity;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.capabilities.IModelPegasus;

public class ModelPegasus extends ModelEarthPony implements IModelPegasus {

    public PegasusWings wings;

    public ModelPegasus(boolean smallArms) {
        super(smallArms);
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        wings = new PegasusWings(this, yOffset, stretch);
    }

    @Override
    public boolean isCrouching() {
        return super.isCrouching() && !rainboom;
    }

    @Override
    public void setRotationAngles(float move, float swing, float age, float headYaw, float headPitch, float scale, Entity entity) {
        checkRainboom(entity, swing);

        super.setRotationAngles(move, swing, age, headYaw, headPitch, scale, entity);

        if (bipedCape != null) {
            wings.setRotationAngles(move, swing, age);
        }
    }

    @Override
    protected void rotateLegsInFlight(float move, float swing, float tick, Entity entity) {
        if (rainboom) {
            bipedLeftArm.rotateAngleX = ROTATE_270;
            bipedRightArm.rotateAngleX = ROTATE_270;

            bipedLeftLeg.rotateAngleX = ROTATE_90;
            bipedRightLeg.rotateAngleX = ROTATE_90;

            bipedLeftArm.rotateAngleY = -0.2F;
            bipedLeftLeg.rotateAngleY = 0.2F;

            bipedRightArm.rotateAngleY = 0.2F;
            bipedRightLeg.rotateAngleY = -0.2F;
        } else {
            super.rotateLegsInFlight(move, swing, tick, entity);
        }
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float age, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, age, headYaw, headPitch, scale);
        if (canFly()) {
            wings.render(scale);
        }
    }

    @Override
    public boolean wingsAreOpen() {
        return isFlying || isCrouching();
    }

    @Override
    public boolean canFly() {
        return metadata.getRace().hasWings();
    }
}
