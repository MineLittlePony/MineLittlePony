package com.minelittlepony.model.components;

import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPart;
import com.minelittlepony.model.capabilities.IModelPegasus;
import com.minelittlepony.pony.data.PonyWearable;

public class PegasusWings implements IModelPart {

    private final IModelPegasus pegasus;

    private final ModelWing leftWing;
    private final ModelWing rightWing;

    private final ModelWing legacyWing;

    public <T extends AbstractPonyModel & IModelPegasus> PegasusWings(T model, float yOffset, float stretch) {
        pegasus = model;

        leftWing = new ModelWing(model, false, 4, yOffset, stretch, 32);
        rightWing = new ModelWing(model, true, -6, yOffset, stretch, 16);
        legacyWing = new ModelWing(model, true, -6, yOffset, stretch, 32);
    }


    @Override
    public void init(float yOffset, float stretch) {

    }

    public ModelWing getLeft() {
        return leftWing;
    }

    public ModelWing getRight() {
        return pegasus.isWearing(PonyWearable.SADDLE_BAGS) ? legacyWing : rightWing;
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, float move, float swing, float bodySwing, float ticks) {
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

        getLeft().rotateWalking(flap);
        getRight().rotateWalking(-flap);

        if (pegasus.wingsAreOpen()) {
            float flapAngle = pegasus.getWingRotationFactor(ticks);
            getLeft().rotateFlying(flapAngle);
            getRight().rotateFlying(-flapAngle);
        }

    }

    @Override
    public void renderPart(float scale) {
        boolean standing = pegasus.wingsAreOpen();
        getLeft().render(standing, scale);
        getRight().render(standing, scale);
    }
}
