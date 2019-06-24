package com.minelittlepony.client.model.components;

import net.minecraft.client.model.Model;

import com.minelittlepony.client.util.render.plane.PlaneRenderer;
import com.minelittlepony.model.IPegasus;

import com.mojang.blaze3d.platform.GlStateManager;

import java.util.UUID;

public class BatWings<T extends Model & IPegasus> extends PegasusWings<T> {

    public BatWings(T model, float yOffset, float stretch) {
        super(model, yOffset, stretch);
    }

    @Override
    public void init(float yOffset, float stretch) {
        leftWing = new Wing(pegasus, false, false, yOffset, stretch, 16);
        rightWing = new Wing(pegasus, true, false, yOffset, stretch, 16);
        legacyWing = rightWing;
    }

    @Override
    public void renderPart(float scale, UUID interpolatorId) {

        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.3F, 1.3F, 1.3F);

        super.renderPart(scale, interpolatorId);
        this.init(0, 0);

        GlStateManager.popMatrix();
    }

    public class Wing extends PegasusWings<T>.Wing {

        public Wing(T pegasus, boolean right, boolean legacy, float y, float scale, int texY) {
            super(pegasus, right, legacy, y, scale, texY);
        }

        @Override
        protected void addClosedWing(boolean right, float y, float scale) {
            float x = right ? -3.5F : 3.5F;

            folded.around(HEAD_RP_X - 0.5F, WING_FOLDED_RP_Y + y - 1, WING_FOLDED_RP_Z - 2)
                  .mirror(right)
                  .tex(56, 16).box(x * 0.9F, 5, 4, 1, 4, 1, scale)
                  .tex(56, 16).box(x, 5, 6, 1, 7, 1, scale)
                              .box(x, 5, 5, 1, 6, 1, scale)
                  .tex(56, 16).box(x * 0.9F, 5, 7, 1, 7, 1, scale)
                  .pitch = ROTATE_90;
        }

        @Override
        protected void addFeathers(boolean right, boolean l, float rotationPointY, float scale) {
            float r = right ? -1 : 1;

            extended.around((r * (EXT_WING_RP_X - 2)), EXT_WING_RP_Y + rotationPointY - 1, EXT_WING_RP_Z - 3)
                    .mirror(right)
                    .yaw = r * 3;

            extended.child().tex(60, 16)
                    .mirror(right)  // children are unaware of their parents being mirrored, sadly
                    .rotate(0.1F, 0, 0)
                    .box(-0.5F, -1, 0, 1, 8, 1, scale + 0.001F)  // this was enough to fix z-fighting
                    .child().tex(60, 16)
                        .mirror(right)
                        .rotate(-0.5F, 0, 0)
                        .around(0, -1, -2)
                        .box(-0.5F, 0, 2, 1, 7, 1, scale);
            extended.child(0)
                    .child().tex(60, 16)
                        .mirror(right)
                        .rotate(-0.5F, 0, 0)
                        .around(0, 4, -2.4F)
                        .box(-0.5F, 0, 3, 1, 7, 1, scale);

            PlaneRenderer skin = new PlaneRenderer(pegasus)
                    .tex(56, 32)
                    .mirror(right)
                    .west(0, 0, -7, 16, 8, scale);

            extended.child(0).child(skin);
        }

        @Override
        public void rotateWalking(float swing) {
            folded.yaw = swing * 0.05F;
        }
    }
}
