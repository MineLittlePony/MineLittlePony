package com.minelittlepony.client.model.components;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.util.render.plane.PlaneRenderer;
import com.minelittlepony.model.IPegasus;

public class BugWings<T extends AbstractPonyModel & IPegasus> extends PegasusWings<T> {

    public BugWings(T model, float yOffset, float stretch) {
        super(model, yOffset, stretch);
    }

    @Override
    public void init(float yOffset, float stretch) {
        leftWing = new Wing(pegasus, false, false, yOffset, stretch, 16);
        rightWing = new Wing(pegasus, true, false, yOffset, stretch, 16);
        legacyWing = rightWing;
    }

    public class Wing extends PegasusWings<T>.Wing {

        public Wing(T pegasus, boolean right, boolean legacy, float y, float scale, int texY) {
            super(pegasus, right, legacy, y, scale, texY);
        }

        @Override
        protected void addClosedWing(boolean right, float y, float scale) {

        }

        @Override
        protected void addFeathers(boolean right, boolean l, float rotationPointY, float scale) {
            float r = right ? -1 : 1;

            extended.around((r * (EXT_WING_RP_X - 2)), EXT_WING_RP_Y + rotationPointY, EXT_WING_RP_Z - 2)
                    .mirror(right)
                    .rotateAngleY = r * 3;

            PlaneRenderer primary = new PlaneRenderer(pegasus)
                    .tex(56, 16)
                    .mirror(right)
                    .west(r * -0.5F, 0, -7, 16, 8, scale);
            PlaneRenderer secondary = new PlaneRenderer(pegasus)
                    .tex(56, 32)
                    .rotate(-0.5F, r * 0.3F, r / 3)
                    .mirror(right)
                    .west(r, 0, -5, 16, 8, scale);

            extended.child(primary);
            extended.child(secondary);
        }

        @Override
        public void render(float scale) {
            extended.render(scale);
        }
    }
}
