package com.minelittlepony.client.model.components;

import net.minecraft.client.model.Model;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.IPart;
import com.minelittlepony.model.IPegasus;
import com.minelittlepony.pony.meta.Wearable;
import com.mojang.blaze3d.platform.GlStateManager;

import java.util.UUID;

public class PegasusWings<T extends Model & IPegasus> implements IPart {

    protected final T pegasus;

    protected Wing leftWing;
    protected Wing rightWing;

    protected Wing legacyWing;

    public PegasusWings(T model, float yOffset, float stretch) {
        pegasus = model;

        init(yOffset, stretch);
    }

    @Override
    public void init(float yOffset, float stretch) {
        leftWing = new Wing(pegasus, false, false, yOffset, stretch, 32);
        rightWing = new Wing(pegasus, true, false, yOffset, stretch, 16);

        legacyWing = new Wing(pegasus, true, true, yOffset, stretch, 32);
    }

    public Wing getLeft() {
        return leftWing;
    }

    public Wing getRight() {
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
            if (!pegasus.getAttributes().isCrouching && pegasus.isWearing(Wearable.SADDLE_BAGS)) {
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

    public class Wing {

        protected final T pegasus;

        protected final PonyRenderer extended;
        protected final PonyRenderer folded;

        public Wing(T pegasus, boolean right, boolean legacy, float y, float scale, int texY) {
            this.pegasus = pegasus;

            folded = new PonyRenderer(pegasus, 56, texY).mirror(legacy);
            extended = new PonyRenderer(pegasus, 56 + ((!right || legacy) ? 1 : 0), texY + 3);

            addClosedWing(right, y, scale);
            addFeathers(right, legacy, y, scale);
        }

        protected void addClosedWing(boolean right, float y, float scale) {
            float x = right ? -6 : 4;

            folded.around(HEAD_RP_X, WING_FOLDED_RP_Y + y, WING_FOLDED_RP_Z)
                  .box(x, 5, 2, 2, 6, 2, scale)
                  .box(x, 5, 4, 2, 8, 2, scale)
                  .box(x, 5, 6, 2, 6, 2, scale)
                  .pitch = ROTATE_90;
        }

        protected void addFeathers(boolean right, boolean l, float rotationPointY, float scale) {
            float r = right ? -1 : 1;

            extended.around(r * EXT_WING_RP_X, EXT_WING_RP_Y + rotationPointY, EXT_WING_RP_Z)
                    .yaw = r * 3;
            addFeather(0, l,  6,     0,    9, scale + 0.1F);
            addFeather(1, l, -1,    -0.3F, 8, scale + 0.1F) .pitch = -0.85F;
            addFeather(2, l,  1.8F,  1.3F, 8, scale - 0.1F) .pitch = -0.75F;
            addFeather(3, l,  5,     2,    8, scale)        .pitch = -0.5F;
            addFeather(4, l,  0,   -0.2F,  6, scale + 0.3F);
            addFeather(5, l,  0,     0,    3, scale + 0.19F).pitch = -0.85F;
        }

        private PonyRenderer addFeather(int i, boolean l, float y, float z, int h, float scale) {
            return extended.child(i).around(0, 0, 0).mirror(l).box(-0.5F, y, z, 1, h, 2, scale);
        }

        public void rotateWalking(float swing) {
            folded.yaw = swing * 0.15F;
        }

        public void rotateFlying(float angle) {
            extended.roll = angle;
        }

        public void render(float scale) {
            if (pegasus.wingsAreOpen()) {
                extended.render(scale);
            } else {
                boolean bags = pegasus.isWearing(Wearable.SADDLE_BAGS);
                if (bags) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef(0, 0, 0.198F);
                }
                folded.render(scale);
                if (bags) {
                    GlStateManager.popMatrix();
                }
            }
        }
    }
}
