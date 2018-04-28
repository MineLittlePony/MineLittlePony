package com.minelittlepony.model.components;

import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPegasus;

public class PegasusWings {

    private final IModelPegasus pegasus;

    public final ModelWing leftWing;
    public final ModelWing rightWing;

    public <T extends AbstractPonyModel & IModelPegasus> PegasusWings(T model, float yOffset, float stretch) {
        pegasus = model;

        leftWing = new ModelWing(model, false, 4f, yOffset, stretch, 32);
        rightWing = new ModelWing(model, true, -6f, yOffset, stretch, 16);
    }

    public void setRotationAngles(float move, float swing, float ticks) {
        float flap = 0;
        float progress = pegasus.getSwingAmount();

        if (progress > 0) {
            flap = MathHelper.sin(MathHelper.sqrt(progress) * PI * 2);
        } else {
            float pi = PI * (float) Math.pow(swing, 16);

            float mve = move * 0.6662f; // magic number ahoy
            float srt = swing / 4;

            flap = MathHelper.cos(mve + pi) * srt;
        }

        leftWing.rotateWalking(flap);
        rightWing.rotateWalking(-flap);

        if (pegasus.wingsAreOpen()) {
            float flapAngle = getWingRotationFactor(ticks);
            leftWing.rotateFlying(flapAngle);
            rightWing.rotateFlying(-flapAngle);
        }

    }

    public float getWingRotationFactor(float ticks) {
        if (pegasus.isFlying()) {
            return (MathHelper.sin(ticks * 0.536f) * 1) + ROTATE_270 + 0.4f;
        }
        return LEFT_WING_ROTATE_ANGLE_Z_SNEAK;
    }

    public void render(float scale) {
        boolean standing = pegasus.wingsAreOpen();
        leftWing.render(standing, scale);
        rightWing.render(standing, scale);
    }
}
