package com.minelittlepony.model.components;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPegasus;
import com.minelittlepony.render.PonyRenderer;

public class ModelWing {

    private final IModelPegasus pegasus;

    private final PonyRenderer extended;
    private final PonyRenderer folded;

    public <T extends AbstractPonyModel & IModelPegasus> ModelWing(T pegasus, boolean right, float y, float scale, int texX, int texY) {
        this.pegasus = pegasus;

        if (right) {
            texX ++;
        }

        folded = new PonyRenderer(pegasus, 56, texY);
        extended = new PonyRenderer(pegasus, texX, texY + 3);

        addClosedWing(right, y, scale);
        addFeathers(right, y, scale);
    }

    private void addClosedWing(boolean right, float y, float scale) {
        float x = right ? -6 : 4;

        folded.around(HEAD_RP_X, WING_FOLDED_RP_Y + y, WING_FOLDED_RP_Z)
              .box(x, 5, 2, 2, 6, 2, scale)
              .box(x, 5, 4, 2, 8, 2, scale)
              .box(x, 5, 6, 2, 6, 2, scale)
              .rotateAngleX = ROTATE_90;
    }

    private void addFeathers(boolean right, float rotationPointY, float scale) {
        float r = right ? -1 : 1;

        extended.around(r * LEFT_WING_EXT_RP_X, LEFT_WING_EXT_RP_Y + rotationPointY, LEFT_WING_EXT_RP_Z);
        addFeather(0,  6,     0,    8, scale + 0.1F);
        addFeather(1, -1.2F, -0.2F, 8, scale + 0.2F) .rotateAngleX = -0.85F;
        addFeather(2,  1.8F,  1.3F, 8, scale + 0.1F) .rotateAngleX = -0.75F;
        addFeather(3,  5,     2,    8, scale)        .rotateAngleX = -0.5F;
        addFeather(4,  0,   -0.2F,  6, scale + 0.3F);
        addFeather(5,  0,    0.2F,  3, scale + 0.19F).rotateAngleX = -0.85F;
    }

    private PonyRenderer addFeather(int i, float y, float z, int h, float scale) {
        return extended.child(i).around(0, 0, 0).box(0, y, z, 1, h, 2, scale);
    }

    public void rotateWalking(float swing) {
        folded.rotateAngleY = swing * 0.2F;
    }

    public void rotateFlying(float angle) {
        extended.rotateAngleZ = angle;
    }

    public void render(float scale) {
        extended.rotateAngleY = 3;
        if (pegasus.wingsAreOpen()) {
            extended.render(scale);
        } else {
            folded.render(scale);
        }
    }
}
