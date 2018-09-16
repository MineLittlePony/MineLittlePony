package com.minelittlepony.model.components;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPegasus;
import com.minelittlepony.render.model.PlaneRenderer;

import static com.minelittlepony.model.PonyModelConstants.EXT_WING_RP_X;
import static com.minelittlepony.model.PonyModelConstants.EXT_WING_RP_Y;
import static com.minelittlepony.model.PonyModelConstants.EXT_WING_RP_Z;
import static com.minelittlepony.model.PonyModelConstants.HEAD_RP_X;
import static com.minelittlepony.model.PonyModelConstants.ROTATE_90;
import static com.minelittlepony.model.PonyModelConstants.WING_FOLDED_RP_Y;
import static com.minelittlepony.model.PonyModelConstants.WING_FOLDED_RP_Z;

public class ModelBatWing<T extends AbstractPonyModel & IModelPegasus> extends ModelWing<T> {

    public ModelBatWing(T pegasus, boolean right, boolean legacy, float y, float scale, int texX, int texY) {
        super(pegasus, right, legacy, y, scale, texX, texY);
    }

    @Override
    protected void addClosedWing(boolean right, float y, float scale) {
        float x = right ? -4 : 3;

        folded.around(HEAD_RP_X, WING_FOLDED_RP_Y + y - 1, WING_FOLDED_RP_Z - 2)
              .mirror(right)
              .tex(56, 16).box(x, 5, 4, 1, 7, 1, scale)
              .tex(56, 16).box(x, 5, 6, 1, 8, 1, scale)
                          .box(x, 5, 5, 1, 8, 1, scale)
              .tex(56, 16).box(x, 5, 7, 1, 7, 1, scale)
              .rotateAngleX = ROTATE_90;
    }

    @Override
    protected void addFeathers(boolean right, boolean l, float rotationPointY, float scale) {
        float r = right ? -1 : 1;

        extended.around((r * (EXT_WING_RP_X - 2)), EXT_WING_RP_Y + rotationPointY, EXT_WING_RP_Z - 2)
                .mirror(right)
                .rotateAngleY = r * 3;

        extended.child().tex(60, 16)
                .rotate(0.1F, 0, 0)
                .box(-0.5F, 0, 0, 1, 8, 1, scale)
                .child().tex(60, 16)
                    .rotate(-0.5F, 0, 0)
                    .around(0, -1, -2)
                    .box(-0.5F, 0, 2, 1, 7, 1, scale);
        extended.child(0)
                .child().tex(60, 16)
                    .rotate(-0.5F, 0, 0)
                    .around(0, 4, -2.4F)
                    .box(-0.5F, 0, 3, 1, 7, 1, scale);

        PlaneRenderer skin = new PlaneRenderer(pegasus)
                .tex(56, 32);

        extended.child(0).child(skin);

        if (right) {
            skin       .west(0, 0, -7, 16, 8, scale);
        } else {
            skin.flip().west(0, 0, -7, 16, 8, scale);
        }
    }

    @Override
    public void rotateWalking(float swing) {
        folded.rotateAngleY = swing * 0.05F;
    }
}
