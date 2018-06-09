package com.minelittlepony.model.components;

import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPart;
import com.minelittlepony.model.capabilities.IModelPegasus;
import com.minelittlepony.pony.data.PonyWearable;

public class PegasusWings<T extends AbstractPonyModel & IModelPegasus> implements IModelPart {

    private final T pegasus;

    private ModelWing leftWing;
    private ModelWing rightWing;

    private ModelWing legacyWing;

    public PegasusWings(T model, float yOffset, float stretch) {
        pegasus = model;

        init(yOffset, stretch);
    }

    @Override
    public void init(float yOffset, float stretch) {
        int x = 57;

        leftWing = new ModelWing(pegasus, false, yOffset, stretch, x, 32);
        rightWing = new ModelWing(pegasus, true, yOffset, stretch, x, 16);

        legacyWing = new ModelWing(pegasus, true, yOffset, stretch, x - 1, 32);
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

            float mve = move * 0.6662f; // magic number ahoy (actually 2/3)
            float srt = swing / 4;

            flap = MathHelper.cos(mve + pi) * srt;
        }

        getLeft().rotateWalking(flap);
        getRight().rotateWalking(-flap);

        if (pegasus.wingsAreOpen()) {
            float flapAngle = pegasus.getWingRotationFactor(ticks);
            if (pegasus.isWearing(PonyWearable.SADDLE_BAGS) && (pegasus.isSwimming() || pegasus.isFlying())) {
                flapAngle -= 1F;
            }
            getLeft().rotateFlying(flapAngle);
            getRight().rotateFlying(-flapAngle);
        }

    }

    @Override
    public void renderPart(float scale) {
        getLeft().render(scale, false);
        getRight().render(scale, true);
    }
}
