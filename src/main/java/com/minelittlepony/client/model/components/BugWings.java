package com.minelittlepony.client.model.components;

import net.minecraft.client.model.Model;

import com.minelittlepony.client.util.render.Part;
import com.minelittlepony.model.IPegasus;

@Deprecated
public class BugWings<T extends Model & IPegasus> extends PegasusWings<T> {

    public BugWings(T model, float yOffset, float stretch) {
        super(model, yOffset, stretch);
    }

    @Deprecated
    public void init(float yOffset, float stretch) {
        leftWing = new Wing(pegasus, false, false, yOffset, stretch, 16);
        rightWing = new Wing(pegasus, true, false, yOffset, stretch, 16);
        legacyWing = rightWing;
    }

    public class Wing extends PegasusWings.Wing {

        public Wing(T pegasus, boolean right, boolean legacy, float y, float scale, int texY) {
            super(pegasus, right, legacy, y, scale, texY);
        }

        @Deprecated
        protected void addFeathers(boolean right, boolean l, float rotationPointY, float scale) {
            float r = right ? -1 : 1;

            extended.around((r * (EXT_WING_RP_X - 2)), EXT_WING_RP_Y + rotationPointY, EXT_WING_RP_Z - 2)
                    .mirror(right)
                    .yaw = r * 3;

            Part primary = new Part(pegasus)
                    .tex(56, 16)
                    .mirror(right)
                    .west(r * -0.5F, 0, -7, 16, 8, scale);
            Part secondary = new Part(pegasus)
                    .tex(56, 32)
                    .rotate(-0.5F, r * 0.3F, r / 3)
                    .mirror(right)
                    .west(r, 0, -5, 16, 8, scale);

            extended.child(primary);
            extended.child(secondary);
        }
    }
}
