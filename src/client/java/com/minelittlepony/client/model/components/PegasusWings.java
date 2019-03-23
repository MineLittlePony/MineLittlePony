package com.minelittlepony.client.model.components;

import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.model.IPart;
import com.minelittlepony.model.IPegasus;
import com.minelittlepony.pony.meta.Wearable;

import java.util.UUID;

import static com.minelittlepony.model.PonyModelConstants.*;

public class PegasusWings<T extends AbstractPonyModel & IPegasus> implements IPart {

    protected final T pegasus;

    protected ModelWing<T> leftWing;
    protected ModelWing<T> rightWing;

    protected ModelWing<T> legacyWing;

    public PegasusWings(T model, float yOffset, float stretch) {
        pegasus = model;

        init(yOffset, stretch);
    }

    @Override
    public void init(float yOffset, float stretch) {
        leftWing = new ModelWing<>(pegasus, false, false, yOffset, stretch, 32);
        rightWing = new ModelWing<>(pegasus, true, false, yOffset, stretch, 16);

        legacyWing = new ModelWing<>(pegasus, true, true, yOffset, stretch, 32);
    }

    public ModelWing<T> getLeft() {
        return leftWing;
    }

    public ModelWing<T> getRight() {
        return pegasus.isWearing(Wearable.SADDLE_BAGS) ? legacyWing : rightWing;
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
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

        float flapAngle = ROTATE_270;

        if (pegasus.wingsAreOpen()) {
            flapAngle = pegasus.getWingRotationFactor(ticks);
            if (!pegasus.isCrouching() && pegasus.isWearing(Wearable.SADDLE_BAGS)) {
                flapAngle -= 1F;
            }
        }

        if (!pegasus.isFlying()) {
            flapAngle = pegasus.getMetadata().getInterpolator(interpolatorId).interpolate("wingFlap", flapAngle, 10);
        }

        getLeft().rotateFlying(flapAngle);
        getRight().rotateFlying(-flapAngle);

    }

    @Override
    public void renderPart(float scale, UUID interpolatorId) {
        getLeft().render(scale);
        getRight().render(scale);
    }
}
