package com.minelittlepony.model.components;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPegasus;
import com.minelittlepony.render.model.PlaneRenderer;

public class ModelBugWing<T extends AbstractPonyModel & IModelPegasus> extends ModelWing<T> {

    public ModelBugWing(T pegasus, boolean right, boolean legacy, float y, float scale, int texX, int texY) {
        super(pegasus, right, legacy, y, scale, texX, texY);
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
                .tex(56, 17)
                .mirror(right)
                .west(0, 0, -7, 15, 8, scale);
        PlaneRenderer secondary = new PlaneRenderer(pegasus)
                .tex(56, 32)
                .rotate(-0.5F, r * 0.3F, r / 3)
                .mirror(right)
                .west(0, 0, -7, 15, 8, scale);

        extended.child(primary);
        extended.child(secondary);
    }

    @Override
    public void rotateWalking(float swing) {
        folded.rotateAngleY = swing * 0.05F;
    }

    @Override
    public void render(float scale) {
        if (pegasus.wingsAreOpen()) {
            extended.render(scale);
        }
    }
}
